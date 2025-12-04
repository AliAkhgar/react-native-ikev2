# 🛠️ Troubleshooting & Debug Guide

> **Complete debugging solutions for React Native IKEv2 integration**  
> *Comprehensive troubleshooting guide with practical solutions and debugging tools*

## 🎯 Quick Navigation

| 🎯 **Category** | 📝 **Description** | 🔗 **Link** |
|----------------|-------------------|-------------|
| 🤖 **Android Issues** | Permission, crashes, notifications | [View →](#-android-issues) |
| 📱 **iOS Issues** | Entitlements, capabilities, simulator | [View →](#-ios-issues) |
| 🌐 **Connection Problems** | Authentication, network, state issues | [View →](#-connection-problems) |
| 🚀 **Production Issues** | Release builds, performance, optimization | [View →](#-production-issues) |
| 🔧 **Debug Tools** | Logging, testing, diagnostics | [View →](#-debug-tools) |
| 📞 **Getting Help** | Bug reports, community resources | [View →](#-getting-help) |

---

## 🚨 Quick Issue Reference

<table>
<tr>
<th>🔍 <strong>Issue</strong></th>
<th>📱 <strong>Platform</strong></th>
<th>⚠️ <strong>Severity</strong></th>
<th>✅ <strong>Quick Solution</strong></th>
</tr>
<tr>
<td><strong>Permission Denied</strong></td>
<td>🤖 Android</td>
<td>🔴 Critical</td>
<td>Call <code>prepare()</code> before connecting</td>
</tr>
<tr>
<td><strong>VPN Entitlement Missing</strong></td>
<td>🍎 iOS</td>
<td>🔴 Critical</td>
<td>Add Network Extensions capability in Xcode</td>
</tr>
<tr>
<td><strong>Connection Fails Immediately</strong></td>
<td>🌐 Both</td>
<td>🟡 Medium</td>
<td>Verify credentials and server address</td>
</tr>
<tr>
<td><strong>State Not Updating</strong></td>
<td>🌐 Both</td>
<td>🟢 Low</td>
<td>Check event listener setup</td>
</tr>
<tr>
<td><strong>Production Failures</strong></td>
<td>🌐 Both</td>
<td>🟡 Medium</td>
<td>Check release build configuration</td>
</tr>
</table>

## 🤖 Android Issues

> **Common Android-specific problems and their solutions**

### 🚫 Permission Denied

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>Connection fails with "VPN permission denied" error</td></tr>
<tr><td>🔄 <strong>Status</strong></td><td><code>prepare()</code> returns <code>false</code></td></tr>
<tr><td>🧠 <strong>Root Cause</strong></td><td>Android requires explicit VPN permission from user</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🔴 <strong>Critical</strong> - Blocks all VPN functionality</td></tr>
</table>

**💡 Solution:**
```typescript
const connectWithPermission = async () => {
  try {
    // ✅ Check if permission is already granted
    const isPrepared = await IKEv2.isPrepared();
    
    if (!isPrepared) {
      // 🔑 Request permission from user
      const granted = await IKEv2.prepare();
      
      if (!granted) {
        Alert.alert(
          '🔐 Permission Required',
          'VPN permission is required to establish secure connection.',
          [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Settings', onPress: () => Linking.openSettings() }
          ]
        );
        return;
      }
    }
    
    // 🚀 Now safe to connect
    await IKEv2.connect(config);
  } catch (error) {
    console.error('❌ Permission error:', error);
  }
};
```

**🛡️ Best Practice:**
> Always check permissions before attempting connection on Android

---

### 💥 VPN Service Crashes

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>App crashes when connecting, "VPN service stopped unexpectedly"</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🔴 <strong>Critical</strong> - App instability</td></tr>
</table>

**🧠 Common Causes:**
1. 📱 Device incompatibility
2. 🧠 Insufficient memory allocation
3. ⚙️ Invalid configuration parameters

**💡 Solutions:**

**🛡️ Add crash detection and fallback:**
```typescript
const connectSafely = async () => {
  try {
    await IKEv2.connect(config);
  } catch (error) {
    console.error('Connection failed:', error);
    
    // Log detailed error info
    console.log('Config used:', JSON.stringify({
      address: config.address,
      hasUsername: !!config.username,
      hasPassword: !!config.password,
    }));
    
    throw error;
  }
};
```

---

### 🔔 Notification Issues

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>VPN notification not showing or actions not working</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟡 <strong>Medium</strong> - Affects user experience</td></tr>
</table>

**💡 Solution:**
```typescript
// ✅ Ensure all required notification fields are provided
androidOptions: {
  connectionName: 'My IKEv2 VPN',
  AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
  Notification: {
    openActivityPackageName: 'com.yourapp.MainActivity', // Must match exactly
    titleConnected: '✅ Secure connection active',
    titleConnecting: '🔄 Establishing connection...',
    showDisconnectAction: true,
    titleDisconnectButton: '🔌 Disconnect',
  }
}
```

**📱 Android 13+ Requirements:**

*Add notification permission to AndroidManifest.xml:*
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

*Request permission at runtime:*
```typescript
import { PermissionsAndroid } from 'react-native';

const requestNotificationPermission = async () => {
  if (Platform.OS === 'android' && Platform.Version >= 33) {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
    );
    return granted === PermissionsAndroid.RESULTS.GRANTED;
  }
  return true;
};
```

---

### 📋 Android Setup Placeholder

> **⚠️ Note:** Detailed Android troubleshooting and setup instructions are coming soon. The following is general guidance.

**🔧 General Android Debugging Steps:**
1. Check that all required permissions are in AndroidManifest.xml
2. Verify VPN service configuration
3. Test on physical device (not emulator)
4. Check logcat for detailed error messages

```bash
# View Android logs for IKEv2
adb logcat | grep -i "ikev2\|vpn\|strongswan"
```

## 📱 iOS Issues

> **iOS-specific challenges and comprehensive solutions**

### 🚫 VPN Entitlement Missing

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>"Personal VPN entitlement missing" error on iOS</td></tr>
<tr><td>🔄 <strong>Status</strong></td><td>Connection fails immediately</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🔴 <strong>Critical</strong> - No VPN functionality</td></tr>
</table>

**💡 Solution:**

**📱 Xcode Configuration:**
1. Select your App target in Xcode
2. Go to **Signing & Capabilities**
3. Click **+ Capability**
4. Add **Network Extensions**
5. Check **Personal VPN**

**📄 Verify Entitlements:**
```xml
<!-- YourApp.entitlements -->
<key>com.apple.developer.networking.vpn.api</key>
<array>
    <string>allow-vpn</string>
</array>
```

**🔍 Debug Logging:**
```typescript
const debugConnect = async () => {
  console.log('⚙️ iOS Config:', iOSOptions);
  
  try {
    await IKEv2.connect(config);
    console.log('✅ Connection successful');
  } catch (error) {
    console.error('❌ iOS Connection Error:', error);
    console.error('📋 Error details:', JSON.stringify(error, null, 2));
  }
};
```

---

### 📜 Provisioning Profile Issues

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>App Store rejection, signing errors</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🔴 <strong>Critical</strong> - Prevents deployment</td></tr>
</table>

**💡 Solution:**

1. **Generate new provisioning profile** with VPN entitlement enabled
2. **Download and install** the updated profile in Xcode
3. **Verify** the profile includes `com.apple.developer.networking.vpn.api`

**🎯 Important Notes:**
- 🔗 Ensure your Apple Developer account has VPN capability enabled
- 📱 Both development and distribution profiles need the entitlement
- 🏪 Required for App Store approval

---

### 🖥️ iOS Simulator Limitations

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>VPN appears connected but no traffic routes</td></tr>
<tr><td>🔄 <strong>Status</strong></td><td>Connection timeouts in simulator</td></tr>
<tr><td>🧠 <strong>Root Cause</strong></td><td>iOS Simulator doesn't support VPN functionality</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟡 <strong>Testing Issue</strong> - Development only</td></tr>
</table>

**💡 Solution:**
> Always test VPN functionality on physical iOS devices

```typescript
const checkPlatform = () => {
  if (__DEV__ && Platform.OS === 'ios') {
    Alert.alert(
      '📱 Development Note',
      'VPN functionality requires physical iOS device. Simulator testing is limited.',
      [{ text: 'OK', style: 'default' }]
    );
  }
};
```

**🧪 Testing Recommendations:**
- ✅ Use physical iPhone/iPad for VPN testing
- 🔧 Test both development and release builds
- 📊 Verify on multiple iOS versions
- 🌐 Test different network conditions

---

### 🌟 iOS Advantages with IKEv2

Unlike OpenVPN, IKEv2 on iOS has significant advantages:

| Feature | OpenVPN | IKEv2 |
|---------|---------|-------|
| **Network Extension** | Required | Not required |
| **Setup Complexity** | High | Low |
| **Native Support** | No | Yes (NEVPNManager) |
| **Performance** | Good | Excellent |
| **Battery Impact** | Higher | Lower |

## 🌐 Connection Problems

> **Cross-platform connection issues and debugging strategies**

### ⚡ Connection Fails Immediately

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>Connection state goes directly to ERROR</td></tr>
<tr><td>🔄 <strong>Status</strong></td><td>No network traffic through VPN</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟡 <strong>Medium</strong> - Configuration issue</td></tr>
</table>

**🔍 Debugging Steps:**

**1️⃣ Validate Configuration:**
```typescript
const validateConfig = (config: ConnectionParams) => {
  const errors = [];
  
  if (!config.address) errors.push('❌ Server address missing');
  if (!config.username) errors.push('❌ Username missing');
  if (!config.password) errors.push('❌ Password missing');
  
  if (errors.length > 0) {
    throw new Error(`🚫 Configuration errors: ${errors.join(', ')}`);
  }
  
  console.log('✅ Configuration validation passed');
};
```

**2️⃣ Test Network Connectivity:**
```typescript
const testConnectivity = async () => {
  try {
    const response = await fetch('https://google.com', { timeout: 5000 });
    console.log('🌐 Network available:', response.status === 200);
    return response.status === 200;
  } catch (error) {
    console.error('❌ Network test failed:', error);
    throw new Error('🚫 No internet connection');
  }
};
```

**3️⃣ Verify Server Address:**
```typescript
const verifyServer = async (address: string) => {
  console.log(`🔍 Verifying server: ${address}`);
  
  // Check if address is reachable
  try {
    // IKEv2 uses UDP port 500 and 4500
    console.log('📡 IKEv2 uses ports 500 (IKE) and 4500 (NAT-T)');
    console.log('⚠️ Make sure these ports are open on your firewall');
  } catch (error) {
    console.error('❌ Server verification failed:', error);
  }
};
```

---

### 🔐 Authentication Failures

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>"Authentication failed" error, connection timeouts</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟡 <strong>Medium</strong> - Credential or server issue</td></tr>
</table>

**🧠 Common Causes:**
1. 🔑 Incorrect credentials
2. 📜 Server certificate issues  
3. ⏰ Time synchronization problems
4. 🔐 Wrong authentication type

**💡 Solutions:**

**🔑 Credential Validation:**
```typescript
const validateCredentials = async (username: string, password: string) => {
  // ✅ Implement your credential validation logic
  if (!username || username.length < 3) {
    throw new Error('❌ Invalid username');
  }
  
  if (!password || password.length < 6) {
    throw new Error('❌ Invalid password');
  }
  
  console.log('✅ Credentials validation passed');
};
```

**🔐 Check Authentication Type (Android):**
```typescript
androidOptions: {
  // Try different auth types if one fails
  AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
  // or try: IKEv2.AndroidAuthType.IKEv2_BYOD_EAP,
}
```

---

### 🔄 State Not Updating

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>UI doesn't reflect connection changes</td></tr>
<tr><td>🔄 <strong>Status</strong></td><td>State listener not triggered</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟢 <strong>Low</strong> - UI synchronization issue</td></tr>
</table>

**🧠 Common Causes:**
1. 🎧 Event listener not properly registered
2. 🔄 Component unmounted before state change
3. 🔀 Multiple listeners interfering

**💡 Solution:**
```typescript
const VPNComponent = () => {
  const [vpnState, setVpnState] = useState(IKEv2.ConnectionState.DISCONNECTED);
  
  useEffect(() => {
    // 🎧 Single listener with proper cleanup
    const subscription = IKEv2.addIKev2StateChangeListener((state) => {
      console.log('🔄 State change:', state.state);
      setVpnState(state.state);
    });
    
    // 📋 Request initial state
    IKEv2.requestCurrentState();
    
    // 🧹 Cleanup on unmount
    return () => {
      console.log('🗑️ Removing VPN state listener');
      subscription.remove();
    };
  }, []); // ✅ Empty dependency array
  
  return (
    <View>
      <Text>🔄 Current State: {vpnState}</Text>
      {vpnState === IKEv2.ConnectionState.CONNECTED && <Text>✅ Connected</Text>}
      {vpnState === IKEv2.ConnectionState.CONNECTING && <Text>🔄 Connecting...</Text>}
      {vpnState === IKEv2.ConnectionState.ERROR && <Text>❌ Error</Text>}
    </View>
  );
};
```

## 🚀 Production Issues

> **Release build challenges and performance optimization**

### 🏭 VPN Works in Dev, Fails in Production

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>Development works perfectly, production builds fail</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🔴 <strong>Critical</strong> - Deployment blocker</td></tr>
</table>

**🧠 Common Causes:**
1. 📦 Missing native dependencies in release build
2. 🔒 Proguard/R8 obfuscation issues (Android)
3. 🏷️ Provisioning profile issues (iOS)

**🤖 Android Solutions:**

**🔧 Update Proguard Rules:**
```proguard
# 🔐 React Native IKEv2 Protection
-keep class com.ikev2.** { *; }
-keep class org.strongswan.** { *; }

# 🚫 Don't warn about missing classes
-dontwarn com.ikev2.**
-dontwarn org.strongswan.**
```

**🧪 Test Release Build Locally:**
```bash
cd android
./gradlew assembleRelease
adb install app/build/outputs/apk/release/app-release.apk
```

**📱 iOS Solutions:**

**🔍 Verify Archive Build:**
- ✅ Test with Archive build, not just Release scheme
- ✅ Check provisioning profile includes VPN entitlement
- ✅ Verify entitlements file is correctly configured

**🔍 Debug Release Issues:**
```typescript
const logBuildInfo = () => {
  console.log('🏗️ Build type:', __DEV__ ? 'Development' : 'Production');
  console.log('📱 Platform:', Platform.OS, Platform.Version);
};
```

---

### ⚡ Performance Issues

<table>
<tr><td>🎯 <strong>Symptoms</strong></td><td>High battery drain, app unresponsive, memory leaks</td></tr>
<tr><td>⚠️ <strong>Severity</strong></td><td>🟡 <strong>Medium</strong> - User experience impact</td></tr>
</table>

**💡 Optimization Solutions:**

**🎧 Optimize State Listeners:**
```typescript
// ❌ Bad: Multiple listeners
useEffect(() => {
  const listener1 = IKEv2.addIKev2StateChangeListener(handler1);
  const listener2 = IKEv2.addIKev2StateChangeListener(handler2);
  // Creates unnecessary overhead
}, []);

// ✅ Good: Single listener with multiplexing
useEffect(() => {
  const subscription = IKEv2.addIKev2StateChangeListener((state) => {
    handler1(state);
    handler2(state);
    // Single listener handles all cases
  });
  
  return () => subscription.remove();
}, []);
```

**🔔 Reduce Notification Updates (Android):**
```typescript
androidOptions: {
  Notification: {
    showTimer: false, // 🔋 Reduces battery usage
    showDisconnectAction: true,
    // ✅ Only essential notification info
  }
}
```

**🧠 Memory Management:**
```typescript
// ✅ Properly cleanup resources
const useVPNConnection = () => {
  useEffect(() => {
    const subscription = IKEv2.addIKev2StateChangeListener(handleStateChange);
    
    return () => {
      subscription.remove();
      // 🧹 Additional cleanup if needed
    };
  }, []);
};
```

## 🔧 Debug Tools

> **Advanced debugging tools and diagnostic techniques**

### 📊 Enable Detailed Logging

<table>
<tr><td>🎯 <strong>Purpose</strong></td><td>Capture comprehensive connection logs for troubleshooting</td></tr>
<tr><td>📱 <strong>Platform</strong></td><td>Android & iOS (different approaches)</td></tr>
</table>

**🤖 Android Logging:**
```bash
# View IKEv2/VPN related logs
adb logcat | grep -i "ikev2\|vpn\|strongswan\|charon"

# More detailed logging
adb logcat *:V | grep -i vpn
```

**📱 iOS Logging:**
```swift
// Enable Console logging in Xcode
// Filter by your app name or "VPN"
```

**📋 React Native Logging:**
```typescript
// Add comprehensive logging to your VPN code
const debugVPNConnection = async () => {
  console.log('🚀 Starting VPN connection debug...');
  console.log('📱 Platform:', Platform.OS, Platform.Version);
  console.log('⚙️ Config:', JSON.stringify({
    address: config.address,
    hasCredentials: !!(config.username && config.password),
  }, null, 2));
  
  try {
    const state = await IKEv2.getCurrentState();
    console.log('📊 Current state:', state);
    
    await IKEv2.connect(config);
    console.log('✅ Connection successful');
  } catch (error) {
    console.error('❌ Connection failed:', error);
    console.error('📋 Error stack:', error.stack);
  }
};
```

---

### 🌐 Network Testing

<table>
<tr><td>🎯 <strong>Purpose</strong></td><td>Verify VPN functionality and IP address changes</td></tr>
<tr><td>🔧 <strong>Usage</strong></td><td>Automated testing and validation</td></tr>
</table>

**🧪 Comprehensive VPN Test:**
```typescript
const testVPNConnection = async () => {
  console.log('🧪 Testing VPN connection...');
  
  try {
    // 1️⃣ Test before VPN
    console.log('📡 Testing connection before VPN...');
    const beforeIP = await fetch('https://api.ipify.org?format=json')
      .then(r => r.json());
    console.log('🌐 IP before VPN:', beforeIP.ip);
    
    // 2️⃣ Connect VPN
    console.log('🔄 Connecting to VPN...');
    await IKEv2.connect(config);
    
    // 3️⃣ Wait for stable connection
    await new Promise(resolve => setTimeout(resolve, 5000));
    
    // 4️⃣ Verify connection state
    const currentState = await IKEv2.getCurrentState();
    console.log('📊 Current VPN state:', currentState);
    
    if (currentState !== IKEv2.ConnectionState.CONNECTED) {
      throw new Error(`❌ VPN not connected. State: ${currentState}`);
    }
    
    // 5️⃣ Test after VPN
    console.log('📡 Testing connection after VPN...');
    const afterIP = await fetch('https://api.ipify.org?format=json')
      .then(r => r.json());
    console.log('🌐 IP after VPN:', afterIP.ip);
    
    // 6️⃣ Verify IP change
    if (beforeIP.ip !== afterIP.ip) {
      console.log('✅ VPN is working - IP changed successfully');
      console.log(`🔄 ${beforeIP.ip} → ${afterIP.ip}`);
      return true;
    } else {
      console.log('⚠️ VPN may not be working - IP unchanged');
      return false;
    }
    
  } catch (error) {
    console.error('🚫 VPN test failed:', error);
    return false;
  }
};
```

---

### 🔍 Connection State Monitoring

<table>
<tr><td>🎯 <strong>Purpose</strong></td><td>Real-time monitoring and debugging of connection states</td></tr>
<tr><td>🎧 <strong>Usage</strong></td><td>Development and production debugging</td></tr>
</table>

**📊 Advanced State Monitor:**
```typescript
const createStateMonitor = () => {
  const stateHistory: Array<{timestamp: string, state: string}> = [];
  const maxHistorySize = 50;
  
  const monitor = {
    start: () => {
      console.log('🎧 Starting VPN state monitor...');
      
      const subscription = IKEv2.addIKev2StateChangeListener((state) => {
        const timestamp = new Date().toISOString();
        const stateInfo = {
          timestamp,
          state: state.state,
        };
        
        // 📝 Add to history
        stateHistory.push(stateInfo);
        if (stateHistory.length > maxHistorySize) {
          stateHistory.shift();
        }
        
        // 📊 Log state change with visual indicators
        const stateEmoji: Record<string, string> = {
          [IKEv2.ConnectionState.CONNECTED]: '✅',
          [IKEv2.ConnectionState.CONNECTING]: '🔄',
          [IKEv2.ConnectionState.DISCONNECTED]: '⚪',
          [IKEv2.ConnectionState.DISCONNECTING]: '🔄',
          [IKEv2.ConnectionState.ERROR]: '❌',
        };
        
        console.log(`${stateEmoji[state.state] || '❓'} State: ${state.state} | ${timestamp}`);
      });
      
      return subscription;
    },
    
    getHistory: () => stateHistory,
    
    exportLogs: () => {
      const logs = stateHistory.map(entry => 
        `${entry.timestamp} | ${entry.state}`
      ).join('\n');
      
      console.log('📋 State History Export:\n', logs);
      return logs;
    }
  };
  
  return monitor;
};

// 🚀 Usage
const monitor = createStateMonitor();
const subscription = monitor.start();

// 🧹 Cleanup when done
// subscription.remove();
```

## 📞 Getting Help

> **Community resources and professional support options**

### 📋 Before Opening an Issue

<table>
<tr><td>✅ <strong>Required Steps</strong></td><td>Complete these steps before seeking help</td></tr>
</table>

**🔍 Pre-Issue Checklist:**
1. ✅ **Review this troubleshooting guide** - Check all relevant sections
2. 📱 **Test on physical devices** - Avoid simulator-only testing
3. ⚙️ **Verify your configuration** - Double-check all parameters
4. 🏗️ **Check platform-specific requirements** - iOS/Android setup
5. 📊 **Enable verbose logging** - Capture detailed error information
6. 🧪 **Test with minimal configuration** - Isolate the issue

---

### 🐛 Bug Report Information

<table>
<tr><td>📊 <strong>Required Data</strong></td><td>Include this information for faster resolution</td></tr>
</table>

**📋 Bug Report Template:**
```typescript
const getBugReportInfo = async () => {
  const info = {
    // 🏗️ Environment Information
    platform: Platform.OS,
    platformVersion: Platform.Version,
    reactNativeVersion: '0.73.x', // Your RN version
    libraryVersion: '1.x.x',      // react-native-ikev2 version
    
    // 📱 Device Information
    deviceInfo: {
      model: 'iPhone 15 Pro / Samsung Galaxy S24',
      osVersion: Platform.Version,
      isEmulator: false // Always test on real devices
    },
    
    // 🔄 VPN State Information
    vpnState: await IKEv2.getCurrentState(),
    isPrepared: Platform.OS === 'android' ? await IKEv2.isPrepared() : 'N/A',
    
    // ⚙️ Configuration (sanitized)
    config: {
      hasAddress: !!config.address,
      hasCredentials: !!(config.username && config.password),
      platform: Platform.OS,
      // ⚠️ DO NOT include actual credentials or server details
    },
    
    // 📊 Error Information
    errorDetails: {
      // Include specific error messages
      // Include stack traces if available
    }
  };
  
  console.log('🐛 Bug Report Info:', JSON.stringify(info, null, 2));
  return info;
};
```

**🔒 Security Notes:**
- ❌ **Never include** usernames, passwords, or server configurations
- ❌ **Never include** private keys or certificates
- ✅ **Do include** sanitized configuration structure
- ✅ **Do include** error messages and stack traces

---

### 🌐 Community Resources

<table>
<tr><td>🔗 <strong>Resource</strong></td><td>📝 <strong>Description</strong></td><td>🎯 <strong>Best For</strong></td></tr>
<tr><td><strong>GitHub Issues</strong></td><td>Bug reports and feature requests</td><td>Technical problems and enhancements</td></tr>
<tr><td><strong>GitHub Discussions</strong></td><td>Community support and Q&A</td><td>General questions and sharing experiences</td></tr>
<tr><td><strong>Examples Directory</strong></td><td>Working code samples</td><td>Implementation guidance</td></tr>
<tr><td><strong>Documentation</strong></td><td>Complete API and setup guides</td><td>Learning and reference</td></tr>
</table>

**🔗 Quick Links:**
- 🐛 **Report Issues**: [GitHub Issues →](https://github.com/AliAkhgar/react-native-ikev2/issues)
- 💬 **Community Discussions**: [GitHub Discussions →](https://github.com/AliAkhgar/react-native-ikev2/discussions)
- 💡 **Working Examples**: [Examples Directory →](../example)
- 📚 **Full Documentation**: [API Reference →](API.md)

---

### 🏆 Contributing to Solutions

<table>
<tr><td>🤝 <strong>How You Can Help</strong></td><td>🌟 <strong>Impact</strong></td></tr>
<tr><td>Share working configurations</td><td>Help others with similar setups</td></tr>
<tr><td>Report platform-specific issues</td><td>Improve library compatibility</td></tr>
<tr><td>Contribute documentation improvements</td><td>Enhance developer experience</td></tr>
<tr><td>Submit tested bug fixes</td><td>Increase library stability</td></tr>
</table>

**📝 Contribution Guidelines:**
- 🧪 **Test thoroughly** on both iOS and Android
- 📚 **Update documentation** for any changes
- 🔧 **Follow coding standards** established in the project
- ✅ **Include unit tests** for new features

---

### 💡 Pro Tips for Faster Resolution

<table>
<tr><td>💡 <strong>Tip</strong></td><td>⚡ <strong>Benefit</strong></td></tr>
<tr><td>Create minimal reproduction case</td><td>Faster debugging and resolution</td></tr>
<tr><td>Test on multiple devices/versions</td><td>Better understanding of scope</td></tr>
<tr><td>Check recent issues for duplicates</td><td>Avoid duplicate reports</td></tr>
<tr><td>Provide before/after comparisons</td><td>Clear understanding of expected behavior</td></tr>
</table>

---

**🎯 Remember:** Most VPN issues are configuration-related. Double-check your setup before assuming it's a library bug! 

**🔧 Quick Validation:** Test with the minimal configuration from our examples first, then gradually add your custom settings to isolate issues.
