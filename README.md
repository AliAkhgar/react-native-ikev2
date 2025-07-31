# 🔐 React Native IKEv2

<div align="center">

![IKEv2 Logo](https://img.shields.io/badge/IKEv2-VPN-blue?style=for-the-badge&logo=shield&logoColor=white)
![React Native](https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

**A powerful and secure IKEv2 VPN implementation for React Native applications**

[![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=flat-square&logo=typescript&logoColor=white)](https://typescriptlang.org/)
[![TurboModule](https://img.shields.io/badge/TurboModule-Ready-green?style=flat-square)](https://reactnative.dev/)
[![MIT License](https://img.shields.io/badge/License-Exclusive-red?style=flat-square)](LICENSE)

</div>

---

## 📖 Overview

React Native IKEv2 is a comprehensive VPN solution that implements the IKEv2 (Internet Key Exchange version 2) protocol for React Native applications. This library provides secure, high-performance VPN connectivity with extensive customization options for both iOS and Android platforms.

### 🌟 Key Features

- **🔒 Secure IKEv2 Protocol**: Industry-standard VPN protocol with robust encryption
- **📱 Cross-Platform**: Native implementations for both iOS and Android
- **⚡ TurboModule Architecture**: High-performance native bridge using React Native's New Architecture
- **🎛️ Rich Configuration**: Extensive options for connection, authentication, and notifications
- **📊 State Management**: Real-time connection state monitoring and event handling
- **🔔 Smart Notifications**: Customizable VPN status notifications with actions
- **🚫 No Network Extension Required**: iOS implementation doesn't require network extension setup
- **🎯 App-Specific Routing**: Android support for per-app VPN routing

### 🔍 About IKEv2 Protocol

IKEv2 (Internet Key Exchange version 2) is a modern VPN protocol that offers:

- **High Security**: Strong encryption and authentication mechanisms
- **Fast Connection**: Quick establishment and re-establishment of VPN tunnels
- **Mobile Optimization**: Excellent for mobile devices with automatic reconnection
- **NAT Traversal**: Built-in support for connections behind NAT/firewalls
- **Dead Peer Detection**: Automatic detection and recovery from connection failures

---

## 📦 Installation

> **⚠️ Important**: This library is **not available on npm registry**. It must be installed locally by cloning the repository.

### Prerequisites

- React Native 0.79.0 or higher
- iOS 13.0+ / Android API level 21+
- Node.js 16+ and npm/yarn

### Local Installation Steps

1. **Clone the repository:**
```bash
git clone https://github.com/AliAkhgar/react-native-ikev2.git
```

2. **Install in your React Native project:**
```bash
cd your-react-native-project
npm install ./path/to/react-native-ikev2
```

3. **Install dependencies:**
```bash
npm install
# or
yarn install
```

4. **For iOS - Install pods:**
```bash
cd ios && pod install
```

---

## 🍎 iOS Setup

The iOS implementation uses the native NetworkExtension framework and **does not require** a separate Network Extension app target.

### Required Capabilities

Add the following capabilities to your iOS app in Xcode:

1. **Personal VPN** - Required for VPN functionality
2. **Network Extensions** - For VPN tunnel management

### Entitlements

Ensure your `Entitlements.plist` includes:

```xml
<key>com.apple.developer.networking.vpn.api</key>
<array>
    <string>allow-vpn</string>
</array>
```

### Info.plist Configuration

No additional Info.plist configuration is required for basic functionality.

### iOS Implementation Notes

- ✅ **No Network Extension Required**: Unlike other VPN implementations, this library handles VPN connections directly within your main app
- ✅ **Native Performance**: Direct integration with iOS NetworkExtension framework
- ✅ **Background Support**: Maintains connection when app is backgrounded
- ✅ **On-Demand Connection**: Support for automatic VPN activation

---

## 🤖 Android Setup

The Android implementation uses the native VpnService and requires specific permissions and service declarations.

### Required Permissions

Add these permissions to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Required Services

Add these service declarations inside the `<application>` tag:

```xml
<application>
    <!-- Your existing application content -->
    
    <!-- VPN State Service -->
    <service
        android:name="org.strongswan.android.logic.VpnStateService"
        android:exported="false">
    </service>
    
    <!-- Main VPN Service -->
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
    
    <!-- VPN Profile Control Activity -->
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
```

### Example Complete AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Required Permissions -->
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
        
        <!-- VPN Services -->
        <service
            android:name="org.strongswan.android.logic.VpnStateService"
            android:exported="false">
        </service>
        
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

---

## 🚀 Usage

### Basic Example

```typescript
import React, { useEffect, useState } from 'react';
import { Alert, PermissionsAndroid, Platform } from 'react-native';
import * as IKev2 from 'react-native-ikev2';

const VPNExample = () => {
  const [vpnState, setVPNState] = useState<IKev2.ConnectionState>();
  const [isPrepared, setIsPrepared] = useState(false);

  useEffect(() => {
    // Request notification permission on Android
    if (Platform.OS === 'android') {
      PermissionsAndroid.request('android.permission.POST_NOTIFICATIONS');
    }

    // Check if VPN is prepared
    IKev2.isPrepared().then(setIsPrepared);

    // Listen for VPN state changes
    const subscription = IKev2.addIKev2StateChangeListener((state) => {
      setVPNState(state.state);
    });

    // Request current state
    IKev2.requestCurrentState();

    return () => subscription.remove();
  }, []);

  const connectVPN = async () => {
    try {
      await IKev2.connect({
        address: 'your-vpn-server.com',
        username: 'your-username',
        password: 'your-password',
        
        iOSOptions: {
          localizedDescription: 'My VPN Connection',
          disconnectOnSleep: false,
          onDemandEnabled: false,
        },
        
        androidOptions: {
          connectionName: 'My VPN',
          AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
          Notification: {
            openActivityPackageName: 'com.yourapp.MainActivity',
            titleConnected: 'VPN Connected',
            showDisconnectAction: true,
            titleDisconnectButton: 'Disconnect',
          },
        },
      });
    } catch (error) {
      Alert.alert('Connection Error', error.toString());
    }
  };

  const disconnectVPN = () => {
    IKev2.disconnect();
  };

  return (
    // Your UI components here
  );
};
```

---

## 📚 API Reference

### Core Methods

#### `prepare(): Promise<boolean>`
**Android Only** - Prepares the VPN service for connection.

```typescript
const isReady = await IKev2.prepare();
```

#### `isPrepared(): Promise<boolean>`
**Android Only** - Checks if the VPN service is prepared.

```typescript
const prepared = await IKev2.isPrepared();
```

#### `connect(params: ConnectionParams): Promise<void>`
Establishes a VPN connection with the specified parameters.

```typescript
await IKev2.connect({
  address: 'vpn.example.com',
  username: 'user123',
  password: 'password123',
  iOSOptions: { /* iOS options */ },
  androidOptions: { /* Android options */ }
});
```

#### `disconnect(): Promise<void>`
Disconnects the current VPN connection.

```typescript
await IKev2.disconnect();
```

#### `getCurrentState(): Promise<ConnectionState>`
Retrieves the current VPN connection state.

```typescript
const state = await IKev2.getCurrentState();
```

#### `requestCurrentState(): Promise<void>`
Requests the current state and triggers the state change listener.

```typescript
await IKev2.requestCurrentState();
```

### State Management

#### `addIKev2StateChangeListener(callback): Subscription`
Subscribes to VPN state changes.

```typescript
const subscription = IKev2.addIKev2StateChangeListener((state) => {
  console.log('VPN State:', state.state);
});

// Don't forget to remove the listener
subscription.remove();
```

### Connection States

```typescript
enum ConnectionState {
  DISCONNECTED = '0',
  DISCONNECTING = '1', 
  CONNECTING = '2',
  CONNECTED = '3',
  INVALID = '-1',
  ERROR = '-2'
}
```

---

## ⚙️ Configuration Options

### ConnectionParams

```typescript
interface ConnectionParams {
  address: string;           // VPN server address
  username: string;          // Authentication username
  password: string;          // Authentication password
  iOSOptions: IOSConnectionOptions;
  androidOptions: AndroidConnectionOptions;
}
```

### iOS Options

```typescript
interface IOSConnectionOptions {
  localizedDescription: string;    // VPN profile description
  disconnectOnSleep: boolean;      // Disconnect when device sleeps
  onDemandEnabled: boolean;        // Enable on-demand connection
  includeAllNetworks?: boolean;    // Route all network traffic
  excludeLocalNetworks?: boolean;  // Exclude local network traffic
  excludeCellularServices?: boolean; // Exclude cellular services
  excludeDeviceCommunication?: boolean; // Exclude device communication
}
```

### Android Options

```typescript
interface AndroidConnectionOptions {
  connectionName: string;           // Display name for the connection
  AuthType: AndroidAuthType;        // Authentication type
  Notification: AndroidNotificationOptions;
  
  // Optional configurations
  MTU?: number;                     // Maximum Transmission Unit (default: 1400)
  DnsServers?: string;              // Custom DNS servers (space-separated)
  NatKeepAlive?: number;            // NAT keep-alive interval (default: 45s)
  
  // Certificate validation
  sendCertificateRequest?: boolean;      // Send certificate request (default: true)
  checkCerificateWithOCSP?: boolean;     // OCSP certificate validation (default: true)
  checkCertificateWithCRLs?: boolean;    // CRL certificate validation (default: true)
  
  // Traffic routing
  customSubnets?: string;                // Only route these subnets through VPN
  excludeSubnets?: string;               // Exclude these subnets from VPN
  
  // App-specific VPN
  allAppsUseVPN?: boolean;               // All apps use VPN (default: true)
  allowOnlySelectedAppsUseVPN?: boolean; // Only selected apps use VPN
  selectedAppsPackageNames?: string[];   // Package names of selected apps
}
```

### Authentication Types

```typescript
enum AndroidAuthType {
  IKEv2_EAP = 'ikev2-eap',           // Standard EAP authentication
  IKEv2_BYOD_EAP = 'ikev2-byod-eap'  // BYOD EAP authentication
}
```

### Notification Options

```typescript
interface AndroidNotificationOptions {
  openActivityPackageName: string;    // Package name to open on notification tap
  
  // Notification actions
  showDisconnectAction?: boolean;     // Show disconnect button
  titleDisconnectButton?: string;     // Disconnect button text
  showPauseAction?: boolean;          // Show pause button
  showTimer?: boolean;                // Show connection timer
  
  // Status messages
  titleConnecting?: string;           // Connecting status text
  titleConnected?: string;            // Connected status text
  titleDisconnecting?: string;        // Disconnecting status text
  titleDisconnected?: string;         // Disconnected status text
  titleError?: string;                // Error status text
}
```

---

## 🎯 Advanced Usage Examples

### Complete VPN Implementation

```typescript
import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Alert,
  StyleSheet,
  PermissionsAndroid,
  Platform,
} from 'react-native';
import * as IKev2 from 'react-native-ikev2';

const AdvancedVPN = () => {
  const [vpnState, setVPNState] = useState<IKev2.ConnectionState>(
    IKev2.ConnectionState.DISCONNECTED
  );
  const [isPrepared, setIsPrepared] = useState(false);
  const [isConnecting, setIsConnecting] = useState(false);

  // Initialize VPN
  useEffect(() => {
    initializeVPN();
  }, []);

  const initializeVPN = useCallback(async () => {
    try {
      // Request permissions on Android
      if (Platform.OS === 'android') {
        await PermissionsAndroid.request('android.permission.POST_NOTIFICATIONS');
        const prepared = await IKev2.isPrepared();
        setIsPrepared(prepared);
      } else {
        setIsPrepared(true); // iOS doesn't need preparation
      }

      // Set up state listener
      const subscription = IKev2.addIKev2StateChangeListener((state) => {
        setVPNState(state.state);
        setIsConnecting(state.state === IKev2.ConnectionState.CONNECTING);
      });

      // Get current state
      await IKev2.requestCurrentState();

      return () => subscription.remove();
    } catch (error) {
      Alert.alert('Initialization Error', error.toString());
    }
  }, []);

  const prepareVPN = useCallback(async () => {
    if (Platform.OS === 'android' && !isPrepared) {
      try {
        const result = await IKev2.prepare();
        setIsPrepared(result);
        if (!result) {
          Alert.alert('Preparation Failed', 'Failed to prepare VPN service');
        }
      } catch (error) {
        Alert.alert('Preparation Error', error.toString());
      }
    }
  }, [isPrepared]);

  const connectVPN = useCallback(async () => {
    if (Platform.OS === 'android' && !isPrepared) {
      await prepareVPN();
      return;
    }

    setIsConnecting(true);
    
    try {
      await IKev2.connect({
        address: 'your-server.example.com',
        username: 'your-username',
        password: 'your-password',
        
        iOSOptions: {
          localizedDescription: 'Secure VPN Connection',
          disconnectOnSleep: false,
          onDemandEnabled: false,
          includeAllNetworks: true,
          excludeLocalNetworks: false,
        },
        
        androidOptions: {
          connectionName: 'Secure VPN',
          AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
          MTU: 1400,
          DnsServers: '8.8.8.8 8.8.4.4',
          NatKeepAlive: 45,
          
          // Certificate validation
          sendCertificateRequest: true,
          checkCerificateWithOCSP: true,
          checkCertificateWithCRLs: true,
          
          // Route all apps through VPN
          allAppsUseVPN: true,
          
          Notification: {
            openActivityPackageName: 'com.yourapp.MainActivity',
            showDisconnectAction: true,
            titleDisconnectButton: 'Disconnect VPN',
            showTimer: true,
            titleConnecting: 'Connecting to VPN...',
            titleConnected: 'VPN Connected - Secure',
            titleDisconnecting: 'Disconnecting VPN...',
            titleDisconnected: 'VPN Disconnected',
            titleError: 'VPN Connection Error',
          },
        },
      });
    } catch (error) {
      setIsConnecting(false);
      Alert.alert('Connection Error', error.toString());
    }
  }, [isPrepared, prepareVPN]);

  const disconnectVPN = useCallback(async () => {
    try {
      await IKev2.disconnect();
    } catch (error) {
      Alert.alert('Disconnection Error', error.toString());
    }
  }, []);

  const getStateText = (state: IKev2.ConnectionState): string => {
    switch (state) {
      case IKev2.ConnectionState.DISCONNECTED:
        return 'Disconnected';
      case IKev2.ConnectionState.CONNECTING:
        return 'Connecting...';
      case IKev2.ConnectionState.CONNECTED:
        return 'Connected';
      case IKev2.ConnectionState.DISCONNECTING:
        return 'Disconnecting...';
      case IKev2.ConnectionState.ERROR:
        return 'Error';
      default:
        return 'Unknown';
    }
  };

  const getStateColor = (state: IKev2.ConnectionState): string => {
    switch (state) {
      case IKev2.ConnectionState.CONNECTED:
        return '#4CAF50';
      case IKev2.ConnectionState.CONNECTING:
      case IKev2.ConnectionState.DISCONNECTING:
        return '#FF9800';
      case IKev2.ConnectionState.ERROR:
        return '#F44336';
      default:
        return '#757575';
    }
  };

  const isConnected = vpnState === IKev2.ConnectionState.CONNECTED;
  const canConnect = isPrepared && !isConnecting && !isConnected;
  const canDisconnect = isConnected || vpnState === IKev2.ConnectionState.CONNECTING;

  return (
    <View style={styles.container}>
      <Text style={styles.title}>VPN Status</Text>
      
      <View style={[styles.statusContainer, { borderColor: getStateColor(vpnState) }]}>
        <Text style={[styles.statusText, { color: getStateColor(vpnState) }]}>
          {getStateText(vpnState)}
        </Text>
      </View>

      {Platform.OS === 'android' && !isPrepared && (
        <TouchableOpacity style={styles.prepareButton} onPress={prepareVPN}>
          <Text style={styles.buttonText}>Prepare VPN</Text>
        </TouchableOpacity>
      )}

      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.button, styles.connectButton, !canConnect && styles.disabledButton]}
          onPress={connectVPN}
          disabled={!canConnect}
        >
          <Text style={styles.buttonText}>
            {isConnecting ? 'Connecting...' : 'Connect'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.disconnectButton, !canDisconnect && styles.disabledButton]}
          onPress={disconnectVPN}
          disabled={!canDisconnect}
        >
          <Text style={styles.buttonText}>Disconnect</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
    color: '#333',
  },
  statusContainer: {
    padding: 20,
    borderRadius: 10,
    borderWidth: 2,
    marginBottom: 30,
    backgroundColor: 'white',
  },
  statusText: {
    fontSize: 18,
    fontWeight: '600',
    textAlign: 'center',
  },
  prepareButton: {
    backgroundColor: '#2196F3',
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderRadius: 8,
    marginBottom: 20,
  },
  buttonContainer: {
    flexDirection: 'row',
    gap: 15,
  },
  button: {
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderRadius: 8,
    minWidth: 100,
  },
  connectButton: {
    backgroundColor: '#4CAF50',
  },
  disconnectButton: {
    backgroundColor: '#F44336',
  },
  disabledButton: {
    backgroundColor: '#CCCCCC',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
    textAlign: 'center',
  },
});

export default AdvancedVPN;
```

### App-Specific VPN (Android)

```typescript
// Configure VPN to only affect specific apps
const connectWithAppRouting = async () => {
  await IKev2.connect({
    address: 'vpn.example.com',
    username: 'user',
    password: 'pass',
    
    iOSOptions: {
      localizedDescription: 'App-Specific VPN',
      disconnectOnSleep: false,
      onDemandEnabled: false,
    },
    
    androidOptions: {
      connectionName: 'App VPN',
      AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
      
      // Disable VPN for all apps by default
      allAppsUseVPN: false,
      
      // Only allow specific apps to use VPN
      allowOnlySelectedAppsUseVPN: true,
      selectedAppsPackageNames: [
        'com.android.chrome',
        'com.whatsapp',
        'com.spotify.music',
      ],
      
      Notification: {
        openActivityPackageName: 'com.yourapp.MainActivity',
        titleConnected: 'App VPN Active',
      },
    },
  });
};
```

---

## 🔧 Troubleshooting

### Common Issues

#### iOS Issues

**Problem**: VPN connection fails immediately
- **Solution**: Ensure your app has the Personal VPN capability enabled in Xcode
- **Check**: Verify entitlements are properly configured

**Problem**: App crashes when connecting
- **Solution**: Make sure you're not trying to connect multiple times simultaneously
- **Check**: Always check current state before attempting connection

#### Android Issues

**Problem**: "VPN service not prepared" error
- **Solution**: Call `IKev2.prepare()` before attempting to connect
- **Check**: Ensure all required permissions are declared in AndroidManifest.xml

**Problem**: Notifications not showing
- **Solution**: Request POST_NOTIFICATIONS permission on Android 13+
- **Check**: Verify notification configuration in androidOptions

**Problem**: Services not found
- **Solution**: Ensure all required services are declared in AndroidManifest.xml
- **Check**: Verify service names match exactly as shown in documentation

### Debug Tips

1. **Enable Debug Logging**:
```typescript
// Check current state before operations
const currentState = await IKev2.getCurrentState();
console.log('Current VPN State:', currentState);
```

2. **State Monitoring**:
```typescript
IKev2.addIKev2StateChangeListener((state) => {
  console.log('State changed to:', state.state);
  // Log all state changes for debugging
});
```

3. **Error Handling**:
```typescript
try {
  await IKev2.connect(params);
} catch (error) {
  console.error('Connection failed:', error);
  // Log full error details
}
```

---

## 📄 License

**🚫 PROPRIETARY LICENSE - NOT OPEN SOURCE**

This library is **exclusively licensed** and is **NOT open source**. All rights reserved.

- ❌ **No Public Distribution**: This library may not be redistributed or published
- ❌ **No Modification**: Source code may not be modified without permission  
- ❌ **Commercial Use Restricted**: Commercial usage requires explicit licensing agreement
- ❌ **No Warranty**: Provided "as-is" without any warranty or support guarantee

For licensing inquiries and commercial usage rights, please contact:

**Ali Akhgar**  
📧 Email: [theakhgar@gmail.com](mailto:theakhgar@gmail.com)  
🔗 GitHub: [@AliAkhgar](https://github.com/AliAkhgar)

---

## 🤝 Support

This library is provided as-is with limited support. For technical issues:

1. **Check Documentation**: Review this comprehensive guide first
2. **Example Implementation**: Study the included example app
3. **Issue Reporting**: Contact the author for critical bugs only

### Contact Information

- **Author**: Ali Akhgar
- **Email**: theakhgar@gmail.com
- **Repository**: https://github.com/AliAkhgar/react-native-ikev2

---

<div align="center">

**⚡ Built with React Native TurboModules for maximum performance**

![Made with ❤️](https://img.shields.io/badge/Made%20with-❤️-red?style=flat-square)
![TypeScript](https://img.shields.io/badge/TypeScript-Ready-blue?style=flat-square)
![iOS](https://img.shields.io/badge/iOS-Compatible-lightgrey?style=flat-square)
![Android](https://img.shields.io/badge/Android-Compatible-green?style=flat-square)

</div>
