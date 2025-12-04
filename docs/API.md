# 🚀 API Reference

> **Complete API documentation for React Native IKEv2 library**  
> *Everything you need to integrate secure IKEv2 VPN connections in your React Native app*

## 🎯 Quick Navigation

| Section | Description | Link |
|---------|-------------|------|
| ⚡ **Core Methods** | Essential VPN connection methods | [View →](#-core-methods) |
| 🎧 **Event Listeners** | Real-time connection state monitoring | [View →](#-event-listeners) |
| 📋 **Types & Interfaces** | TypeScript definitions and configs | [View →](#-types--interfaces) |
| 🏷️ **Enums** | Connection states and configuration options | [View →](#-enums) |
| 🔄 **Platform Differences** | iOS vs Android specific behaviors | [View →](#-platform-differences) |

---

## ⚡ Core Methods

> **Essential methods for IKEv2 VPN connection management**

### 🤖 `prepare(): Promise<boolean>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>Android Only</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Request VPN permission from Android system</td></tr>
<tr><td>⚠️ <strong>Required</strong></td><td>Must be called before connecting on Android</td></tr>
</table>

**Example Usage:**
```typescript
const isGranted = await IKEv2.prepare();
if (!isGranted) {
  console.log('❌ User denied VPN permission');
} else {
  console.log('✅ VPN permission granted');
}
```

| Return Value | Description |
|--------------|-------------|
| `true` ✅ | Permission granted by user |
| `false` ❌ | Permission denied by user |

**⚠️ Possible Errors:**
- System VPN service unavailable

---

### 🔍 `isPrepared(): Promise<boolean>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>Android Only</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Check if VPN permission is already granted</td></tr>
<tr><td>💡 <strong>Use Case</strong></td><td>Avoid showing permission dialog unnecessarily</td></tr>
</table>

**Example Usage:**
```typescript
const hasPermission = await IKEv2.isPrepared();
if (hasPermission) {
  console.log('✅ Ready to connect - permission already granted');
  // Proceed with VPN connection
} else {
  console.log('⏳ Permission required - call prepare() first');
}
```

| Return Value | Description |
|--------------|-------------|
| `true` ✅ | Permission already granted |
| `false` ⏳ | Permission not yet granted |

---

### 🔗 `connect(params: ConnectionParams): Promise<void>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS & Android</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Initiate IKEv2 VPN connection with configuration</td></tr>
<tr><td>⚡ <strong>Action</strong></td><td>Establishes secure IKEv2/IPSec tunnel to VPN server</td></tr>
</table>

**Example Usage:**
```typescript
try {
  await IKEv2.connect({
    address: 'vpn.example.com',
    username: 'user123',
    password: 'password123',
    iOSOptions: { 
      localizedDescription: 'My IKEv2 Connection',
      disconnectOnSleep: false,
      onDemandEnabled: false,
    },
    androidOptions: { 
      connectionName: 'My IKEv2 VPN',
      AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
      Notification: {
        openActivityPackageName: 'com.myapp.MainActivity',
        titleConnected: 'VPN Active',
      }
    }
  });
  console.log('🎉 Connected successfully');
} catch (error) {
  console.error('❌ Connection failed:', error);
}
```

**📥 Parameters:**
- `params: ConnectionParams` - Complete connection configuration

**📤 Returns:** `Promise<void>`

**⚠️ Possible Errors:**
- `❌ Connection failed` - Network or server issues
- `❌ Invalid configuration` - Malformed config parameters  
- `❌ Network unavailable` - No internet connectivity
- `❌ Authentication failed` - Wrong credentials

---

### 🔌 `disconnect(): Promise<void>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS & Android</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Terminate active IKEv2 VPN connection</td></tr>
<tr><td>⚡ <strong>Action</strong></td><td>Cleanly closes VPN tunnel</td></tr>
</table>

**Example Usage:**
```typescript
try {
  await IKEv2.disconnect();
  console.log('✅ VPN disconnected successfully');
} catch (error) {
  console.error('❌ Disconnect failed:', error);
}
```

**📤 Returns:** `Promise<void>`

**⚠️ Possible Errors:**
- `❌ Disconnect failed` - System error during disconnection
- `❌ No active connection` - Already disconnected

---

### 📊 `getCurrentState(): Promise<ConnectionState>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS & Android</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Get current VPN connection status</td></tr>
<tr><td>⚡ <strong>Action</strong></td><td>Returns immediate state value</td></tr>
</table>

**Example Usage:**
```typescript
const state = await IKEv2.getCurrentState();
console.log('📊 Current VPN state:', state);

switch (state) {
  case IKEv2.ConnectionState.CONNECTED:
    console.log('🟢 VPN is active and connected');
    break;
  case IKEv2.ConnectionState.CONNECTING:
    console.log('🟡 VPN connection in progress...');
    break;
  case IKEv2.ConnectionState.DISCONNECTED:
    console.log('🔴 VPN is disconnected');
    break;
}
```

**📤 Returns:** `Promise<ConnectionState>`

| State | Icon | Description |
|-------|------|-------------|
| `DISCONNECTED` | 🔴 | Not connected to VPN |
| `CONNECTING` | 🟡 | Connection attempt in progress |
| `CONNECTED` | 🟢 | Successfully connected |
| `DISCONNECTING` | 🟠 | Disconnection in progress |
| `ERROR` | ❌ | Connection error occurred |
| `INVALID` | ⚪ | Invalid/unknown state |

---

### 📡 `requestCurrentState(): Promise<void>`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS & Android</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Trigger state change event with current status</td></tr>
<tr><td>💡 <strong>Use Case</strong></td><td>Unified event-driven state management</td></tr>
</table>

**Example Usage:**
```typescript
// Request current state - will trigger state change listener
await IKEv2.requestCurrentState();

// State will be delivered via addIKev2StateChangeListener callback
// No direct return value - uses event system for consistency
```

**📤 Returns:** `Promise<void>`
- No direct return - triggers state change event instead

**💡 Why use this?**
- Maintains consistent event-driven architecture
- Ensures all state updates go through the same listener system
- Useful for refreshing UI state after app becomes active

---

## 🎧 Event Listeners

> **Real-time monitoring of VPN connection state changes**

### 📻 `addIKev2StateChangeListener(callback: Function)`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS & Android</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Monitor VPN state changes in real-time</td></tr>
<tr><td>⚡ <strong>Action</strong></td><td>Registers callback for state updates</td></tr>
</table>

**Example Usage:**
```typescript
// Register state change listener
const subscription = IKEv2.addIKev2StateChangeListener((state) => {
  console.log('🔄 VPN state changed to:', state.state);
  
  switch (state.state) {
    case IKEv2.ConnectionState.CONNECTED:
      console.log('🟢 VPN is now connected - secure tunnel active');
      break;
    case IKEv2.ConnectionState.DISCONNECTED:
      console.log('🔴 VPN is now disconnected - using regular connection');
      break;
    case IKEv2.ConnectionState.CONNECTING:
      console.log('🟡 VPN connecting - establishing secure tunnel...');
      break;
    case IKEv2.ConnectionState.ERROR:
      console.log('❌ VPN connection error - check configuration');
      break;
  }
});

// 🧹 Always clean up when component unmounts
subscription.remove();
```

**📥 Parameters:**
- `callback: (state: ConnectionStateListenerCallback) => void`

**📤 Returns:** `EventSubscription`
- Object with `remove()` method to unsubscribe

**📊 Callback Parameter Structure:**
```typescript
interface ConnectionStateListenerCallback {
  state: ConnectionState;  // Current connection state
}
```

**💡 Best Practices:**
- ✅ Always call `subscription.remove()` to prevent memory leaks
- ✅ Use this for UI state updates (connection indicators, etc.)
- ✅ Handle all possible state values for robust UX
- ❌ Don't forget to unsubscribe when component unmounts

---

## 📋 Types & Interfaces

> **Complete TypeScript definitions for type-safe IKEv2 VPN integration**

### 🔧 `ConnectionParams`

<table>
<tr><td>📝 <strong>Purpose</strong></td><td>Main configuration interface for IKEv2 VPN connections</td></tr>
<tr><td>🎯 <strong>Required</strong></td><td>Server address, username, password, platform options</td></tr>
<tr><td>⚡ <strong>Usage</strong></td><td>Pass to <code>connect()</code> method</td></tr>
</table>

```typescript
interface ConnectionParams {
  address: string;                      // 🌐 VPN server address
  username: string;                     // 👤 Authentication username  
  password: string;                     // 🔑 Authentication password
  iOSOptions: IOSConnectionOptions;     // 🍎 iOS-specific configuration
  androidOptions: AndroidConnectionOptions; // 🤖 Android-specific configuration
}
```

**✅ Required Fields:**
| Field | Icon | Description |
|-------|------|-------------|
| `address` | 🌐 | VPN server address/hostname |
| `username` | 👤 | Username for VPN authentication |
| `password` | 🔑 | Password for VPN authentication |
| `iOSOptions` | 🍎 | iOS configuration (required even on Android) |
| `androidOptions` | 🤖 | Android configuration (required even on iOS) |

---

### 🍎 `IOSConnectionOptions`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>iOS Specific</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Configure iOS IKEv2 connection behavior via NEVPNManager</td></tr>
<tr><td>⚠️ <strong>Note</strong></td><td>Required even when running on Android</td></tr>
</table>

```typescript
interface IOSConnectionOptions {
  // 🔴 Required Fields
  localizedDescription: string;           // 📱 VPN connection description (shown in Settings)
  disconnectOnSleep: boolean;             // 😴 Disconnect when device sleeps
  onDemandEnabled: boolean;               // 🔄 Enable on-demand connection
  
  // 🌐 Optional Network Settings  
  includeAllNetworks?: boolean;           // 🌍 Route all traffic through VPN
  excludeLocalNetworks?: boolean;         // 🏠 Exclude local network traffic
  excludeCellularServices?: boolean;      // 📶 Exclude cellular services
  excludeDeviceCommunication?: boolean;   // 📱 Exclude device communication
}
```

**🔥 Example Configuration:**
```typescript
iOSOptions: {
  // Required settings
  localizedDescription: 'My Secure IKEv2 Connection',
  disconnectOnSleep: false,
  onDemandEnabled: false,
  
  // Network routing
  includeAllNetworks: false,    // Don't route ALL traffic
  excludeLocalNetworks: true,   // Keep local network access
}
```

**💡 Configuration Tips:**
- ✅ `localizedDescription` shows in iOS Settings → VPN
- ✅ Set `disconnectOnSleep: false` for persistent connections
- ✅ Enable `excludeLocalNetworks` to access local devices (printers, etc.)

---

### 🤖 `AndroidConnectionOptions`

<table>
<tr><td>🏷️ <strong>Platform</strong></td><td>Android Specific</td></tr>
<tr><td>📝 <strong>Purpose</strong></td><td>Comprehensive Android IKEv2 VPN configuration</td></tr>
<tr><td>⚠️ <strong>Note</strong></td><td>Required even when running on iOS</td></tr>
</table>

```typescript
interface AndroidConnectionOptions {
  // 🔴 Required
  connectionName: string;                 // 📛 VPN connection display name
  AuthType: AndroidAuthType;              // 🔐 Authentication type
  Notification: AndroidNotificationOptions; // 📲 Notification configuration
  
  // ⚙️ Optional Settings
  MTU?: number;                          // 📦 Maximum transmission unit
  
  /**
   * DNS servers separated by space
   * e.g. "8.8.8.8 2001:4806:4806:8888"
   * @default received_from_vpn_server
   */
  DnsServers?: string;                   // 🌐 Custom DNS servers
  
  /**
   * If behind routers, and router deletes NAT mapping too early, try 20 (seconds).
   * @default 45
   */
  NatKeepAlive?: number;                 // ⏱️ NAT keep-alive interval
  
  /**
   * To reduce request size, you can disable it.
   * It only works if server sends its own certificate AUTOMATICALLY.
   * @default true
   */
  sendCertificateRequest?: boolean;      // 📜 Send certificate request
  
  /**
   * Check online to see if server certificate has been revoked.
   * @default true
   */
  checkCerificateWithOCSP?: boolean;     // 🔍 OCSP certificate check
  
  /**
   * Use CRL to check the certificate integrity.
   * It is only used if OCSP does not yield the result.
   * @default true
   */
  checkCertificateWithCRLs?: boolean;    // 📋 CRL certificate check
  
  /**
   * Only route these subnets traffic to VPN.
   * Everything else will be routed as if there is no VPN.
   * Separated by space, e.g. "192.168.1.0/24 2001::db8::/64"
   */
  customSubnets?: string;                // 🛣️ Custom subnet routing
  
  /**
   * Traffic to these subnets will not be routed to VPN.
   * Separated by space, e.g. "192.168.1.0/24 2001::db8::/64"
   */
  excludeSubnets?: string;               // 🚫 Excluded subnets
  
  /**
   * If true, all apps are allowed to use VPN.
   * @default true
   */
  allAppsUseVPN?: boolean;               // 📱 All apps use VPN
  
  /**
   * Only if allAppsUseVPN is false.
   * If true, only selected apps are allowed to use VPN.
   * If false, selected apps are disallowed to use VPN.
   */
  allowOnlySelectedAppsUseVPN?: boolean; // 🎯 Selected apps only
  
  /**
   * Package name list of selected Apps.
   */
  selectedAppsPackageNames?: string[];   // 📦 App package names
}
```

**🔥 Example Configuration:**
```typescript
androidOptions: {
  // Required settings
  connectionName: 'My Enterprise VPN',
  AuthType: AndroidAuthType.IKEv2_EAP,
  
  // Custom DNS
  DnsServers: '8.8.8.8 8.8.4.4',
  
  // Routing
  customSubnets: '10.0.0.0/8 172.16.0.0/12',
  excludeSubnets: '192.168.1.0/24',
  
  // Per-app VPN
  allAppsUseVPN: false,
  allowOnlySelectedAppsUseVPN: true,
  selectedAppsPackageNames: [
    'com.company.app',
    'com.company.email',
  ],
  
  // Notification
  Notification: {
    openActivityPackageName: 'com.myapp.MainActivity',
    titleConnected: '🔒 Secure Connection Active',
    titleConnecting: '🔄 Connecting...',
    showDisconnectAction: true,
    showTimer: true,
  }
}
```

---

### 📲 `AndroidNotificationOptions`

<table>
<tr><td>📝 <strong>Purpose</strong></td><td>Configure Android VPN status notifications</td></tr>
<tr><td>🎯 <strong>Features</strong></td><td>Status updates, action buttons, connection timer</td></tr>
<tr><td>💡 <strong>UX Impact</strong></td><td>User can monitor and control VPN from notification</td></tr>
</table>

```typescript
interface AndroidNotificationOptions {
  // 🔴 Required
  openActivityPackageName: string;       // 📦 Package name to open on tap
  
  // 🎛️ Optional Actions
  showDisconnectAction?: boolean;        // 🔌 Show disconnect button
  titleDisconnectButton?: string;        // 🔌 Disconnect button text
  showPauseAction?: boolean;             // ⏸️ Show pause button
  showTimer?: boolean;                   // ⏱️ Show connection timer
  
  // 📱 Optional Status Messages
  titleConnecting?: string;              // 🟡 Connecting status text
  titleConnected?: string;               // 🟢 Connected status text
  titleDisconnecting?: string;           // 🟠 Disconnecting status text
  titleDisconnected?: string;            // 🔴 Disconnected status text
  titleError?: string;                   // ❌ Error status text
}
```

**🔥 Example Configuration:**
```typescript
Notification: {
  // Required settings
  openActivityPackageName: 'com.myapp.MainActivity',
  
  // Status messages with emojis for better UX
  titleConnected: '🔒 Secure Connection Active',
  titleConnecting: '🔄 Establishing Secure Connection...',
  titleDisconnecting: '🔄 Disconnecting from VPN...',
  titleDisconnected: '🔓 VPN Disconnected',
  titleError: '❌ VPN Connection Error',
  
  // Action buttons
  showDisconnectAction: true,
  titleDisconnectButton: 'Disconnect VPN',
  
  // Features
  showTimer: true,  // Shows connection duration
}
```

**💡 Notification Best Practices:**
- ✅ Use clear, descriptive titles that indicate current status
- ✅ Include emojis for quick visual status recognition
- ✅ Enable `showTimer` to show connection duration
- ✅ Provide disconnect action for user convenience

---

## 🏷️ Enums

> **Predefined constants for connection states and configuration options**

### 📊 `ConnectionState`

<table>
<tr><td>📝 <strong>Purpose</strong></td><td>VPN connection state enumeration</td></tr>
<tr><td>🎯 <strong>Usage</strong></td><td>State checking, UI updates, error handling</td></tr>
<tr><td>⚡ <strong>Source</strong></td><td>Returned by state methods and listeners</td></tr>
</table>

```typescript
enum ConnectionState {
  DISCONNECTED = '0',     // 🔴 Not connected to VPN
  DISCONNECTING = '1',    // 🟠 Disconnection in progress
  CONNECTING = '2',       // 🟡 Connection attempt in progress
  CONNECTED = '3',        // 🟢 Successfully connected
  INVALID = '-1',         // ⚪ Invalid/unknown state
  ERROR = '-2',           // ❌ Connection error occurred
}
```

| State | Icon | Value | Description | Common Actions |
|-------|------|-------|-------------|----------------|
| `DISCONNECTED` | 🔴 | `'0'` | Not connected to VPN | Show "Connect" button |
| `DISCONNECTING` | 🟠 | `'1'` | Disconnection in progress | Show loading state |
| `CONNECTING` | 🟡 | `'2'` | Connection attempt in progress | Show loading state |
| `CONNECTED` | 🟢 | `'3'` | Successfully connected | Show "Disconnect" button |
| `INVALID` | ⚪ | `'-1'` | Invalid/unknown state | Refresh state |
| `ERROR` | ❌ | `'-2'` | Connection error occurred | Show error message |

**💡 Usage Examples:**
```typescript
// In state change listener
const handleStateChange = (state) => {
  switch (state.state) {
    case IKEv2.ConnectionState.CONNECTED:
      setStatusIcon('🟢');
      setButtonText('Disconnect');
      setButtonEnabled(true);
      break;
    case IKEv2.ConnectionState.CONNECTING:
      setStatusIcon('🟡');
      setButtonText('Connecting...');
      setButtonEnabled(false);
      break;
    case IKEv2.ConnectionState.DISCONNECTED:
      setStatusIcon('🔴');
      setButtonText('Connect');
      setButtonEnabled(true);
      break;
    case IKEv2.ConnectionState.ERROR:
      setStatusIcon('❌');
      setButtonText('Retry');
      setButtonEnabled(true);
      showErrorDialog('Connection failed');
      break;
  }
};
```

---

### 🔐 `AndroidAuthType`

<table>
<tr><td>📝 <strong>Purpose</strong></td><td>IKEv2 authentication type enumeration for Android</td></tr>
<tr><td>🎯 <strong>Usage</strong></td><td>Configure authentication method in androidOptions</td></tr>
</table>

```typescript
enum AndroidAuthType {
  IKEv2_EAP = 'ikev2-eap',           // 🔐 EAP authentication
  IKEv2_BYOD_EAP = 'ikev2-byod-eap', // 📱 BYOD with EAP authentication
}
```

| Auth Type | Icon | Description | Use Case |
|-----------|------|-------------|----------|
| `IKEv2_EAP` | 🔐 | Standard EAP authentication | Most VPN servers |
| `IKEv2_BYOD_EAP` | 📱 | BYOD EAP authentication | Enterprise BYOD setups |

**🔥 Usage Example:**
```typescript
androidOptions: {
  AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
  // or for BYOD environments:
  // AuthType: IKEv2.AndroidAuthType.IKEv2_BYOD_EAP,
}
```

---

## 🔄 Platform Differences

> **Understanding iOS vs Android specific behaviors and requirements**

### 🍎 iOS-Specific Behavior

#### 📱 NEVPNManager Integration

<table>
<tr><td>✅ <strong>Advantage</strong></td><td>iOS uses built-in NEVPNManager - no extra extension needed</td></tr>
<tr><td>⚙️ <strong>System Level</strong></td><td>VPN configuration is managed at system level</td></tr>
<tr><td>🔒 <strong>Security</strong></td><td>Native IKEv2 implementation with hardware acceleration</td></tr>
</table>

```typescript
// iOS Configuration
iOSOptions: {
  // 📝 Shows in iOS Settings → VPN
  localizedDescription: 'My Secure VPN Connection',
  
  // ⚙️ iOS manages VPN connections at system level
  disconnectOnSleep: false, // System may still override this
  onDemandEnabled: true,    // Automatic connection when needed
}
```

#### 🌟 iOS Advantages

| Feature | Icon | Benefit |
|---------|------|---------|
| **No Extension Needed** | ✅ | Simpler setup than OpenVPN |
| **Native IKEv2** | 🔐 | Built-in protocol support |
| **Hardware Acceleration** | ⚡ | Better performance |
| **System Integration** | 📱 | Shows in Settings app |

**💡 iOS Development Tips:**
```typescript
// Check if running on iOS and handle accordingly
if (Platform.OS === 'ios') {
  console.log('🍎 Running on iOS - using native NEVPNManager');
  
  // iOS may disconnect in background
  AppState.addEventListener('change', (nextAppState) => {
    if (nextAppState === 'active') {
      // Check VPN state when app becomes active
      IKEv2.requestCurrentState();
    }
  });
}
```

---

### 🤖 Android-Specific Behavior

#### 🔐 VPN Service Permissions

<table>
<tr><td>⚠️ <strong>Required</strong></td><td>Explicit VPN permission must be granted</td></tr>
<tr><td>🎯 <strong>User Action</strong></td><td>System dialog requires user confirmation</td></tr>
<tr><td>⚡ <strong>One-Time</strong></td><td>Permission persists until app uninstall</td></tr>
</table>

```typescript
// Always check/request permission on Android
if (Platform.OS === 'android') {
  console.log('🤖 Android detected - checking VPN permissions');
  
  const isPrepared = await IKEv2.isPrepared();
  if (!isPrepared) {
    console.log('⏳ Requesting VPN permission...');
    const granted = await IKEv2.prepare(); // Shows system permission dialog
    
    if (granted) {
      console.log('✅ VPN permission granted');
    } else {
      console.log('❌ VPN permission denied by user');
      // Handle permission denial
    }
  } else {
    console.log('✅ VPN permission already granted');
  }
}
```

#### 🚀 Android Capabilities & Features

| Feature | Icon | Description | Benefit |
|---------|------|-------------|---------|
| **Rich Notifications** | 📲 | Actions, timer, status updates | Better UX control |
| **Per-App Routing** | 📱 | Route specific apps through VPN | Granular control |
| **Custom DNS** | 🌐 | Override DNS configuration | Enhanced privacy |
| **Subnet Routing** | 🛣️ | Custom routes, exclusions | Network flexibility |
| **strongSwan Based** | 🔐 | Proven IKEv2 implementation | Reliability |

---

### 🌐 Cross-Platform Best Practices

#### 🎯 Universal Connection Handler

```typescript
const connectVPN = async (config: ConnectionParams) => {
  try {
    console.log('🚀 Starting IKEv2 VPN connection...');
    
    // 🤖 Android-specific permission check
    if (Platform.OS === 'android') {
      console.log('🔐 Checking Android VPN permissions...');
      const isPrepared = await IKEv2.isPrepared();
      if (!isPrepared) {
        console.log('⏳ Requesting VPN permission...');
        const granted = await IKEv2.prepare();
        if (!granted) {
          throw new Error('❌ VPN permission denied by user');
        }
        console.log('✅ VPN permission granted');
      }
    }
    
    // 🔧 Configure for both platforms
    const connectionConfig: ConnectionParams = {
      ...config,
      iOSOptions: {
        localizedDescription: 'My Secure IKEv2 Connection',
        disconnectOnSleep: false,
        onDemandEnabled: false,
        excludeLocalNetworks: true,
        ...config.iOSOptions,
      },
      androidOptions: {
        connectionName: 'My IKEv2 VPN',
        AuthType: AndroidAuthType.IKEv2_EAP,
        Notification: {
          openActivityPackageName: 'com.myapp.MainActivity',
          titleConnected: '🔒 VPN Connected',
          titleConnecting: '⏳ Establishing connection...',
          showDisconnectAction: true,
          showTimer: true,
        },
        ...config.androidOptions,
      },
    };
    
    console.log('🔗 Attempting VPN connection...');
    await IKEv2.connect(connectionConfig);
    console.log('🎉 VPN connected successfully!');
    
  } catch (error) {
    console.error('❌ VPN connection failed:', error);
    throw error;
  }
};
```

---

## 🎓 Additional Resources

**📚 Related Documentation:**
- **[Installation Guide →](INSTALLATION.md)** - Platform setup and configuration
- **[Usage Examples →](EXAMPLES.md)** - Practical implementation examples  
- **[Troubleshooting →](DEBUG.md)** - Common issues and solutions

**💡 Pro Tips for Success:**
- Start with the simplest configuration that works
- Always handle both success and error cases
- Use TypeScript for better development experience
- Monitor state changes for responsive UI updates
- Test on physical devices for accurate VPN behavior

---

*This comprehensive API reference covers all available methods, types, and platform-specific considerations. For hands-on examples and implementation guidance, explore the related documentation links above.*
