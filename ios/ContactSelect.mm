#import "ContactSelect-Bridging-Header.h"

@implementation ContactSelect

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(pickContact:(RCTResponseSenderBlock)callback)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    CNContactPickerViewController *contactPicker = [[CNContactPickerViewController alloc] init];
    contactPicker.delegate = self;

    UIViewController *rootViewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    [rootViewController presentViewController:contactPicker animated:YES completion:nil];

    // Save the callback for later use
    self.contactSelectionCallback = callback;
  });
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContact:(CNContact *)contact
{
  NSString *phoneNumber = @"";
  for (CNLabeledValue<CNPhoneNumber *> *phone in contact.phoneNumbers) {
    phoneNumber = [phone.value stringValue];
    break; // Use the first phone number, you can modify this based on your requirements
  }

  [self.contactSelectionCallback @[[NSNull null], phoneNumber]];
}

- (void)contactPickerDidCancel:(CNContactPickerViewController *)picker
{
  [self.contactSelectionCallback @[@"Contact selection canceled"]];
}

@end
