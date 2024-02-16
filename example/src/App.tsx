import React from 'react';
import { View, Text, Button, PermissionsAndroid } from 'react-native';
import ContactModule from 'react-native-contact-select';

function ContactSelector() {
  const pickContact = async () => {
    const granted = await PermissionsAndroid.request(
      'android.permission.READ_CONTACTS',
      {
        title: 'Cool Contact App Contact Permission',
        message: 'Cool Contact App  needs access to your contacts ',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      try {
        const phone = await ContactModule.selectContact();
        console.log('Selected phone number:', phone);
      } catch (error) {
        console.error(error);
      }
    } else {
      console.log('contact permission denied');
    }
  };

  return (
    <View>
      <Text>Contact Selector</Text>
      <Button title="Pick Contact" onPress={pickContact} />
    </View>
  );
}

export default ContactSelector;
