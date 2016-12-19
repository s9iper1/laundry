package com.byteshaft.laundry.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

/**
 * Created by husnain on 12/9/16.
 */

public class ResetPassword extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText mNewPassword;
    private EditText mOldPassword;
    private EditText mEmail;
    private Button mResetButton;

    private String mEmailAddressString;
    private String mOldPasswordString;
    private String mPasswordString;

    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reset_password);
        mEmail = (EditText) findViewById(R.id.email_address);
        mOldPassword = (EditText) findViewById(R.id.old_password);
        mNewPassword = (EditText) findViewById(R.id.password);
        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEditText()) {
                    System.out.println("working");
                    changePassword(mEmailAddressString, mOldPasswordString, mOldPasswordString);
                }
            }
        });
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmail.setEnabled(false);
        mEmailAddressString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
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

    private void changePassword(String email, String emailotp, String newpassword) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/change-password", AppGlobals.BASE_URL));
        request.send(getUserChangePassword(email, emailotp, newpassword));
        WebServiceHelpers.showProgressDialog(ResetPassword.this, "Resetting your password");
    }


    private String getUserChangePassword(String email, String emailotp, String newpassword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailotp);
            jsonObject.put("new_password", newpassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onError(HttpRequest request, int readyStat, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                WebServiceHelpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(ResetPassword.this, "Resetting Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(ResetPassword.this, "Resetting Failed!" , "old Password is wrong");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(ResetPassword.this, "Your password successfully changed", Toast.LENGTH_SHORT).show();
                }
        }
    }

}
