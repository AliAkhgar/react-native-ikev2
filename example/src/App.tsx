/**
 * Not yet compatible with React-Native-Control-Tower.
 */

import React from 'react';
import {
  Text,
  View,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
  Image,
  Alert,
  SafeAreaView,
  PermissionsAndroid,
} from 'react-native';
import * as IKev2 from 'react-native-ikev2';

/**
 *Only  to get enum title to show you.
 *Must use enum values to coordinate application.
 */

const getKeyName = (enumValue: IKev2.ConnectionState): string | undefined => {
  return Object.keys(IKev2.ConnectionState).find(
    (key: any) =>
      IKev2.ConnectionState[key as keyof typeof IKev2.ConnectionState] ===
      enumValue
  );
};

export default function App() {
  const [isPrepared, setIsPrepared] = React.useState(false);
  const [vpnState, setVPNState] = React.useState<IKev2.ConnectionState>();

  const [username, setUsername] = React.useState('godping__');
  const [password, setPassword] = React.useState('_');
  const [address, setAddress] = React.useState('p87074c.google.ir');

  React.useEffect(() => {
    PermissionsAndroid.request('android.permission.POST_NOTIFICATIONS');
    IKev2.isPrepared().then((prepared) => {
      setIsPrepared(prepared);
    });

    var IKev2Sub = IKev2.addIKev2StateChangeListener((IKev2State) => {
      setVPNState(IKev2State?.state);
      return;
      //Example For How to check States Properly.
      // if (IKev2State.state === IKev2.ConnectionState.DISCONNECTED) {
      //   Alert.alert('Ikev2', 'it is dc!');
      //   return;
      // }
    });
    IKev2.requestCurrentState();

    return () => {
      IKev2Sub.remove();
    };
  }, []);

  const requestPrepare = () => {
    IKev2.prepare()
      .then((result: any) => {
        Alert.alert('IKev2', 'prepare Result:' + result);
      })
      .catch((e) => {
        Alert.alert('IKev2', 'err' + e);
      });
  };

  const runIsPrepared = () => {
    IKev2.isPrepared().then((prepared) => {
      setIsPrepared(prepared);
    });
  };

  const getCurrentState = async () => {
    try {
      const Ikev2State = await IKev2.getCurrentState();
      setVPNState(Ikev2State);
    } catch (e) {
      Alert.alert('IKev2', 'error current state: ' + e?.toString());
    }
  };

  const requestCurrentState = async () => {
    try {
      await IKev2.requestCurrentState();
    } catch (e) {
      Alert.alert('IKev2', 'error current state: ' + e?.toString());
    }
  };

  const connect = () => {
    IKev2.connect({
      username: username,
      password: password,
      address: address,
      androidOptions: {
        connectionName: 'Hey!',
        MTU: 1480,
        AuthType: IKev2.AndroidAuthType.IKEv2_EAP,
        checkCerificateWithOCSP: false,
        checkCertificateWithCRLs: false,

        //now all apps allow is disabled
        allAppsUseVPN: false,
        // only these packages are allowed.
        allowOnlySelectedAppsUseVPN: true,
        selectedAppsPackageNames: ['com.example', 'com.android.chrome'],

        Notification: {
          openActivityPackageName: 'ikev2.example.MainActivity',
          titleConnecting: 'Connecting...',
          titleConnected: 'Connected!!',
          showDisconnectAction: true,
          titleDisconnectButton: 'Disconnect me',
          showTimer: true,
        },
      },
      iOSOptions: {
        localizedDescription: 'MyVPNBaby',
        disconnectOnSleep: false,
        onDemandEnabled: false,
      },
    })
      .then((_) => {})
      .catch((e) => Alert.alert('e', e.toString()));
  };

  const disconnect = () => {
    IKev2.disconnect();
  };

  return (
    <ScrollView style={{ flex: 1 }}>
      <SafeAreaView style={styles.container}>
        <Image style={{ width: 200, height: 100 }} source={{ uri: ik2 }} />
        <Text
          style={{
            color: 'white',
            fontSize: 20,
            fontWeight: '600',
            backgroundColor: 'darkgreen',
            borderWidth: 2,
            borderBottomWidth: 0,
          }}
        >
          TurboModule
        </Text>
        <Text
          style={{
            color: 'white',
            fontSize: 20,
            fontWeight: '600',
            backgroundColor: 'red',
            borderWidth: 2,
          }}
        >
          IKev2 SDK
        </Text>

        <View
          style={{
            backgroundColor: 'lightgray',
            width: '90%',
            alignItems: 'center',
            marginTop: 10,
            borderWidth: 1,
          }}
        >
          <Text style={{ color: 'green' }}>@Android Only</Text>
          <View style={{ flexDirection: 'row' }}>
            <TouchableOpacity
              onPress={requestPrepare}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>Prepare</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={runIsPrepared}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>is Prepared?</Text>
            </TouchableOpacity>
          </View>

          <Text style={{ marginTop: 10 }}>isPrepared : {isPrepared + ''}</Text>
        </View>

        <View
          style={{
            backgroundColor: 'lightgray',
            width: '90%',
            alignItems: 'center',
            marginTop: 10,
            borderWidth: 1,
          }}
        >
          <Text style={{ color: 'red' }}>State Management</Text>
          <View style={{ flexDirection: 'row' }}>
            <TouchableOpacity
              onPress={getCurrentState}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>Get State</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={requestCurrentState}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>Request State</Text>
            </TouchableOpacity>
          </View>

          <Text style={{ marginTop: 10 }}>
            State : {getKeyName(vpnState!!)}
          </Text>
        </View>

        <View
          style={{
            backgroundColor: 'lightgray',
            width: '90%',
            alignItems: 'center',
            marginTop: 10,
            borderWidth: 1,
          }}
        >
          <Text style={{ color: 'blue', fontWeight: '600' }}>
            Connection Control
          </Text>
          <View style={{ flexDirection: 'row' }}>
            <TouchableOpacity
              onPress={connect}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>Connect</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={disconnect}
              style={{
                marginTop: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                margin: 3,
              }}
            >
              <Text>Disconnect</Text>
            </TouchableOpacity>
          </View>

          <Text style={{ marginTop: 10 }}></Text>
        </View>

        <View
          style={{
            backgroundColor: 'lightgray',
            width: '90%',
            alignItems: 'center',
            marginTop: 10,
            borderWidth: 1,
          }}
        >
          <Text style={{ color: 'blue', fontWeight: '600' }}>
            Connection Data
          </Text>
          <TextInput
            value={address}
            onChangeText={(t) => setAddress(t)}
            placeholder="Server"
            style={{
              textAlign: 'center',
              borderRadius: 10,
              marginTop: 10,
              justifyContent: 'center',
              alignItems: 'center',
              backgroundColor: 'lightblue',
              padding: 10,
              width: '30%',
              margin: 3,
            }}
          />
          <View style={{ flexDirection: 'row', marginTop: 5 }}>
            <TextInput
              value={username}
              onChangeText={(t) => setUsername(t)}
              placeholder="Username"
              style={{
                borderRadius: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                width: '30%',
                margin: 3,
              }}
            />

            <TextInput
              value={password}
              onChangeText={(t) => setPassword(t)}
              placeholder="Password"
              style={{
                borderRadius: 10,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'lightblue',
                padding: 10,
                width: '30%',
                margin: 3,
              }}
            />
          </View>

          <Text style={{ marginTop: 10 }}></Text>
        </View>
      </SafeAreaView>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    // justifyContent: 'center',
    paddingBottom: 100,
  },
});

const ik2 = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAYIAAAC6CAYAAACjpEr9AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDcuMS1jMDAwIDc5LmRhYmFjYmIsIDIwMjEvMDQvMTQtMDA6Mzk6NDQgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCAyMi41IChNYWNpbnRvc2gpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjc0NzY0QkU2MzQyNTExRUM4NUYzREJBRTFCMzAwNjJCIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjFCNkVDNDZFMzQyNjExRUM4NUYzREJBRTFCMzAwNjJCIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NzQ3NjRCRTQzNDI1MTFFQzg1RjNEQkFFMUIzMDA2MkIiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NzQ3NjRCRTUzNDI1MTFFQzg1RjNEQkFFMUIzMDA2MkIiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz7n3yyYAABBi0lEQVR42uy9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`;
