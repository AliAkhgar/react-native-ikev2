# 🎯 Usage Examples

> **Complete integration examples for React Native IKEv2**  
> *Production-ready code samples and patterns for secure IKEv2 VPN integration*

## 🚀 Quick Navigation

| Example | Description | Link |
|---------|-------------|------|
| 🔌 **Basic Connection** | Simple connect/disconnect functionality | [View →](#-basic-vpn-connection) |
| ⚙️ **Advanced Config** | Enterprise-grade configuration options | [View →](#-advanced-configuration) |
| 📦 **State Management** | Minimal state management with Zustand | [View →](#-state-management) |
| 🚨 **Error Handling** | Comprehensive error management | [View →](#-error-handling) |

---

## 🔌 Basic VPN Connection

> **Simple and reliable IKEv2 VPN connection patterns**

### 📱 Simple Connect/Disconnect

```typescript
import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, Alert, StyleSheet, Platform } from 'react-native';
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

const SimpleVPN = () => {
  const [isConnected, setIsConnected] = useState(false);
  const [isConnecting, setIsConnecting] = useState(false);
  const [connectionState, setConnectionState] = useState(IKEv2.ConnectionState.DISCONNECTED);

  useEffect(() => {
    // Set up state listener
    const subscription = IKEv2.addIKev2StateChangeListener((state) => {
      setConnectionState(state.state);
      setIsConnected(state.state === IKEv2.ConnectionState.CONNECTED);
      setIsConnecting(state.state === IKEv2.ConnectionState.CONNECTING);
    });

    // Get initial state
    IKEv2.requestCurrentState();

    return () => subscription.remove();
  }, []);

  const handleConnect = async () => {
    try {
      // Android permission check
      if (Platform.OS === 'android') {
        const isPrepared = await IKEv2.isPrepared();
        if (!isPrepared) {
          const granted = await IKEv2.prepare();
          if (!granted) {
            Alert.alert('Permission Required', 'VPN permission is required to continue');
            return;
          }
        }
      }

      await IKEv2.connect({
        address: 'your-server.com',
        username: 'your-username',
        password: 'your-password',
        
        iOSOptions: {
          localizedDescription: 'My IKEv2 VPN',
          disconnectOnSleep: false,
          onDemandEnabled: false,
        },
        
        androidOptions: {
          connectionName: 'My IKEv2 VPN',
          AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
          Notification: {
            openActivityPackageName: 'com.yourapp.MainActivity',
            titleConnected: '🔒 Secure connection active',
            titleConnecting: '🔄 Establishing secure connection...',
            showDisconnectAction: true,
            titleDisconnectButton: 'Disconnect',
          },
        },
      });
    } catch (error) {
      Alert.alert('Connection Error', error.message);
    }
  };

  const handleDisconnect = async () => {
    try {
      await IKEv2.disconnect();
    } catch (error) {
      Alert.alert('Disconnect Error', error.message);
    }
  };

  const getStatusColor = () => {
    switch (connectionState) {
      case IKEv2.ConnectionState.CONNECTED: return '#4CAF50';
      case IKEv2.ConnectionState.CONNECTING: return '#FF9800';
      case IKEv2.ConnectionState.DISCONNECTING: return '#FF9800';
      case IKEv2.ConnectionState.ERROR: return '#F44336';
      default: return '#9E9E9E';
    }
  };

  const getStatusText = () => {
    switch (connectionState) {
      case IKEv2.ConnectionState.CONNECTED: return '🟢 Connected';
      case IKEv2.ConnectionState.CONNECTING: return '🟡 Connecting...';
      case IKEv2.ConnectionState.DISCONNECTING: return '🟡 Disconnecting...';
      case IKEv2.ConnectionState.ERROR: return '🔴 Connection Error';
      default: return '⚫ Disconnected';
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.statusContainer}>
        <Text style={[styles.statusText, { color: getStatusColor() }]}>
          {getStatusText()}
        </Text>
      </View>

      <View style={styles.buttonContainer}>
        {!isConnected && !isConnecting && (
          <TouchableOpacity style={styles.connectButton} onPress={handleConnect}>
            <Text style={styles.buttonText}>Connect to VPN</Text>
          </TouchableOpacity>
        )}

        {isConnecting && (
          <TouchableOpacity style={styles.disabledButton} disabled>
            <Text style={styles.buttonText}>Connecting...</Text>
          </TouchableOpacity>
        )}

        {isConnected && (
          <TouchableOpacity style={styles.disconnectButton} onPress={handleDisconnect}>
            <Text style={styles.buttonText}>Disconnect VPN</Text>
          </TouchableOpacity>
        )}
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
  },
  statusContainer: {
    marginBottom: 40,
    padding: 20,
    borderRadius: 10,
    backgroundColor: '#f5f5f5',
  },
  statusText: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  buttonContainer: {
    width: '100%',
  },
  connectButton: {
    backgroundColor: '#4CAF50',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  disconnectButton: {
    backgroundColor: '#F44336',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  disabledButton: {
    backgroundColor: '#CCCCCC',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default SimpleVPN;
```

---

## ⚙️ Advanced Configuration

> **Enterprise-grade IKEv2 VPN configuration with advanced routing and security options**

### 🏢 Enterprise VPN with Custom Routing

```typescript
import React from 'react';
import { Platform } from 'react-native';
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

const EnterpriseVPN = () => {
  const connectToEnterprise = async () => {
    try {
      // Check Android permissions
      if (Platform.OS === 'android') {
        const isPrepared = await IKEv2.isPrepared();
        if (!isPrepared) {
          const granted = await IKEv2.prepare();
          if (!granted) {
            throw new Error('VPN permission denied');
          }
        }
      }

      await IKEv2.connect({
        address: 'enterprise-vpn.company.com',
        username: 'employee@company.com',
        password: 'secure-password',
        
        iOSOptions: {
          localizedDescription: 'Company IKEv2 VPN - Secure Access',
          disconnectOnSleep: true,
          onDemandEnabled: true,
          includeAllNetworks: false,
          excludeLocalNetworks: true,
          excludeCellularServices: false,
        },
        
        androidOptions: {
          connectionName: 'Company IKEv2 VPN',
          AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
          
          // Custom DNS configuration
          DnsServers: '8.8.8.8 1.1.1.1',
          
          // Advanced routing - only route corporate subnets
          customSubnets: '10.0.0.0/8 172.16.0.0/12 192.168.0.0/16',
          excludeSubnets: '192.168.1.0/24',
          
          // Per-app VPN for corporate apps only
          allAppsUseVPN: false,
          allowOnlySelectedAppsUseVPN: true,
          selectedAppsPackageNames: [
            'com.company.secure-app',
            'com.company.internal-tools',
            'com.company.email',
          ],
          
          // Security settings
          sendCertificateRequest: true,
          checkCerificateWithOCSP: true,
          checkCertificateWithCRLs: true,
          
          // NAT settings for corporate networks
          NatKeepAlive: 20,
          
          // MTU optimization
          MTU: 1400,
          
          // Rich notification
          Notification: {
            openActivityPackageName: 'com.company.app.MainActivity',
            titleConnected: '🏢 Connected to Company Network',
            titleConnecting: '🔄 Connecting to Corporate VPN...',
            titleDisconnecting: '🔄 Disconnecting from VPN...',
            titleDisconnected: '🔴 Disconnected from Company VPN',
            titleError: '❌ VPN Connection Error',
            showDisconnectAction: true,
            titleDisconnectButton: 'Disconnect VPN',
            showTimer: true,
          },
        },
      });
      
      console.log('Enterprise IKEv2 VPN connected successfully');
    } catch (error) {
      console.error('Enterprise VPN connection failed:', error);
      throw error;
    }
  };

  return null; // Your UI implementation
};

export default EnterpriseVPN;
```

### 🌐 Split Tunneling Configuration

```typescript
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

const connectWithSplitTunneling = async () => {
  await IKEv2.connect({
    address: 'vpn.example.com',
    username: 'user',
    password: 'password',
    
    iOSOptions: {
      localizedDescription: 'Split Tunnel VPN',
      disconnectOnSleep: false,
      onDemandEnabled: false,
      // iOS split tunneling
      includeAllNetworks: false,
      excludeLocalNetworks: true,
    },
    
    androidOptions: {
      connectionName: 'Split Tunnel VPN',
      AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
      
      // Only route specific subnets through VPN
      customSubnets: '10.0.0.0/8 172.16.0.0/12',
      
      // Exclude local network and specific services
      excludeSubnets: '192.168.0.0/16',
      
      Notification: {
        openActivityPackageName: 'com.myapp.MainActivity',
        titleConnected: '🔀 Split Tunnel Active',
      },
    },
  });
};
```

---

## 📦 State Management

> **Minimal and efficient VPN state management with Zustand**

### 🗃️ Zustand VPN Store

```typescript
import { create } from 'zustand';
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

// 🏷️ Define VPN store state interface
interface VPNState {
  // Connection state
  connectionState: IKEv2.ConnectionState;
  isConnected: boolean;
  isConnecting: boolean;
  
  // Connection details
  serverAddress: string | null;
  lastError: string | null;
  
  // Actions
  setConnectionState: (state: IKEv2.ConnectionState) => void;
  setServerAddress: (address: string | null) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  reset: () => void;
}

// 🎯 Create Zustand store
export const useVPNStore = create<VPNState>((set, get) => ({
  // Initial state
  connectionState: IKEv2.ConnectionState.DISCONNECTED,
  isConnected: false,
  isConnecting: false,
  serverAddress: null,
  lastError: null,

  // Actions
  setConnectionState: (state: IKEv2.ConnectionState) => set((prev) => {
    const isConnected = state === IKEv2.ConnectionState.CONNECTED;
    const isConnecting = state === IKEv2.ConnectionState.CONNECTING;
    
    return {
      connectionState: state,
      isConnected,
      isConnecting,
      lastError: null, // Clear error on state change
    };
  }),

  setServerAddress: (address: string | null) => set({ serverAddress: address }),

  setError: (error: string | null) => set({ 
    lastError: error,
    connectionState: error ? IKEv2.ConnectionState.ERROR : get().connectionState 
  }),

  clearError: () => set({ lastError: null }),

  reset: () => set({
    connectionState: IKEv2.ConnectionState.DISCONNECTED,
    isConnected: false,
    isConnecting: false,
    serverAddress: null,
    lastError: null,
  }),
}));
```

### 📱 VPN Connection Page with Zustand

```typescript
import React, { useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Platform } from 'react-native';
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';
import { useVPNStore } from './vpn-store';

const VPNConnectionPage: React.FC = () => {
  const {
    connectionState,
    isConnected,
    isConnecting,
    serverAddress,
    lastError,
    setConnectionState,
    setServerAddress,
    setError,
    clearError,
  } = useVPNStore();

  // 🔗 Set up IKEv2 event listeners
  useEffect(() => {
    const subscription = IKEv2.addIKev2StateChangeListener((event) => {
      setConnectionState(event.state);
    });

    // 🔄 Request initial state
    IKEv2.requestCurrentState();

    return () => subscription.remove();
  }, [setConnectionState]);

  // 🔌 Connect function
  const handleConnect = async () => {
    try {
      clearError();
      setServerAddress('your-server.com');
      
      // Android permission check
      if (Platform.OS === 'android') {
        const isPrepared = await IKEv2.isPrepared();
        if (!isPrepared) {
          const granted = await IKEv2.prepare();
          if (!granted) {
            throw new Error('VPN permission denied');
          }
        }
      }

      await IKEv2.connect({
        address: 'your-server.com',
        username: 'your-username',
        password: 'your-password',
        
        iOSOptions: {
          localizedDescription: 'My IKEv2 VPN',
          disconnectOnSleep: false,
          onDemandEnabled: false,
        },
        
        androidOptions: {
          connectionName: 'My IKEv2 VPN',
          AuthType: IKEv2.AndroidAuthType.IKEv2_EAP,
          Notification: {
            openActivityPackageName: 'com.yourapp.MainActivity',
            titleConnected: '🔒 VPN Connected',
            titleConnecting: '🔄 Connecting to VPN...',
            showDisconnectAction: true,
          },
        },
      });
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Connection failed';
      setError(errorMessage);
    }
  };

  // 🔌 Disconnect function
  const handleDisconnect = async () => {
    try {
      clearError();
      await IKEv2.disconnect();
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Disconnect failed';
      setError(errorMessage);
    }
  };

  // 🎨 Get status info
  const getStatusInfo = () => {
    if (lastError) {
      return { emoji: '🔴', text: 'Error', color: '#F44336' };
    }
    
    switch (connectionState) {
      case IKEv2.ConnectionState.CONNECTED:
        return { emoji: '🟢', text: 'Connected', color: '#4CAF50' };
      case IKEv2.ConnectionState.CONNECTING:
        return { emoji: '🟡', text: 'Connecting...', color: '#FF9800' };
      case IKEv2.ConnectionState.DISCONNECTING:
        return { emoji: '🟡', text: 'Disconnecting...', color: '#FF9800' };
      default:
        return { emoji: '⚫', text: 'Disconnected', color: '#9E9E9E' };
    }
  };

  const { emoji, text, color } = getStatusInfo();

  return (
    <View style={styles.container}>
      {/* Status Display */}
      <View style={[styles.statusCard, { borderColor: color }]}>
        <Text style={styles.statusEmoji}>{emoji}</Text>
        <Text style={[styles.statusText, { color }]}>{text}</Text>
        
        {serverAddress && (
          <Text style={styles.serverText}>Server: {serverAddress}</Text>
        )}
      </View>

      {/* Error Display */}
      {lastError && (
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>❌ {lastError}</Text>
          <TouchableOpacity onPress={clearError} style={styles.clearButton}>
            <Text style={styles.clearButtonText}>Clear</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Action Buttons */}
      <View style={styles.buttonContainer}>
        {!isConnected && !isConnecting && (
          <TouchableOpacity style={styles.connectButton} onPress={handleConnect}>
            <Text style={styles.buttonText}>🔌 Connect VPN</Text>
          </TouchableOpacity>
        )}

        {isConnecting && (
          <TouchableOpacity style={styles.disabledButton} disabled>
            <Text style={styles.buttonText}>🔄 Connecting...</Text>
          </TouchableOpacity>
        )}

        {isConnected && (
          <TouchableOpacity style={styles.disconnectButton} onPress={handleDisconnect}>
            <Text style={styles.buttonText}>🔌 Disconnect VPN</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  statusCard: {
    backgroundColor: 'white',
    padding: 24,
    borderRadius: 12,
    marginBottom: 20,
    borderWidth: 2,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statusEmoji: {
    fontSize: 36,
    marginBottom: 8,
  },
  statusText: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  serverText: {
    fontSize: 14,
    color: '#666',
  },
  errorContainer: {
    backgroundColor: '#ffebee',
    padding: 12,
    borderRadius: 8,
    marginBottom: 20,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  errorText: {
    color: '#c62828',
    flex: 1,
  },
  clearButton: {
    paddingHorizontal: 12,
    paddingVertical: 4,
  },
  clearButtonText: {
    color: '#1976d2',
    fontWeight: 'bold',
  },
  buttonContainer: {
    gap: 12,
  },
  connectButton: {
    backgroundColor: '#4CAF50',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  disconnectButton: {
    backgroundColor: '#F44336',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  disabledButton: {
    backgroundColor: '#CCCCCC',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default VPNConnectionPage;
```

---

## 🚨 Error Handling

> **Comprehensive error management and user-friendly error recovery patterns**

### 🛡️ Comprehensive Error Handling

```typescript
import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert, StyleSheet, Platform, Linking } from 'react-native';
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

const VPNWithErrorHandling = () => {
  const [errors, setErrors] = useState<string[]>([]);

  const handleVPNError = (error: any, context: string) => {
    let errorMessage = 'Unknown error occurred';
    
    if (error instanceof Error) {
      errorMessage = error.message;
    } else if (typeof error === 'string') {
      errorMessage = error;
    }
    
    const fullError = `${context}: ${errorMessage}`;
    setErrors(prev => [...prev, fullError]);
    
    console.error(fullError, error);
    
    // Show user-friendly error messages
    switch (context) {
      case 'CONNECTION':
        Alert.alert(
          'Connection Failed',
          'Unable to connect to VPN server. Please check your credentials and try again.',
          [{ text: 'OK' }]
        );
        break;
        
      case 'PERMISSION':
        Alert.alert(
          'Permission Required',
          'VPN permission is required to establish connection.',
          [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Grant Permission', onPress: () => IKEv2.prepare() },
          ]
        );
        break;
        
      case 'NETWORK':
        Alert.alert(
          'Network Error',
          'Please check your internet connection and try again.',
          [{ text: 'OK' }]
        );
        break;
        
      case 'AUTHENTICATION':
        Alert.alert(
          'Authentication Failed',
          'Invalid username or password. Please check your credentials.',
          [{ text: 'OK' }]
        );
        break;
        
      default:
        Alert.alert('Error', errorMessage, [{ text: 'OK' }]);
    }
  };

  const safeConnect = async (config: IKEv2.ConnectionParams) => {
    try {
      // Android permission check with error handling
      if (Platform.OS === 'android') {
        try {
          const isPrepared = await IKEv2.isPrepared();
          if (!isPrepared) {
            const granted = await IKEv2.prepare();
            if (!granted) {
              throw new Error('User denied VPN permission');
            }
          }
        } catch (permError) {
          handleVPNError(permError, 'PERMISSION');
          return;
        }
      }
      
      // Validate configuration
      if (!config.address || !config.username || !config.password) {
        throw new Error('Missing required connection parameters');
      }
      
      // Attempt connection with timeout
      const connectionPromise = IKEv2.connect(config);
      const timeoutPromise = new Promise((_, reject) => {
        setTimeout(() => reject(new Error('Connection timeout')), 30000);
      });
      
      await Promise.race([connectionPromise, timeoutPromise]);
      
    } catch (connectionError: any) {
      // Categorize errors
      const errorMessage = connectionError.message?.toLowerCase() || '';
      
      if (errorMessage.includes('network') || errorMessage.includes('timeout')) {
        handleVPNError(connectionError, 'NETWORK');
      } else if (errorMessage.includes('auth') || errorMessage.includes('credential')) {
        handleVPNError(connectionError, 'AUTHENTICATION');
      } else if (errorMessage.includes('config') || errorMessage.includes('invalid')) {
        handleVPNError(connectionError, 'CONFIGURATION');
      } else {
        handleVPNError(connectionError, 'CONNECTION');
      }
    }
  };

  const safeDisconnect = async () => {
    try {
      await IKEv2.disconnect();
    } catch (disconnectError) {
      handleVPNError(disconnectError, 'DISCONNECTION');
    }
  };

  // Clear errors
  const clearErrors = () => {
    setErrors([]);
  };

  return (
    <View style={styles.container}>
      {/* Error display */}
      {errors.length > 0 && (
        <View style={styles.errorContainer}>
          <Text style={styles.errorTitle}>Recent Errors:</Text>
          {errors.slice(-3).map((error, index) => (
            <Text key={index} style={styles.errorText}>
              • {error}
            </Text>
          ))}
          <TouchableOpacity onPress={clearErrors}>
            <Text style={styles.clearButton}>Clear Errors</Text>
          </TouchableOpacity>
        </View>
      )}
      
      {/* Your VPN UI */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  errorContainer: {
    backgroundColor: '#ffebee',
    padding: 10,
    margin: 10,
    borderRadius: 5,
    borderLeftWidth: 4,
    borderLeftColor: '#f44336',
  },
  errorTitle: {
    fontWeight: 'bold',
    color: '#c62828',
    marginBottom: 5,
  },
  errorText: {
    color: '#d32f2f',
    fontSize: 12,
    marginBottom: 2,
  },
  clearButton: {
    color: '#1976d2',
    textAlign: 'right',
    marginTop: 5,
    textDecorationLine: 'underline',
  },
});

export default VPNWithErrorHandling;
```

### 🔄 Retry Logic with Exponential Backoff

```typescript
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

const connectWithRetry = async (
  config: IKEv2.ConnectionParams,
  maxRetries: number = 3,
  baseDelay: number = 1000
) => {
  let lastError: Error | null = null;
  
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      console.log(`🔄 Connection attempt ${attempt}/${maxRetries}...`);
      await IKEv2.connect(config);
      console.log('✅ Connected successfully!');
      return true;
    } catch (error) {
      lastError = error instanceof Error ? error : new Error(String(error));
      console.log(`❌ Attempt ${attempt} failed: ${lastError.message}`);
      
      if (attempt < maxRetries) {
        const delay = baseDelay * Math.pow(2, attempt - 1);
        console.log(`⏳ Waiting ${delay}ms before retry...`);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
  }
  
  throw lastError || new Error('Connection failed after all retries');
};

// Usage
try {
  await connectWithRetry(vpnConfig, 3, 2000);
} catch (error) {
  console.error('All connection attempts failed:', error);
}
```

---

## 🎓 Summary

> **Essential IKEv2 VPN integration patterns for React Native applications**

This guide provides practical examples for integrating React Native IKEv2 into your applications. Each example includes:

<table>
<tr><td>🔒 <strong>Security</strong></td><td>Best practices and secure configuration patterns</td></tr>
<tr><td>🎯 <strong>State Management</strong></td><td>Simple and efficient state management with Zustand</td></tr>
<tr><td>🚨 <strong>Error Handling</strong></td><td>Comprehensive error management with user-friendly recovery</td></tr>
<tr><td>📱 <strong>Cross-Platform</strong></td><td>iOS and Android specific configurations and optimizations</td></tr>
<tr><td>🎨 <strong>UI/UX</strong></td><td>Clean, responsive interfaces with real-time status indicators</td></tr>
</table>

**🚀 Next Steps:**
- Explore the [API Reference](./API.md) for detailed method documentation
- Check [Installation Guide](./INSTALLATION.md) for platform-specific setup
- Review [Troubleshooting Guide](./DEBUG.md) for common issues and solutions

---

*Built with ❤️ for secure, reliable IKEv2 VPN integration in React Native applications*
