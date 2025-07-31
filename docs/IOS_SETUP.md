# iOS Setup Guide

This guide provides detailed instructions for integrating the React Native IKEv2 library into your iOS application.

## Key Benefits

✅ **No Network Extension Required**: Unlike other VPN implementations, this library works directly within your main app  
✅ **Native Performance**: Direct integration with iOS NetworkExtension framework  
✅ **Background Support**: Maintains connection when app is backgrounded  
✅ **On-Demand Connection**: Support for automatic VPN activation  

## Prerequisites

- iOS 13.0 or higher
- Xcode 12.0 or higher
- Apple Developer Account (for VPN entitlements)

## Step-by-Step Setup

### 1. Xcode Project Configuration

Open your iOS project in Xcode and follow these steps:

#### Add Capabilities

1. Select your app target in Xcode
2. Go to "Signing & Capabilities" tab
3. Click "+ Capability" and add:
   - **Personal VPN** - Required for VPN functionality
   - **Network Extensions** - For VPN tunnel management

### 2. Entitlements Configuration

Ensure your app's entitlements file (`.entitlements`) includes:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <!-- VPN API Entitlement -->
    <key>com.apple.developer.networking.vpn.api</key>
    <array>
        <string>allow-vpn</string>
    </array>
    
    <!-- Your other entitlements -->
</dict>
</plist>
```

### 3. Pod Installation

Make sure the podspec is properly installed:

```bash
cd ios && pod install
```

### 4. iOS Configuration Options

When connecting, configure iOS-specific options:

```typescript
import * as IKev2 from 'react-native-ikev2';

await IKev2.connect({
  address: 'your-server.com',
  username: 'username',
  password: 'password',
  
  iOSOptions: {
    localizedDescription: 'My Secure VPN',     // Name shown in iOS VPN settings
    disconnectOnSleep: false,                  // Keep connected when device sleeps
    onDemandEnabled: false,                    // Auto-connect when needed
    includeAllNetworks: true,                  // Route all traffic through VPN
    excludeLocalNetworks: false,               // Include local network traffic
    excludeCellularServices: false,            // Include cellular traffic
    excludeDeviceCommunication: false,         // Include device communication
  },
  
  androidOptions: {
    // Android options (ignored on iOS)
  }
});
```

## iOS-Specific Features

### Connection Management

```typescript
// Check connection state
const state = await IKev2.getCurrentState();

// Listen for state changes
const subscription = IKev2.addIKev2StateChangeListener((state) => {
  console.log('VPN State:', state.state);
});

// Clean up listener
subscription.remove();
```

### Background Operation

The VPN connection will automatically maintain itself when your app goes to the background. The iOS system handles the VPN tunnel independently of your app's lifecycle.

### System Integration

Once connected, the VPN will appear in:
- iOS Settings > VPN & Device Management
- Control Center (if VPN toggle is enabled)
- Status bar VPN indicator

## Example Implementation

```typescript
import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';
import * as IKev2 from 'react-native-ikev2';

const iOSVPNExample = () => {
  const [vpnState, setVPNState] = useState(IKev2.ConnectionState.DISCONNECTED);

  useEffect(() => {
    const subscription = IKev2.addIKev2StateChangeListener((state) => {
      setVPNState(state.state);
    });

    IKev2.requestCurrentState();

    return () => subscription.remove();
  }, []);

  const connectVPN = async () => {
    try {
      await IKev2.connect({
        address: 'vpn.example.com',
        username: 'myusername',
        password: 'mypassword',
        
        iOSOptions: {
          localizedDescription: 'Secure Business VPN',
          disconnectOnSleep: false,
          onDemandEnabled: false,
          includeAllNetworks: true,
          excludeLocalNetworks: false,
        },
        
        androidOptions: {
          connectionName: 'iOS VPN',
          AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
          Notification: {
            openActivityPackageName: 'com.example.app',
          },
        },
      });
    } catch (error) {
      Alert.alert('Connection Error', error.toString());
    }
  };

  const disconnectVPN = async () => {
    try {
      await IKev2.disconnect();
    } catch (error) {
      Alert.alert('Disconnection Error', error.toString());
    }
  };

  const isConnected = vpnState === IKev2.ConnectionState.CONNECTED;

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text style={{ fontSize: 18, marginBottom: 20 }}>
        VPN Status: {isConnected ? 'Connected' : 'Disconnected'}
      </Text>
      
      <TouchableOpacity
        onPress={isConnected ? disconnectVPN : connectVPN}
        style={{
          backgroundColor: isConnected ? '#f44336' : '#4caf50',
          padding: 15,
          borderRadius: 8,
        }}
      >
        <Text style={{ color: 'white', fontSize: 16 }}>
          {isConnected ? 'Disconnect' : 'Connect'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default iOSVPNExample;
```

## Troubleshooting

### Common Issues

**Issue**: "VPN configuration failed" error
- **Solution**: Ensure Personal VPN capability is enabled in Xcode
- **Check**: Verify your Apple Developer account has VPN entitlements

**Issue**: Connection immediately fails
- **Solution**: Check server address, username, and password
- **Check**: Ensure your VPN server supports IKEv2 protocol

**Issue**: App crashes on connection attempt
- **Solution**: Don't call connect multiple times simultaneously
- **Check**: Always check current state before connecting

**Issue**: VPN not appearing in iOS Settings
- **Solution**: Ensure proper entitlements configuration
- **Check**: Rebuild and reinstall the app

### Debug Tips

1. **Monitor State Changes**:
```typescript
IKev2.addIKev2StateChangeListener((state) => {
  console.log('iOS VPN State:', state.state);
});
```

2. **Check Current State**:
```typescript
const currentState = await IKev2.getCurrentState();
console.log('Current state:', currentState);
```

3. **Error Handling**:
```typescript
try {
  await IKev2.connect(params);
} catch (error) {
  console.error('iOS Connection Error:', error);
}
```

## Apple Review Guidelines

When submitting to the App Store:

1. **Clearly explain VPN usage** in your app description
2. **Provide legitimate use case** for VPN functionality
3. **Don't bypass geo-restrictions** for content
4. **Follow Apple's VPN guidelines** in the App Store Review Guidelines

## Security Considerations

- Always use secure credentials for VPN connections
- Consider implementing certificate pinning for additional security
- Store sensitive configuration securely using Keychain
- Validate server certificates when possible

## Performance Notes

- iOS handles VPN routing efficiently at the system level
- Minimal battery impact when properly configured
- Network performance depends on VPN server quality
- Consider implementing connection retry logic for poor network conditions