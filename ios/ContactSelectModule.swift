import Foundation
import Contacts
import React

@objc(ContactSelectModule)
class ContactSelectModule: NSObject, RCTBridgeModule, CNContactPickerDelegate {

  var contactSelectionCallback: RCTResponseSenderBlock?

  static func moduleName() -> String {
    return "ContactSelectModule"
  }

  @objc func pickContact(_ callback: @escaping RCTResponseSenderBlock) {
    DispatchQueue.main.async {
      let contactPicker = CNContactPickerViewController()
      contactPicker.delegate = self

      guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
        callback(["Failed to present contact picker"])
        return
      }

      rootViewController.present(contactPicker, animated: true, completion: nil)

      // Save the callback for later use
      self.contactSelectionCallback = callback
    }
  }

  func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
    var phoneNumber = ""
    for phone in contact.phoneNumbers {
      phoneNumber = phone.value.stringValue
      break // Use the first phone number, you can modify this based on your requirements
    }

    self.contactSelectionCallback?([NSNull(), phoneNumber])
  }

  func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
    self.contactSelectionCallback?(["Contact selection canceled"])
  }

  @objc func constantsToExport() -> [AnyHashable: Any] {
    return [:]
  }
}
