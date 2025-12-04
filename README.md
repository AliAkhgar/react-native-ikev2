<p align="center">
  <!-- Banner image placeholder -->
  <img src="https://via.placeholder.com/800x200/1a365d/ffffff?text=React+Native+IKEv2" alt="React Native IKEv2 Banner" style="width:100%;">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/React%20Native-Turbo%20Module-61DAFB?style=flat-square&logo=react&logoColor=white" alt="React Native Turbo Module" />
  <img src="https://img.shields.io/badge/IKEv2-Protocol-blue?style=flat-square&logo=openvpn" alt="IKEv2 Protocol" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat-square&logo=opensourceinitiative&logoColor=white" alt="MIT License" />
</p>

<h3>Features</h3>

⚡ **Turbo Module** architecture for optimal performance  
🔐 **IKEv2/IPSec** native protocol support  
🍎 **iOS Built-in** NEVPNManager integration  
🤖 **Android strongSwan** implementation  
🔔 **Customizable notifications** with actions and timers  
🔄 **Event-driven** connection state management  
📝 **Fully typed** APIs with IntelliSense support  
🌐 **Advanced routing** and custom DNS features  

## 🧭 Fast Navigation

<div align="center">

**📦 Installation** - Complete setup and platform configuration guide  
<a href="docs/INSTALLATION.md" style="color: #007acc; text-decoration: none;">→ Browse Installation Guide</a>

**📋 API Reference** - Complete method and interface documentation  
<a href="docs/API.md" style="color: #007acc; text-decoration: none;">→ Browse API Documentation</a>

**💡 Usage Example** - Practical implementation examples and guides  
<a href="docs/EXAMPLES.md" style="color: #007acc; text-decoration: none;">→ View Usage Examples</a>

**🔧 Troubleshoot and Debug** - Problem solutions and debugging tools  
<a href="docs/DEBUG.md" style="color: #007acc; text-decoration: none;">→ Access Troubleshooting Guide</a>

</div>

---

## 📦 Installation

### Quick Install

```bash
# Using npm
npm install @aliakhgar1/react-native-ikev2

# Using yarn
yarn add @aliakhgar1/react-native-ikev2

# Install iOS dependencies (iOS only)
cd ios && pod install
```

### 🎯 Prerequisites

**React Native:** 0.68+  
**Android:** API 24+  
**iOS:** 15.1+

---

## 🚀 Quick Start

Get up and running with IKEv2 VPN connectivity in under 5 minutes:

```typescript
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

// 1. Check permissions (Android only)
const connectToVPN = async () => {
  if (Platform.OS === 'android') {
    const isPrepared = await IKEv2.isPrepared();
    if (!isPrepared) {
      const granted = await IKEv2.prepare();
      if (!granted) return; // User denied permission
    }
  }

  // 2. Connect with minimal configuration
  await IKEv2.connect({
    address: 'your-vpn-server.com',
    username: 'your-username',
    password: 'your-password',
    
    iOSOptions: {
      localizedDescription: 'My IKEv2 Connection',
      disconnectOnSleep: false,
      onDemandEnabled: false,
    },
    
    androidOptions: {
      connectionName: 'My IKEv2 VPN',
      AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
      Notification: {
        openActivityPackageName: 'com.yourapp.MainActivity',
        titleConnected: '🔒 Secure Connection Active',
        titleConnecting: '🔄 Connecting to VPN...',
        showDisconnectAction: true,
      },
    },
  });
};

// 3. Listen for connection state changes
const subscription = IKEv2.addIKev2StateChangeListener((state) => {
  switch (state.state) {
    case IKEv2.ConnectionState.CONNECTED:
      console.log('🟢 VPN Connected Successfully!');
      break;
    case IKEv2.ConnectionState.DISCONNECTED:
      console.log('🔴 VPN Disconnected');
      break;
    case IKEv2.ConnectionState.CONNECTING:
      console.log('🟡 Connecting to VPN...');
      break;
  }
});

// 4. Disconnect when done
await IKEv2.disconnect();
subscription.remove();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
