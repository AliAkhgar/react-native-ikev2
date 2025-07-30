#import <Ikev2Spec/Ikev2Spec.h>
#import <React/RCTEventEmitter.h>
#import <NetworkExtension/NetworkExtension.h>

@interface Ikev2 : RCTEventEmitter <NativeIkev2Spec>

@property (strong, nonatomic) NEVPNManager *vpnManager;
@property(strong, nonatomic) NSObject *vpnStateObserver;

@end
