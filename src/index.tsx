import { NativeEventEmitter, NativeModules } from 'react-native';
import Ikev2, {
  ConnectionState,
  type ConnectionStateListenerCallback,
  AndroidAuthType,
} from './NativeIkev2';

export const {
  isPrepared,
  connect,
  disconnect,
  prepare,
  getCurrentState,
  getConstants,
  requestCurrentState,
} = Ikev2;

export { ConnectionState, AndroidAuthType };

const IKev2StateEventKey = 'VPNStateIkev2';
const eventEmitter = new NativeEventEmitter(NativeModules.Ikev2);
export const addIKev2StateChangeListener = (
  callback: (state: ConnectionStateListenerCallback) => void
) => {
  return eventEmitter.addListener(IKev2StateEventKey, callback);
};
