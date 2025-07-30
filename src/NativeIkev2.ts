import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  prepare: () => Promise<boolean>;
  isPrepared: () => Promise<boolean>;
  connect: (params: ConnectionParams) => Promise<void>;
  getCurrentState: () => Promise<ConnectionState>;
  requestCurrentState: () => Promise<void>;
  disconnect: () => Promise<void>;

  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export interface ConnectionParams {
  address: string;
  username: string;
  password: string;

  /**
   * @iOS Only Params
   */
  iOSOptions: IOSConnectionOptions;
  /**
   * @Android Only
   */
  androidOptions: AndroidConnectionOptions;
}

// Starting with Capital Letter as Codegen behaviour.
export interface IOSConnectionOptions {
  localizedDescription: string;
  disconnectOnSleep: boolean;
  onDemandEnabled: boolean;
  includeAllNetworks?: boolean;
  excludeLocalNetworks?: boolean;
  excludeCellularServices?: boolean;
  excludeDeviceCommunication?: boolean;
}

export interface AndroidConnectionOptions {
  connectionName: string;
  AuthType: AndroidAuthType;
  Notification: AndroidNotificationOptions;

  MTU?: number;
  /**
   * Seprated By Space
   * e.g. "8.8.8.8 2001:4806:4806:8888"
   * @default received_from_vpn_server
   */
  DnsServers?: string;
  /**
   * If behind routers, and router delete NAT mapping to early, try 20 (seconds).
   * @default 45
   */
  NatKeepAlive?: number;

  /**
   * To Reduce request size, you can disable it.
   * It only works, if server sends it's own certificate AUTOMATICALLY.
   * @default true
   */
  sendCertificateRequest?: boolean;

  /**
   * Check online to see if server certificate has been revoked.
   * @default true
   */
  checkCerificateWithOCSP?: boolean;

  /**
   * Use CRL to check the cerificate integrity.
   * It is only used if OCSP does not yeild the result.
   * @default true
   */
  checkCertificateWithCRLs?: boolean;

  /**
   * Only route these subnets traffic to VPN.
   * Everything else wil be routed as if there is no vpn.
   * seprated by space
   * e.g. "192.168.1.0/24 2001::db8::/64"
   */
  customSubnets?: string;

  /**
   * Traffic to these subnets will not be routed to VPN, as if there is no vpn.
   * seprated by space
   * e.g. "192.168.1.0/24 2001::db8::/64"
   */
  excludeSubnets?: string;

  /**
   * If true, all apps are allowed to use VPN, selected apps and options
   *  will be ignored.
   * @default true
   */
  allAppsUseVPN?: boolean;

  /**
   *  // ONLY IF allAppsUseVPN is false.
   * If true, only selected apps are allowed to use VPN.
   * If false, selected apps are Disallowed to use VPN.
   * @default none
   */
  allowOnlySelectedAppsUseVPN?: boolean;

  /**
   * PackgeName list of selected Apps.
   */
  selectedAppsPackageNames?: string[];
}
export interface AndroidNotificationOptions {
  openActivityPackageName: string;
  showDisconnectAction?: boolean;
  titleDisconnectButton?: string;

  showPauseAction?: boolean;
  showTimer?: boolean;

  titleConnecting?: string;
  titleConnected?: string;
  titleDisconnecting?: string;
  titleDisconnected?: string;
  titleError?: string;
}

export enum AndroidAuthType {
  IKEv2_EAP = 'ikev2-eap',
  IKEv2_BYOD_EAP = 'ikev2-byod-eap',
}

export interface ConnectionStateListenerCallback {
  state: ConnectionState;
}

export enum ConnectionState {
  DISCONNECTED = '0',
  DISCONNECTING = '1',
  CONNECTING = '2',
  CONNECTED = '3',
  INVALID = '-1',
  ERROR = '-2',
}

export enum CharonErrorState {
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

export default TurboModuleRegistry.getEnforcing<Spec>('Ikev2');
