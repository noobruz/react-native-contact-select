import React from 'react';
import { View, Text, Button } from 'react-native';
import ContactModule from 'react-native-contact-select'; // Adjust the path accordingly

function ContactSelector() {
  const handleContactSelection = (error: any, phoneNumber: any) => {
    if (error) {
      console.error(error);
    } else {
      console.log('Selected phone number:', phoneNumber);
      // Do something with the selected contact information
    }
  };

  const pickContact = () => {
    ContactModule.pickContact(handleContactSelection);
  };

  return (
    <View>
      <Text>Contact Selector</Text>
      <Button title="Pick Contact" onPress={pickContact} />
    </View>
  );
}

export default ContactSelector;
