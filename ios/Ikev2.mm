#import "Ikev2.h"
#import "Ikev2-Swift.h"


@interface Ikev2 ()<RntDelegate>
@end

@implementation Ikev2{
  RntImpl *moduleImpl;
}

-(instancetype) init {
  self = [super init];
  if(self){
    moduleImpl = [RntImpl new];
    moduleImpl.delegate = self;
  }
  return self;
}


RCT_EXPORT_MODULE()

//setups
+ (BOOL)requiresMainQueueSetup {
  return NO;
}


- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
  [moduleImpl prepare];
  return std::make_shared<facebook::react::NativeIkev2SpecJSI>(params);
}


//functions

- (void)connect:(JS::NativeIkev2::ConnectionParams &)params resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  
  bool disconnectOnSleep = params.iOSOptions().disconnectOnSleep();
  bool onDemandEnabled = params.iOSOptions().onDemandEnabled();
  bool includeAllNetworks = params.iOSOptions().includeAllNetworks().value_or(false);
  bool excludeLocalNetworks = params.iOSOptions().excludeLocalNetworks().value_or(false);
  bool excludeCellularServices = params.iOSOptions().excludeCellularServices().value_or(false);
  bool excludeDeviceCommunication = params.iOSOptions().excludeDeviceCommunication().value_or(false);
  
  ConnectionParameters *swiftParams =[[ConnectionParameters alloc]
   initWithAddress:[params.address() copy]
   username:[params.username() copy]
   password:[params.password() copy]
   localizedDescription:[params.iOSOptions().localizedDescription() copy]
   disconnectOnSleep: disconnectOnSleep
   onDemandEnabled: onDemandEnabled
   includeAllNetworks: includeAllNetworks
   excludeLocalNetworks:excludeLocalNetworks
   excludeCellularServices:excludeCellularServices
   excludeDeviceCommunication:excludeDeviceCommunication];
  
  NSString* _ = [moduleImpl connectWithParams:swiftParams];
  resolve(nil);
}

- (void)disconnect:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  NSString* _ = [moduleImpl disconnect];
  resolve(nil);
}

- (void)getCurrentState:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  resolve([moduleImpl getCurrentState]);
}

- (void)requestCurrentState:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  [moduleImpl requestCurrentState];
  resolve(nil);
}



/**
 * Not Required For iOS
 */
- (void)isPrepared:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
}
- (void)prepare:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
}



- (NSArray<NSString *> *)supportedEvents {
  return @[ @"VPNStateIkev2" ];
}
- (void)sendMessageToJS:(NSDictionary *)message {
  [self sendEventWithName:@"VPNStateIkev2" result:message];
}

- (void)sendEventWithName:(NSString * _Nonnull)name result:(NSDictionary * _Nonnull)result  {
  [self sendEventWithName:name body:result];
}


@end
