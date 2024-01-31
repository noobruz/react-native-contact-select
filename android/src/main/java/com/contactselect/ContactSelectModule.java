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

public class ContactSelectModule extends ReactContextBaseJavaModule {
    private static final int CONTACT_REQUEST_CODE = 1;
    private Callback contactSelectionCallback;

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == CONTACT_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    if (contactUri != null) {
                        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                        Cursor cursor = activity.getContentResolver().query(contactUri, projection, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactSelectionCallback.invoke(null, phoneNumber);
                            cursor.close();
                        } else {
                            contactSelectionCallback.invoke("Failed to retrieve contact");
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
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, CONTACT_REQUEST_CODE);
        } else {
            openContactPicker();
        }
    }

    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        getCurrentActivity().startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }
}
