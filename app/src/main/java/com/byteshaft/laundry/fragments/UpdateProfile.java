package com.byteshaft.laundry.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.Helpers;
import com.byteshaft.laundry.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateProfile extends Fragment {

    private View mBaseView;
    private Button mUpdateButton;
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailAddress;
    private EditText mZipCode;
    private EditText mPhoneNumber;

    private String mUsernameString;
    private String mFirstNameString;
    private String mLastNameString;
    private String mEmailAddressString;
    private String mZipCodeString;
    private String mUserIdString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID);
    private String mPhoneNumberString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.update_profile, container, false);
        setHasOptionsMenu(true);
        mUsername = (EditText) mBaseView.findViewById(R.id.user_name);
        mEmailAddress = (EditText) mBaseView.findViewById(R.id.email);
        mPhoneNumber = (EditText) mBaseView.findViewById(R.id.phone);
        mUpdateButton = (Button) mBaseView.findViewById(R.id.update_button);
        mUsername.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULLNAME));
        mEmailAddress.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mPhoneNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PHONE_NUMBER));
        mEmailAddress.setEnabled(false);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsernameString = mUsername.getText().toString();
                mEmailAddressString = mEmailAddress.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();


            }
        });
        return mBaseView;
    }
}
