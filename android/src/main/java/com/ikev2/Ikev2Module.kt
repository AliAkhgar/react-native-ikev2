package com.ikev2

/**
 * Based on SS
 * versionCode 89
 * versionName "2.5.5"
 */

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.strongswan.android.data.VpnProfile
import org.strongswan.android.data.VpnProfileDataSource
import org.strongswan.android.data.VpnProfileSource
import org.strongswan.android.data.VpnType
import org.strongswan.android.logic.StrongSwanApplication
import org.strongswan.android.logic.VpnStateService
import org.strongswan.android.logic.VpnStateService.ErrorState
import org.strongswan.android.logic.VpnStateService.LocalBinder
import org.strongswan.android.logic.VpnStateService.State
import org.strongswan.android.logic.VpnStateService.VpnStateListener
import java.util.HashMap

/**
 * Why this?
 * we are sending the whole readableMap as Bundle to the CharonVPNService.
 * so we want to persist androidOptions.Notification readableMap keys
 * to reference them from other places. this helps mismatch and time-cost.
 */
object NotificationBundle {
  const val BUNDLE_KEY:String = "Notification" // key of bundle sent to vpnService. Not key sent from JS side
  //Keys from JS-SIDE
  const val ACTIVITY_PACKAGE_NAME:String = "openActivityPackageName"
  const val SHOW_DISCONNECT:String = "showDisconnectAction"
  const val SHOW_PAUSE:String = "showPauseAction"
  const val SHOW_TIMER:String = "showTimer"
  const val TITLE_ACTION_DISCONNECT:String = "titleDisconnectButton"
  const val TITLE_CONNECTING:String = "titleConnecting"
  const val TITLE_CONNECTED:String = "titleConnected"
  const val TITLE_DISCONNECTING:String = "titleDisconnecting"
  const val TITLE_DISCONNECTED:String = "titleDisconnected"
  const val TITLE_ERROR:String = "titleError"
}

@ReactModule(name = Ikev2Module.NAME)
class Ikev2Module(reactContext: ReactApplicationContext) :
  NativeIkev2Spec(reactContext),ActivityEventListener, ServiceConnection, VpnStateListener {

  companion object {
    const val NAME = "Ikev2"
    const val EVENT_NAME = "VPNStateIkev2"
    const val PREPARE_VPN_PROFILE = 110
  }
  override fun getName(): String {
    return NAME
  }


  private fun bindService(){
    val vpnStateServiceIntent = Intent(
      reactApplicationContext,
      VpnStateService::class.java
    )
    reactApplicationContext.bindService(vpnStateServiceIntent, this, Service.BIND_AUTO_CREATE)
  }

  init {
    reactApplicationContext.addActivityEventListener(this)
    // Load charon bridge
    StrongSwanApplication(reactApplicationContext)
    System.loadLibrary("androidbridge")
    bindService()
  }


  /**
   * VPN CONTROL RELATED
   */

  var preparePromise: Promise? = null;
  override fun prepare(promise: Promise?) {
    if (currentActivity == null) {
      promise!!.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
      return
    }
    preparePromise = promise
    val intent = VpnService.prepare(reactApplicationContext);
    if (intent != null) {
      reactApplicationContext.startActivityForResult(intent, PREPARE_VPN_PROFILE,null)
    } else {
      reactApplicationContext.onActivityResult(reactApplicationContext.currentActivity,
        Activity.RESULT_OK, PREPARE_VPN_PROFILE,null);
    }
  }

  override fun isPrepared(promise: Promise?) {
    val intent = VpnService.prepare(currentActivity);
    if(intent==null){
      promise?.resolve(true)
    }else {
      promise?.resolve(false)
    }
  }


  private var profileToConnectAfterPrepare:Bundle? = null
  override fun connect(params: ReadableMap?, promise: Promise?) {
    try{
      clearAllProfiles()
      if(params==null){
        promise!!.reject("E_PARAMS_ERR", "ConnectionParams empty error.")
        return
      }
      if (vpnStateService == null) {
        promise!!.reject("E_SERVICE_NOT_STARTED", "Service not started yet")
        return
      }
      if (currentActivity == null) {
        promise!!.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
        return
      }
      //Open Datasource, populate profile, save.
      val mDataSource = VpnProfileSource(reactApplicationContext);
      mDataSource.open();
      var mProfile = VpnProfile()
      mProfile = populateVpnProfile(mProfile, params)
      val insertedProfile = mDataSource.insertProfile(mProfile)
      mDataSource.close()

      //create connect bundle
      val profileInfo = Bundle()
      profileInfo.putString(VpnProfileDataSource.KEY_UUID,insertedProfile.uuid.toString())
      profileInfo.putString(VpnProfileDataSource.KEY_PASSWORD, params.getString("password"))
      /**
       * Redundacny, [TODO]
       * get androidOptions, ship it with bundle, get it in CharonVPNService
       */
      val androidOptions = params.getMap("androidOptions")!!
      val notificationOptions = androidOptions.getMap("Notification")?.toHashMap()
      profileInfo.putBundle(NotificationBundle.BUNDLE_KEY,
        convertMixedMapToBundle(notificationOptions)
      )


      val intent = VpnService.prepare(currentActivity)
      if (intent != null) {
        //if not prepared, send bundle and prepare.
        profileToConnectAfterPrepare = profileInfo
        reactApplicationContext.startActivityForResult(intent, PREPARE_VPN_PROFILE,null)
      }else{
        //if prepared, connect straight.
        vpnStateService!!.connect(profileInfo, true)
      }

      promise?.resolve(null)
    }catch (e:Exception){
      promise?.reject("E_Connection_Process",e.message)
    }
  }

  private fun connectAfterPrepared(){
    vpnStateService!!.connect(profileToConnectAfterPrepare, true)
    profileToConnectAfterPrepare = null
  }

  override fun disconnect(promise: Promise?) {
    if (vpnStateService != null) {
      vpnStateService!!.disconnect()
    }
    promise!!.resolve(true)
  }


  /**
   * CONNECTION-STATE RELATED
   */
  override fun getCurrentState(promise: Promise?) {
    if (vpnStateService == null) {
      promise!!.resolve(VpnState.VPN_STATE_DISCONNECTED.value)
      return;
    }

    val errorState: ErrorState = vpnStateService!!.errorState
    if (errorState == ErrorState.NO_ERROR) {
      promise!!.resolve(translateVPNState(vpnStateService!!.state))
    } else {
      promise!!.resolve(VpnState.VPN_STATE_ERROR.value)
    }

  }

  override fun requestCurrentState(promise: Promise?) {
    postStateToJS()
    promise!!.resolve(null)
  }


  /**
   * VPN STATE RELATED
   */
  override fun addListener(eventType: String?) {}
  override fun removeListeners(count: Double) {}
  fun sendEvent(eventName: String, params: WritableMap?) {
    reactApplicationContext.getJSModule<DeviceEventManagerModule.RCTDeviceEventEmitter>(
      DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
    ).emit(eventName, params)
  }




  /**
   * Activity Event Listener Section,
   * After prepared, we trigger connect automatically.
   */
  override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == PREPARE_VPN_PROFILE) {
      if (preparePromise != null) {
        if (resultCode == RESULT_OK) {
          preparePromise!!.resolve(true)
        } else {
          preparePromise!!.resolve(false)
        }
        preparePromise = null
      }

      // connectAfterPrepare
      if(profileToConnectAfterPrepare!=null){
        //connect
        connectAfterPrepared()
      }
    }
  }

  override fun onNewIntent(p0: Intent?) {
  }


  var vpnStateService: VpnStateService? = null
  override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
    vpnStateService = (p1 as LocalBinder).service
    if (vpnStateService != null) {
      vpnStateService!!.registerListener(this)
      //send state to js on service connected.
      postStateToJS()
    }
  }

  override fun onServiceDisconnected(p0: ComponentName?) {
    vpnStateService = null
  }

  override fun stateChanged() {
    postStateToJS()
  }

  fun postStateToJS(){
    val params = Arguments.createMap()
    //If no service Attached, Post Disconnected.
    if (vpnStateService == null) {
      params.putString("state", VpnState.VPN_STATE_DISCONNECTED.value)
      sendEvent(EVENT_NAME,params);
      return
    }

    //First check error State, if no error, send state
    val errorState = vpnStateService?.errorState
    if (errorState == ErrorState.NO_ERROR) {
      val state = translateVPNState(vpnStateService!!.state)
      params.putString("state", state)
    } else {
      //if error, send error state
      params.putString("state", VpnState.VPN_STATE_ERROR.value)
      Log.d("VXXT",vpnStateService?.errorState!!.name)
    }
    sendEvent(EVENT_NAME,params);
  }

  private enum class VpnState(val value: String) {
    VPN_STATE_DISCONNECTED("0"),
    VPN_STATE_CONNECTING("2"),
    VPN_STATE_CONNECTED("3"),
    VPN_STATE_DISCONNECTING("1"),
    VPN_STATE_OTHER("-1"),
    VPN_STATE_ERROR("-2");

    companion object {
      fun fromValue(value: String): VpnState? = entries.find { it.value == value }
    }
  }

  private fun translateVPNState(level: State): String {
    val state: VpnState = when (level) {
      State.DISABLED -> VpnState.VPN_STATE_DISCONNECTED
      State.CONNECTING -> VpnState.VPN_STATE_CONNECTING
      State.CONNECTED -> VpnState.VPN_STATE_CONNECTED
      State.DISCONNECTING -> VpnState.VPN_STATE_DISCONNECTING
      else -> VpnState.VPN_STATE_OTHER
    }
    return state.value;
  }


  private fun clearAllProfiles(){
    val mDataSource = VpnProfileSource(reactApplicationContext);
    mDataSource.open();
    mDataSource.allVpnProfiles.forEach { vpnProfile: VpnProfile? ->
      run {
        mDataSource.deleteVpnProfile(vpnProfile)
      }
    }
    mDataSource.close()
  }


  private fun populateVpnProfile(mProfile:VpnProfile,params:ReadableMap): VpnProfile{
    val androidOptions = params.getMap("androidOptions")!!

    //Connection Essentials
    mProfile.name = androidOptions.getString("connectionName")
    mProfile.gateway = params.getString("address")
    mProfile.username = params.getString("username")
    mProfile.password = params.getString("password")

    val authType = androidOptions.getString("AuthType")
    if("ikev2-eap".equals(authType)){
      mProfile.vpnType = VpnType.IKEV2_EAP
    }else if("ikev2-byod-eap".equals(authType)){
      mProfile.vpnType = VpnType.IKEV2_BYOD_EAP
    }

    //Optional
    if(androidOptions.hasKey("MTU")){
      mProfile.mtu = androidOptions.getInt("MTU")
    }
    if(androidOptions.hasKey("DnsServers")){
      mProfile.dnsServers = androidOptions.getString("DnsServers")
    }
    if(androidOptions.hasKey("NatKeepAlive")){
      mProfile.natKeepAlive = androidOptions.getInt("NatKeepAlive")
    }
    //Flags
    var profileFlags = 0
    if(androidOptions.hasKey("sendCertificateRequest")){
      val flg = if (!androidOptions.getBoolean("sendCertificateRequest")) VpnProfile.FLAGS_SUPPRESS_CERT_REQS else 0
      profileFlags = profileFlags or flg
    }
    if(androidOptions.hasKey("checkCerificateWithOCSP")){
      val flg = if (!androidOptions.getBoolean("checkCerificateWithOCSP")) VpnProfile.FLAGS_DISABLE_OCSP else 0
      profileFlags = profileFlags or flg
    }
    if(androidOptions.hasKey("checkCertificateWithCRLs")){
      val flg = if (!androidOptions.getBoolean("checkCertificateWithCRLs")) VpnProfile.FLAGS_DISABLE_CRL else 0
      profileFlags = profileFlags or flg
    }
    mProfile.flags = profileFlags


    if(androidOptions.hasKey("customSubnets")){
      mProfile.includedSubnets = androidOptions.getString("customSubnets")
    }
    if(androidOptions.hasKey("excludeSubnets")){
      mProfile.excludedSubnets = androidOptions.getString("excludeSubnets")
    }

    if(androidOptions.hasKey("selectedAppsPackageNames")){
      val apps = androidOptions.getArray("selectedAppsPackageNames")?.toArrayList()?.joinToString(" ")
      mProfile.selectedApps = apps
    }
    if(androidOptions.hasKey("allAppsUseVPN")){
      val allAppsUseVPN = androidOptions.getBoolean("allAppsUseVPN")
      if(allAppsUseVPN){
        mProfile.selectedAppsHandling = VpnProfile.SelectedAppsHandling.SELECTED_APPS_DISABLE
      }else if(androidOptions.hasKey("allowOnlySelectedAppsUseVPN")){
        //if allApps is false, set the option
        if(androidOptions.getBoolean("allowOnlySelectedAppsUseVPN")){
          mProfile.selectedAppsHandling = VpnProfile.SelectedAppsHandling.SELECTED_APPS_ONLY
        }else{
          mProfile.selectedAppsHandling = VpnProfile.SelectedAppsHandling.SELECTED_APPS_EXCLUDE
        }
      }
    }

    return mProfile
  }

}
