# 📦 Installation Guide

This comprehensive guide will walk you through the installation and setup process for React Native IKEv2.

## 🔧 Prerequisites

Before installing the library, ensure you have the following requirements:

### Minimum Requirements
- **React Native** 0.68 or higher
- **iOS** 15.1+ (for iOS development)
- **Android** API Level 24+ (Android 7.0+)

## 📥 Installation Steps

### Step 1: Install React-Native-IKEv2

```bash
npm i --save @aliakhgar1/react-native-ikev2
OR
yarn add @aliakhgar1/react-native-ikev2
```

### Step 2: iOS Setup

#### Install CocoaPods Dependencies
```bash
cd ios && pod install
```

#### Configure App Capabilities

**For Your Main App Target:**
1. Open your project in Xcode
2. Select your App target
3. Go to **Signing & Capabilities**
4. Add **Network Extensions** capability
5. Enable **Personal VPN**

<img width="611" height="598" alt="iOS Capabilities Setup" src="https://via.placeholder.com/611x598/f0f0f0/333333?text=iOS+Capabilities+Setup" />

> **💡 Note:** Unlike OpenVPN, IKEv2 uses the built-in iOS VPN APIs and does **not require a separate Network Extension target**. The native `NEVPNManager` handles IKEv2 connections directly within your main app.

#### Required Entitlements

**Main App Entitlements:**
```xml
<!-- YourApp.entitlements -->
<key>com.apple.developer.networking.vpn.api</key>
<array>
    <string>allow-vpn</string>
</array>
```

#### No Extra Setup Required! ✅

IKEv2 on iOS uses the native `NEVPNManager` APIs which are built into iOS. This means:

- ✅ No separate Network Extension target needed
- ✅ No additional Swift files to copy
- ✅ No extra package dependencies required
- ✅ No additional frameworks to link

Simply install the package, run `pod install`, configure the entitlements, and you're ready to connect!

---

### Step 3: Android Setup

> **⚠️ Note:** Detailed Android setup instructions are coming soon. The following is a placeholder with general guidance.

#### Update AndroidManifest.xml

Add required permissions to `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <!-- VPN Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    
    <!-- VPN Service Permission -->
    <uses-permission android:name="android.permission.BIND_VPN_SERVICE" 
        tools:ignore="ProtectedPermissions" />

    <!-- Your Application Tag -->
    <application>
        
        <!-- Your main activity -->
        <activity>
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- IKEv2 VPN Service configuration will be documented here -->
        
    </application>
</manifest>
```

#### Gradle Configuration

> **📝 Coming Soon:** Detailed Gradle configuration for Android IKEv2 setup.

#### Configure ProGuard (if using)

If you're using ProGuard (or R8) for code obfuscation, add the following rules to `android/app/proguard-rules.pro`:

```proguard
# React Native IKEv2
-keep class com.ikev2.** { *; }

# strongSwan (if applicable)
-keep class org.strongswan.** { *; }

# Don't warn about missing classes
-dontwarn com.ikev2.**
-dontwarn org.strongswan.**
```

> **📝 Note:** Android setup details including additional Gradle configuration, service registration, and strongSwan integration will be provided by the library maintainer.

---

## 🔍 Verification

### Test Installation

To verify the installation, simply import the library and check if it loads correctly:

```typescript
import * as IKEv2 from '@aliakhgar1/react-native-ikev2';

// Check if the module is available
console.log('IKEv2 methods:', Object.keys(IKEv2));

// On Android, check permission status
if (Platform.OS === 'android') {
  const isPrepared = await IKEv2.isPrepared();
  console.log('VPN permission status:', isPrepared ? 'Granted' : 'Not granted');
}
```

### Build and Run

```bash
# Test iOS
npx react-native run-ios

# Test Android
npx react-native run-android
```

## 🐛 Common Installation Issues

### iOS Issues

#### Issue: "Personal VPN entitlement missing"
**Solution:**
1. Ensure you've added the Network Extensions capability in Xcode
2. Make sure "Personal VPN" is checked in the capability settings
3. Verify your provisioning profile includes the VPN entitlement

#### Issue: "VPN configuration failed to save"
**Solution:**
1. Check that your app's bundle identifier matches your provisioning profile
2. Ensure you have a valid development/distribution certificate
3. Try cleaning the build folder and rebuilding

### Android Issues

#### Issue: "VPN permission denied"
**Solution:**
1. Ensure `BIND_VPN_SERVICE` permission is in AndroidManifest.xml
2. Call `IKEv2.prepare()` before connecting
3. Test on a physical device (VPN doesn't work reliably on emulators)

#### Issue: "Native module not found"
**Solution:**
1. Clean and rebuild the project:
```bash
cd android
./gradlew clean
cd ..
npx react-native run-android
```

2. Clear React Native cache:
```bash
npx react-native start --reset-cache
```

## 📱 Platform-Specific Notes

### iOS Simulator Limitations
- VPN functionality requires a physical device
- Simulator will show connection attempts but won't establish actual VPN tunnel

### Android Emulator Limitations
- VPN functionality may be limited on emulators
- Always test on physical devices for accurate behavior

## ✅ Next Steps

After successful installation:

1. **Read the [API Documentation](./API.md)** - Complete method reference
2. **Check [Usage Examples](./EXAMPLES.md)** - Practical implementation patterns
3. **Review [Troubleshooting Guide](./DEBUG.md)** - Common issues and solutions

---

Need help? Check our [Troubleshooting Guide](./DEBUG.md) or [open an issue](https://github.com/AliAkhgar/react-native-ikev2/issues).
