# Android Manifest Templates

This directory contains example manifest files and configuration templates for integrating the React Native IKEv2 library into your Android application.

## Complete AndroidManifest.xml Example

The following is a complete example of an AndroidManifest.xml file with all required permissions and services for the IKEv2 VPN library:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Required Permissions for IKEv2 VPN -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    
    <application
        android:name=".MainApplication"
        android:label="@string/app_name"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:allowBackup="false"
        android:theme="@style/AppTheme"
        android:supportsRtl="true">
        
        <!-- Your main activity -->
        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|screenSize|smallestScreenSize|uiMode"
            android:launchMode="singleTask"
            android:windowSoftInputMode="adjustResize"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- Required VPN State Service -->
        <service
            android:name="org.strongswan.android.logic.VpnStateService"
            android:exported="false">
        </service>
        
        <!-- Required Main VPN Service -->
        <service
            android:name="org.strongswan.android.logic.CharonVpnService"
            android:exported="false"
            android:foregroundServiceType="specialUse"
            android:permission="android.permission.BIND_VPN_SERVICE">
            <intent-filter>
                <action android:name="android.net.VpnService" />
            </intent-filter>
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="VpnService instance"/>
        </service>
        
        <!-- Required VPN Profile Control Activity -->
        <activity
            android:name="org.strongswan.android.ui.VpnProfileControlActivity"
            android:theme="@style/TransparentActivity"
            android:taskAffinity=""
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:exported="true">
            <intent-filter>
                <action android:name="org.strongswan.android.action.START_PROFILE" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="org.strongswan.android.action.DISCONNECT" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```

## Required Styles

Add this to your `android/app/src/main/res/values/styles.xml`:

```xml
<style name="TransparentActivity" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="android:windowIsTranslucent">true</item>
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowIsFloating">true</item>
    <item name="android:backgroundDimEnabled">false</item>
</style>
```

## Integration Checklist

- [ ] All permissions added to AndroidManifest.xml
- [ ] All services declared in AndroidManifest.xml
- [ ] VpnProfileControlActivity added with correct intent filters
- [ ] TransparentActivity style added to styles.xml
- [ ] POST_NOTIFICATIONS permission requested at runtime (Android 13+)
- [ ] VPN preparation called before connection attempts

## Troubleshooting

If you encounter issues:

1. **Service Not Found**: Ensure all service names match exactly as shown above
2. **Permission Denied**: Verify all permissions are declared in manifest
3. **Notification Issues**: Ensure POST_NOTIFICATIONS permission is requested at runtime
4. **VPN Not Prepared**: Call `IKev2.prepare()` before connecting