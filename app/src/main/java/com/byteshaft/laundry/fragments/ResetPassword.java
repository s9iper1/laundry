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

public class ResetPassword extends Fragment {

    private View mBaseView;
    private EditText mNewPassword;
    private EditText mOldPassword;
    private EditText mEmail;
    private Button mResetButton;

    private String mEmailAddressString;
    private String mOldPasswordString;
    private String mPasswordString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.reset_password, container, false);
        setHasOptionsMenu(true);
        mEmail = (EditText) mBaseView.findViewById(R.id.email_address);
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmail.setEnabled(false);
        mOldPassword = (EditText) mBaseView.findViewById(R.id.old_password);
        mNewPassword = (EditText) mBaseView.findViewById(R.id.password);
        mResetButton = (Button) mBaseView.findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEditText()) {
                }

            }
        });
        return mBaseView;
    }


    private boolean validateEditText() {

        boolean valid = true;
        mPasswordString = mNewPassword.getText().toString();
        mOldPasswordString = mOldPassword.getText().toString();
        mEmailAddressString = mEmail.getText().toString();


        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mNewPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mNewPassword.setError(null);
        }

        if (mOldPasswordString.trim().isEmpty() || mOldPasswordString.length() < 3) {
            mOldPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mOldPassword.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        return valid;
    }
}
