package com.contactselect;

import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactSelectModule extends ReactContextBaseJavaModule {
    private static final int PICK_CONTACT_REQUEST = 1;
    private Callback contactSelectionCallback;

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == PICK_CONTACT_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    if (contactUri != null) {
                        Cursor cursor = activity.getContentResolver().query(contactUri, null, null, null, null);
                        if (cursor != null) {
                            try {
                                if (cursor.moveToFirst()) {
                                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                                    String phoneNumber = getContactPhoneNumber(contactId);
                                    contactSelectionCallback.invoke(null, phoneNumber);
                                } else {
                                    contactSelectionCallback.invoke("Contact details not found");
                                }
                            } finally {
                                cursor.close();
                            }
                        } else {
                            contactSelectionCallback.invoke("Failed to retrieve contact details");
                        }
                    } else {
                        contactSelectionCallback.invoke("Failed to retrieve contact");
                    }
                } else {
                    contactSelectionCallback.invoke("Contact selection canceled");
                }
            }
        }
    };

    public ContactSelectModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(activityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return "ContactSelectModule";
    }

    @ReactMethod
    public void pickContact(Callback callback) {
        contactSelectionCallback = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, PICK_CONTACT_REQUEST);
        } else {
            openContactPicker();
        }
    }

    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        getCurrentActivity().startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    private String getContactPhoneNumber(String contactId) {
        Cursor cursor = getCurrentActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }
}
