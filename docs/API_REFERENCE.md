# API Reference

Complete API documentation for React Native IKEv2 library.

## Table of Contents

- [Core Methods](#core-methods)
- [Types and Interfaces](#types-and-interfaces)
- [Enums](#enums)
- [Event Handling](#event-handling)
- [Error Handling](#error-handling)

## Core Methods

### `prepare(): Promise<boolean>`

**Platform**: Android Only

Prepares the VPN service for connection. This method must be called before attempting to connect on Android.

```typescript
const isReady = await IKev2.prepare();
if (isReady) {
  console.log('VPN service is ready');
} else {
  console.log('Failed to prepare VPN service');
}
```

**Returns**: Promise that resolves to `true` if preparation was successful, `false` otherwise.

**Throws**: May throw if VPN permissions are not granted.

---

### `isPrepared(): Promise<boolean>`

**Platform**: Android Only

Checks if the VPN service is currently prepared and ready for connection.

```typescript
const prepared = await IKev2.isPrepared();
console.log('VPN prepared:', prepared);
```

**Returns**: Promise that resolves to `true` if VPN is prepared, `false` otherwise.

---

### `connect(params: ConnectionParams): Promise<void>`

**Platform**: iOS, Android

Establishes a VPN connection using the provided parameters.

```typescript
await IKev2.connect({
  address: 'vpn.example.com',
  username: 'user123',
  password: 'password123',
  iOSOptions: {
    localizedDescription: 'My VPN',
    disconnectOnSleep: false,
    onDemandEnabled: false,
  },
  androidOptions: {
    connectionName: 'My VPN',
    AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
    Notification: {
      openActivityPackageName: 'com.example.MainActivity',
    },
  },
});
```

**Parameters**:
- `params`: [ConnectionParams](#connectionparams) - Configuration for the VPN connection

**Returns**: Promise that resolves when connection attempt starts (not when connected).

**Throws**: Connection errors, invalid parameters, or permission issues.

---

### `disconnect(): Promise<void>`

**Platform**: iOS, Android

Disconnects the current VPN connection.

```typescript
await IKev2.disconnect();
console.log('Disconnect request sent');
```

**Returns**: Promise that resolves when disconnection starts.

**Throws**: May throw if no active connection exists.

---

### `getCurrentState(): Promise<ConnectionState>`

**Platform**: iOS, Android

Retrieves the current VPN connection state synchronously.

```typescript
const state = await IKev2.getCurrentState();
console.log('Current state:', state);
```

**Returns**: Promise that resolves to the current [ConnectionState](#connectionstate).

---

### `requestCurrentState(): Promise<void>`

**Platform**: iOS, Android

Requests the current state and triggers the state change listener with the current state.

```typescript
await IKev2.requestCurrentState();
// State change listener will be called with current state
```

**Returns**: Promise that resolves when state request is sent.

---

## Types and Interfaces

### `ConnectionParams`

Configuration object for establishing VPN connections.

```typescript
interface ConnectionParams {
  address: string;           // VPN server address (IP or hostname)
  username: string;          // Authentication username
  password: string;          // Authentication password
  iOSOptions: IOSConnectionOptions;
  androidOptions: AndroidConnectionOptions;
}
```

**Properties**:
- `address`: The VPN server's IP address or hostname
- `username`: Username for authentication
- `password`: Password for authentication
- `iOSOptions`: iOS-specific configuration
- `androidOptions`: Android-specific configuration

---

### `IOSConnectionOptions`

iOS-specific VPN configuration options.

```typescript
interface IOSConnectionOptions {
  localizedDescription: string;        // VPN profile name in iOS settings
  disconnectOnSleep: boolean;          // Disconnect when device sleeps
  onDemandEnabled: boolean;            // Enable automatic connection
  includeAllNetworks?: boolean;        // Route all traffic through VPN
  excludeLocalNetworks?: boolean;      // Exclude local network traffic
  excludeCellularServices?: boolean;   // Exclude cellular services
  excludeDeviceCommunication?: boolean; // Exclude device communication
}
```

**Properties**:
- `localizedDescription`: Display name for the VPN profile in iOS Settings
- `disconnectOnSleep`: Whether to disconnect when the device goes to sleep
- `onDemandEnabled`: Enable on-demand VPN activation
- `includeAllNetworks`: Route all network traffic through VPN (optional)
- `excludeLocalNetworks`: Exclude local network traffic from VPN (optional)
- `excludeCellularServices`: Exclude cellular services from VPN (optional)
- `excludeDeviceCommunication`: Exclude device communication from VPN (optional)

---

### `AndroidConnectionOptions`

Android-specific VPN configuration options.

```typescript
interface AndroidConnectionOptions {
  connectionName: string;
  AuthType: AndroidAuthType;
  Notification: AndroidNotificationOptions;
  
  // Network Configuration
  MTU?: number;                          // Maximum Transmission Unit
  DnsServers?: string;                   // Custom DNS servers
  NatKeepAlive?: number;                 // NAT keep-alive interval
  
  // Certificate Validation
  sendCertificateRequest?: boolean;
  checkCerificateWithOCSP?: boolean;
  checkCertificateWithCRLs?: boolean;
  
  // Traffic Routing
  customSubnets?: string;
  excludeSubnets?: string;
  
  // App-Specific VPN
  allAppsUseVPN?: boolean;
  allowOnlySelectedAppsUseVPN?: boolean;
  selectedAppsPackageNames?: string[];
}
```

**Network Configuration**:
- `MTU`: Maximum Transmission Unit size (default: 1400)
- `DnsServers`: Space-separated DNS server addresses (e.g., "8.8.8.8 8.8.4.4")
- `NatKeepAlive`: NAT keep-alive interval in seconds (default: 45)

**Certificate Validation**:
- `sendCertificateRequest`: Send certificate request to server (default: true)
- `checkCerificateWithOCSP`: Validate certificates using OCSP (default: true)
- `checkCertificateWithCRLs`: Validate certificates using CRL (default: true)

**Traffic Routing**:
- `customSubnets`: Only route these subnets through VPN (space-separated CIDR notation)
- `excludeSubnets`: Exclude these subnets from VPN routing (space-separated CIDR notation)

**App-Specific VPN**:
- `allAppsUseVPN`: Whether all apps use VPN (default: true)
- `allowOnlySelectedAppsUseVPN`: If false, selected apps are excluded from VPN
- `selectedAppsPackageNames`: Array of package names for app-specific routing

---

### `AndroidNotificationOptions`

Configuration for VPN status notifications on Android.

```typescript
interface AndroidNotificationOptions {
  openActivityPackageName: string;
  
  // Notification Actions
  showDisconnectAction?: boolean;
  titleDisconnectButton?: string;
  showPauseAction?: boolean;
  showTimer?: boolean;
  
  // Status Messages
  titleConnecting?: string;
  titleConnected?: string;
  titleDisconnecting?: string;
  titleDisconnected?: string;
  titleError?: string;
}
```

**Required**:
- `openActivityPackageName`: Package name of activity to open when notification is tapped

**Actions**:
- `showDisconnectAction`: Show disconnect button in notification
- `titleDisconnectButton`: Text for disconnect button
- `showPauseAction`: Show pause button in notification
- `showTimer`: Show connection timer in notification

**Status Messages**:
- `titleConnecting`: Text shown during connection
- `titleConnected`: Text shown when connected
- `titleDisconnecting`: Text shown during disconnection
- `titleDisconnected`: Text shown when disconnected
- `titleError`: Text shown on error

---

## Enums

### `ConnectionState`

Represents the current VPN connection state.

```typescript
enum ConnectionState {
  DISCONNECTED = '0',    // VPN is disconnected
  DISCONNECTING = '1',   // VPN is disconnecting
  CONNECTING = '2',      // VPN is connecting
  CONNECTED = '3',       // VPN is connected
  INVALID = '-1',        // Invalid state
  ERROR = '-2'           // Error state
}
```

**Usage**:
```typescript
if (state === IKev2.ConnectionState.CONNECTED) {
  console.log('VPN is connected');
}
```

---

### `AndroidAuthType`

Authentication types supported on Android.

```typescript
enum AndroidAuthType {
  IKEv2_EAP = 'ikev2-eap',           // Standard EAP authentication
  IKEv2_BYOD_EAP = 'ikev2-byod-eap'  // BYOD EAP authentication
}
```

**Usage**:
```typescript
AuthType: IKev2.AndroidAuthType.IKEv2_EAP
```

---

### `CharonErrorState`

Detailed error states for debugging (read-only).

```typescript
enum CharonErrorState {
  NO_ERROR,
  AUTH_FAILED,
  PEER_AUTH_FAILED,
  LOOKUP_FAILED,
  UNREACHABLE,
  GENERIC_ERROR,
  PASSWORD_MISSING,
  CERTIFICATE_UNAVAILABLE,
  UNDEFINED,
}
```

---

## Event Handling

### `addIKev2StateChangeListener(callback): Subscription`

Subscribe to VPN state changes.

```typescript
const subscription = IKev2.addIKev2StateChangeListener((stateChange) => {
  console.log('New state:', stateChange.state);
  
  switch (stateChange.state) {
    case IKev2.ConnectionState.CONNECTING:
      console.log('Connecting to VPN...');
      break;
    case IKev2.ConnectionState.CONNECTED:
      console.log('VPN connected successfully');
      break;
    case IKev2.ConnectionState.DISCONNECTED:
      console.log('VPN disconnected');
      break;
    case IKev2.ConnectionState.ERROR:
      console.log('VPN connection error');
      break;
  }
});

// Clean up subscription
subscription.remove();
```

**Parameters**:
- `callback`: Function that receives [ConnectionStateListenerCallback](#connectionstatelistenercallback)

**Returns**: Subscription object with `remove()` method.

---

### `ConnectionStateListenerCallback`

Callback interface for state change events.

```typescript
interface ConnectionStateListenerCallback {
  state: ConnectionState;
}
```

**Usage**:
```typescript
const subscription = IKev2.addIKev2StateChangeListener((event) => {
  console.log('State changed to:', event.state);
});
```

---

## Error Handling

### Common Error Scenarios

**Connection Errors**:
```typescript
try {
  await IKev2.connect(params);
} catch (error) {
  if (error.message.includes('AUTH_FAILED')) {
    console.log('Authentication failed - check credentials');
  } else if (error.message.includes('UNREACHABLE')) {
    console.log('Server unreachable - check network and server address');
  } else {
    console.log('Connection error:', error.message);
  }
}
```

**Preparation Errors (Android)**:
```typescript
try {
  const prepared = await IKev2.prepare();
  if (!prepared) {
    console.log('User denied VPN permission');
  }
} catch (error) {
  console.log('Preparation error:', error.message);
}
```

**Permission Errors**:
```typescript
// Handle permission denial
try {
  await IKev2.connect(params);
} catch (error) {
  if (error.message.includes('PERMISSION')) {
    console.log('VPN permission required');
    // Prompt user to grant permission
  }
}
```

### Best Practices

1. **Always check state before operations**:
```typescript
const currentState = await IKev2.getCurrentState();
if (currentState === IKev2.ConnectionState.DISCONNECTED) {
  await IKev2.connect(params);
}
```

2. **Handle state changes appropriately**:
```typescript
IKev2.addIKev2StateChangeListener((state) => {
  if (state.state === IKev2.ConnectionState.ERROR) {
    // Handle error state
    IKev2.disconnect(); // Clean up
  }
});
```

3. **Clean up resources**:
```typescript
useEffect(() => {
  const subscription = IKev2.addIKev2StateChangeListener(handleStateChange);
  return () => subscription.remove();
}, []);
```

4. **Validate parameters before connection**:
```typescript
const connectVPN = async (params) => {
  if (!params.address || !params.username || !params.password) {
    throw new Error('Missing required connection parameters');
  }
  await IKev2.connect(params);
};
```