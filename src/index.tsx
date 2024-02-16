import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-contact-select' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ContactSelectModule = NativeModules.ContactSelectModule
  ? NativeModules.ContactSelectModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export default {
  selectContact: () => {
    return new Promise(async (resolve, reject) => {
      ContactSelectModule.pickContact((error: string, number: string) => {
        if (error) {
          reject(error);
        }

        resolve(number);
      });
    });
  },
};
