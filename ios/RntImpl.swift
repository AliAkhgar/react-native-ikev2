//
//  RntImpl.swift
//  RntAplusIkev2
//
//  Created by Ali Akhgar on 1/25/1404 AP.
//

import Foundation
import NetworkExtension
import Security

// Define a protocol for the Calculator Delegate
@objc public protocol RntDelegate {
  func sendEvent(name: String, result:  [String: String])
}

@objc(ConnectionParameters)
public class ConnectionParameters: NSObject { // inherit from NSObject
    @objc public let address: String
    @objc public let username: String
    @objc public let password: String
    @objc public let localizedDescription: String
    @objc public let disconnectOnSleep: Bool
    @objc public let onDemandEnabled: Bool
    @objc public let includeAllNetworks: Bool
    @objc public let excludeLocalNetworks: Bool
    @objc public let excludeCellularServices: Bool
    @objc public let excludeDeviceCommunication: Bool
  

  @objc public init(address: String,
                       username: String,
                       password: String,
                       localizedDescription: String = "", // Provide a default or require a value
                       disconnectOnSleep: Bool = false,
                       onDemandEnabled: Bool = false,
                       includeAllNetworks: Bool = false,
                       excludeLocalNetworks: Bool = false,
                       excludeCellularServices: Bool = false,
                       excludeDeviceCommunication: Bool = false) {
         
         self.address = address
         self.username = username
         self.password = password
         self.localizedDescription = localizedDescription
         self.disconnectOnSleep = disconnectOnSleep
         self.onDemandEnabled = onDemandEnabled
         self.includeAllNetworks = includeAllNetworks
         self.excludeLocalNetworks = excludeLocalNetworks
         self.excludeCellularServices = excludeCellularServices
         self.excludeDeviceCommunication = excludeDeviceCommunication
         
         super.init() // Call the superclass's initializer
     }
}

@objc public class RntImpl: NSObject {
  
  private let vpnManager = NEVPNManager.shared()
  @objc public weak var delegate: RntDelegate? = nil
  
  let kcs = KeychainService(serviceName: "APlusIKev2")
  private var swiftParams:ConnectionParameters?
  
  @objc public func connect(params:ConnectionParameters)-> String{
    swiftParams = params
    self.prepareConnection()
    return ""
  }
  
  
  private func prepareConnection(){
    kcs.save(key: "IKev2Pass", value: self.swiftParams!.password)
    
    //little hack, we add this no first remove profile, make it temp.
    // then we write our new profile.
    // if we dont do this, when switching between protocolTypes, we get connection invalid OR shared secret was not
    // provided from OS.
  
    let protocolIKEv2 = NEVPNProtocolIKEv2()
    protocolIKEv2.passwordReference = kcs.load(key: "IKev2Pass")
    protocolIKEv2.sharedSecretReference = nil
    protocolIKEv2.username = self.swiftParams!.username
    protocolIKEv2.serverAddress = self.swiftParams!.address
    protocolIKEv2.remoteIdentifier = self.swiftParams!.address
    protocolIKEv2.localIdentifier = self.swiftParams!.localizedDescription
    protocolIKEv2.useExtendedAuthentication = true
    protocolIKEv2.disconnectOnSleep = false
    protocolIKEv2.disableMOBIKE = false
    protocolIKEv2.disableRedirect = false
    protocolIKEv2.enableRevocationCheck = false
    protocolIKEv2.useConfigurationAttributeInternalIPSubnet = false
    protocolIKEv2.authenticationMethod = .none
    protocolIKEv2.deadPeerDetectionRate = .medium
    protocolIKEv2.childSecurityAssociationParameters.encryptionAlgorithm = .algorithmAES256GCM
    protocolIKEv2.childSecurityAssociationParameters.integrityAlgorithm = .SHA384
    protocolIKEv2.childSecurityAssociationParameters.diffieHellmanGroup = .group20
    protocolIKEv2.childSecurityAssociationParameters.lifetimeMinutes = 1440
    protocolIKEv2.ikeSecurityAssociationParameters.encryptionAlgorithm = .algorithmAES256GCM
    protocolIKEv2.ikeSecurityAssociationParameters.integrityAlgorithm = .SHA384
    protocolIKEv2.ikeSecurityAssociationParameters.diffieHellmanGroup = .group20
    protocolIKEv2.ikeSecurityAssociationParameters.lifetimeMinutes = 1440
    self.vpnManager.protocolConfiguration = protocolIKEv2
    self.vpnManager.localizedDescription = "VPN"
    self.vpnManager.isOnDemandEnabled = false
    self.vpnManager.isEnabled = false
    
    self.vpnManager.saveToPreferences(completionHandler: {
      error in
      //do not repeat, if error is permission denied.
      if(error?.localizedDescription == "permission denied"){
        return;
      }
      if(error != nil){
        self.prepareConnection()
        return;
      }
      //go to saving.
      self.saveConnection()
    })
  }
  
  
  // Save new profile.
  private func saveConnection(){
      let protocolIKEv2 = NEVPNProtocolIKEv2()
    
      protocolIKEv2.passwordReference = kcs.load(key: "IKev2Pass")
      protocolIKEv2.sharedSecretReference = nil
      protocolIKEv2.username = self.swiftParams!.username
      protocolIKEv2.serverAddress = self.swiftParams!.address
      protocolIKEv2.remoteIdentifier = self.swiftParams!.address
      protocolIKEv2.localIdentifier = self.swiftParams!.localizedDescription
      protocolIKEv2.useExtendedAuthentication = true
      protocolIKEv2.disconnectOnSleep = self.swiftParams!.disconnectOnSleep
    
      /**
        ** -> Plan to Make them as settings [TODO]
       */
      //Start Optional
      protocolIKEv2.disableMOBIKE = false
      protocolIKEv2.disableRedirect = false
      protocolIKEv2.enableRevocationCheck = false
      protocolIKEv2.useConfigurationAttributeInternalIPSubnet = false
      protocolIKEv2.authenticationMethod = .none
      protocolIKEv2.deadPeerDetectionRate = .medium
      //End Optionals
    
      protocolIKEv2.excludeLocalNetworks = self.swiftParams!.excludeLocalNetworks
      if #available(iOS 16.4, *) {
        protocolIKEv2.excludeCellularServices = self.swiftParams!.excludeCellularServices
      } else {
        // Fallback on earlier versions
      }
      if #available(iOS 17.4, *) {
        protocolIKEv2.excludeDeviceCommunication = self.swiftParams!.excludeDeviceCommunication
      } else {
        // Fallback on earlier versions
      }
        /***
            ** > Disabling These Options For Now.
         **/
//      protocolIKEv2.childSecurityAssociationParameters.encryptionAlgorithm = .algorithmAES256GCM
//      protocolIKEv2.childSecurityAssociationParameters.integrityAlgorithm = .SHA384
//      protocolIKEv2.childSecurityAssociationParameters.diffieHellmanGroup = .group20
//      protocolIKEv2.childSecurityAssociationParameters.lifetimeMinutes = 1440
//      protocolIKEv2.ikeSecurityAssociationParameters.encryptionAlgorithm = .algorithmAES256GCM
//      protocolIKEv2.ikeSecurityAssociationParameters.integrityAlgorithm = .SHA384
//      protocolIKEv2.ikeSecurityAssociationParameters.diffieHellmanGroup = .group20
//      protocolIKEv2.ikeSecurityAssociationParameters.lifetimeMinutes = 1440
      self.vpnManager.protocolConfiguration = protocolIKEv2
      self.vpnManager.localizedDescription = self.swiftParams!.localizedDescription
      self.vpnManager.isOnDemandEnabled = self.swiftParams!.onDemandEnabled
      self.vpnManager.isEnabled = true
      
      self.vpnManager.saveToPreferences(completionHandler: {error in
        self.vpnManager.saveToPreferences(completionHandler: {error in
          if(error != nil){
            //retry
            self.saveConnection()
            return;
          }
          self.vpnManager.saveToPreferences(completionHandler: self.vpnSaveHandler)
        })
      })
  }
  
  private var vpnSaveHandler: (Error?) -> Void { return
      { (error:Error?) in
          if (error != nil) {
              self.saveConnection()
              print("Could not save VPN Configurations")
              return
          } else {
            self.vpnManager.loadFromPreferences(completionHandler: {error in
              if(error != nil){
                //retry
                self.saveConnection()
                return;
              }
              
              //start
                do {
                  try self.vpnManager.connection.startVPNTunnel()
                } catch _ {
                  //retry connect
                  do{
                    try self.vpnManager.connection.startVPNTunnel()
                  }catch _ {
                    //retry
                    self.saveConnection()
                    return;
                  }
                }
              //end
            })
          }
      }
  }
  
  @objc public func disconnect()-> String{
    self.vpnManager.connection.stopVPNTunnel()
    return ""
  }
  
  @objc public func getCurrentState()->  String?{
    //Send back state directly.
    return translateNEStatus(status: self.vpnManager.connection.status)["state"] ?? "0"
  }
  
  @objc public func requestCurrentState(){
    self.eventNotifyJS(value: self.translateNEStatus(status: self.vpnManager.connection.status))
  }
  
  @objc public func prepare(){
    vpnManager.loadFromPreferences { (error) in
      if error != nil {
          print(error.debugDescription)
      }
      else{
          print("No error from loading VPN viewDidLoad")
      }
    };
    self.eventNotifyJS(value: self.translateNEStatus(status: self.vpnManager.connection.status))
    NotificationCenter.default.addObserver(forName: NSNotification.Name.NEVPNStatusDidChange, object :nil , queue: nil) {
          notification in let nevpnconn = notification.object as! NEVPNConnection
        self.eventNotifyJS(value: self.translateNEStatus(status: nevpnconn.status) )
      }
  }
  
  @objc func eventNotifyJS(value:  [String: String]) {
    delegate?.sendEvent(name: Event.VPNStateIkev2.rawValue, result: value);
  }
  
  func translateNEStatus( status:NEVPNStatus ) ->  [String: String] {
    var state = ""
      switch status {
      case .connecting:
          state =  "2"
      case .connected:
          state =  "3"
      case .disconnecting:
          state = "1"
      case .disconnected:
          state = "0"
      case .invalid:
        state = "-1"
      case .reasserting:
        state = "2"
      @unknown default:
          state = "0"
      }
    
    return ["state" : state];
  }
    
}

public extension RntImpl {
  // List of emittable events
  enum Event: String, CaseIterable {
    case VPNStateIkev2
  }

  @objc
  static var supportedEvents: [String] {
    return Event.allCases.map(\.rawValue);
  }
}
