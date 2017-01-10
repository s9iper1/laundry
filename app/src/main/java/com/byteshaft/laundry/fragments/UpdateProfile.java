package com.byteshaft.laundry.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.laundry.MainActivity;
import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.WebServiceHelpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class UpdateProfile extends Fragment implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener {

    private View mBaseView;

    private Button mUpdateButton;
    private EditText mUsername;
    private EditText mEmailAddress;
    private EditText mPhoneNumber;
    private EditText mPassword;
    private EditText mVerifyPassword;

    private String mUsernameString;
    private String mEmailAddressString;
    private String mPhoneNumberString;
    private String mPasswordString;
    private String mVerifyPasswordString;

    private HttpRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.update_profile, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Update Profile");
        //initialization
        mUsername = (EditText) mBaseView.findViewById(R.id.user_name);
        mEmailAddress = (EditText) mBaseView.findViewById(R.id.email);
        mPhoneNumber = (EditText) mBaseView.findViewById(R.id.phone);
        mPassword = (EditText) mBaseView.findViewById(R.id.password);
        mVerifyPassword = (EditText) mBaseView.findViewById(R.id.verify_password);
        mUpdateButton = (Button) mBaseView.findViewById(R.id.update_button);
        mUsername.setTypeface(AppGlobals.typefaceNormal);
        mEmailAddress.setTypeface(AppGlobals.typefaceNormal);
        mPhoneNumber.setTypeface(AppGlobals.typefaceNormal);
        mPassword.setTypeface(AppGlobals.typefaceNormal);
        mVerifyPassword.setTypeface(AppGlobals.typefaceNormal);
        mUpdateButton.setTypeface(AppGlobals.typefaceNormal);

        //getting user saved data from sharedPreference
        mUsername.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME));
        mEmailAddress.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mPhoneNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PHONE_NUMBER));
        mEmailAddress.setEnabled(false);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
                mUsernameString = mUsername.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();
                if (validateEditText()) {

                }
            }
        });
        return mBaseView;
    }
    private boolean validateEditText() {

        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();


        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 3 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }
        return valid;
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                WebServiceHelpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Registration Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER);

                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, username);
                            Log.i("user name", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME));
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void updateUser() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", "http://178.62.87.25/api/user/me");
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        WebServiceHelpers.showProgressDialog(getActivity(), "Updating User Profile");
    }

    private String updateUserData(String username, String password, String phoneNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", username);
            jsonObject.put("phone_number", phoneNumber);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();

    }
}
