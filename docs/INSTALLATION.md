# Installation Guide

This guide provides step-by-step instructions for installing and setting up the React Native IKEv2 library in your project.

## ⚠️ Important Notice

**This library is NOT available on npm registry**. It must be installed locally by cloning the repository.

## Prerequisites

Before installing, ensure you have:

- React Native 0.79.0 or higher
- Node.js 16+ 
- npm or yarn package manager
- For iOS: Xcode 12.0+, iOS 13.0+
- For Android: Android API level 21+

## Step 1: Clone the Repository

First, clone the React Native IKEv2 repository to your local machine:

```bash
git clone https://github.com/AliAkhgar/react-native-ikev2.git
```

## Step 2: Install in Your React Native Project

Navigate to your React Native project directory and install the library locally:

```bash
cd your-react-native-project

# Install from local path
npm install ./path/to/react-native-ikev2

# Or if you cloned it to a specific location
npm install /Users/yourname/path/to/react-native-ikev2
```

**Example with absolute path:**
```bash
npm install /Users/developer/Libraries/react-native-ikev2
```

**Example with relative path:**
```bash
# If react-native-ikev2 is in the same parent directory as your project
npm install ../react-native-ikev2
```

## Step 3: Install Project Dependencies

Install the library's dependencies:

```bash
# In your React Native project
npm install

# Or with yarn
yarn install
```

## Step 4: iOS Setup

For iOS projects, install the native dependencies:

```bash
cd ios
pod install
cd ..
```

## Step 5: Platform-Specific Configuration

### iOS Configuration

1. Open your project in Xcode
2. Add required capabilities:
   - Personal VPN
   - Network Extensions
3. Configure entitlements (see [iOS Setup Guide](./docs/IOS_SETUP.md))

### Android Configuration

1. Update `AndroidManifest.xml` with required permissions and services
2. Add VPN-related styles
3. See [Android Setup Guide](./docs/ANDROID_SETUP.md) for complete configuration

## Step 6: Verify Installation

Create a simple test to verify the installation:

```typescript
// App.tsx or a test component
import React from 'react';
import { View, Text } from 'react-native';
import * as IKev2 from 'react-native-ikev2';

const TestIKev2 = () => {
  React.useEffect(() => {
    // Test that the library is properly installed
    console.log('IKev2 ConnectionState:', IKev2.ConnectionState);
    console.log('IKev2 AndroidAuthType:', IKev2.AndroidAuthType);
  }, []);

  return (
    <View>
      <Text>IKev2 Library Installed Successfully!</Text>
    </View>
  );
};

export default TestIKev2;
```

## Troubleshooting Installation

### Common Issues

**Issue**: "Module not found" error
- **Solution**: Ensure the path to the library is correct
- **Check**: Verify the library was cloned successfully

**Issue**: iOS build fails
- **Solution**: Run `pod install` in the ios directory
- **Check**: Ensure Xcode project has required capabilities

**Issue**: Android build fails  
- **Solution**: Verify all services are declared in AndroidManifest.xml
- **Check**: Ensure all required permissions are added

**Issue**: Metro bundler cache issues
- **Solution**: Clear cache and reinstall
```bash
npx react-native start --reset-cache
```

### Reinstallation

If you need to reinstall or update the library:

```bash
# Remove existing installation
npm uninstall react-native-ikev2

# Pull latest changes from repository
cd path/to/react-native-ikev2
git pull origin main

# Reinstall in your project
cd your-react-native-project
npm install ./path/to/react-native-ikev2

# For iOS
cd ios && pod install
```

## Development Setup

If you plan to modify the library:

```bash
# Clone the repository
git clone https://github.com/AliAkhgar/react-native-ikev2.git
cd react-native-ikev2

# Install dependencies
yarn install

# Build the library
yarn prepare

# Run type checking
yarn typecheck

# Link to your project for development
cd your-react-native-project
npm link ../react-native-ikev2
```

## Alternative Installation Methods

### Using npm pack

1. In the library directory:
```bash
cd react-native-ikev2
npm pack
```

2. In your project:
```bash
npm install ./path/to/react-native-ikev2-0.1.0.tgz
```

### Using yarn link

1. In the library directory:
```bash
cd react-native-ikev2
yarn link
```

2. In your project:
```bash
cd your-react-native-project
yarn link react-native-ikev2
```

## Next Steps

After successful installation:

1. 📖 Read the [Complete Documentation](../README.md)
2. 🍎 Follow [iOS Setup Guide](./IOS_SETUP.md) 
3. 🤖 Follow [Android Setup Guide](./ANDROID_SETUP.md)
4. 📚 Check [API Reference](./API_REFERENCE.md)
5. 🚀 Start implementing VPN functionality!

## Support

For installation issues, please ensure you've followed all steps correctly. This library is provided as-is with limited support.

Contact: theakhgar@gmail.com