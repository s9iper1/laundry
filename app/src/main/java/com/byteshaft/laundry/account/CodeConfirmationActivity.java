package com.byteshaft.laundry.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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


public class CodeConfirmationActivity extends Activity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private Button mSubmitButton;
    private EditText mEmail;
    private EditText mCode;
    private String mConfirmationEmail;
    private String mConformationCode;

    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.confirmation_code_activity);
        mEmail = (EditText) findViewById(R.id.et_confirmation_code_email);
        mCode = (EditText) findViewById(R.id.et_confirmation_code);
        mSubmitButton = (Button) findViewById(R.id.btn_confirmation_code_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmationEmail = mEmail.getText().toString();
                mConformationCode = mCode.getText().toString();
                System.out.println(mConfirmationEmail);
                System.out.println(mConformationCode);
                if (validateConfirmationCode()) {
                    activateUser(mConfirmationEmail, mConformationCode);
                }
            }
        });

        mEmail.setText(RegisterActivity.mEmailAddressString);
        mConfirmationEmail = RegisterActivity.mEmailAddressString;
    }

    @Override
    public void onBackPressed() {
        finish();
        MainActivity.getInstance().finish();
        super.onBackPressed();
    }

    public boolean validateConfirmationCode() {
        boolean valid = true;
        if (mConformationCode.isEmpty() || mConformationCode.length() < 4) {
            mCode.setError("Minimum 4 Characters");
            valid = false;
        } else {
            mCode.setError(null);
        }
        return valid;
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                WebServiceHelpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getApplicationContext(), "Please enter correct account activation key", Toast.LENGTH_LONG).show();
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_FULLNAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);

                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULLNAME, username);
                            Log.i("user name", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULLNAME));
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            Log.i("token", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
                            RegisterActivity.getInstance().finish();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }


    private void activateUser(String email, String emailOtp) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST",  String.format("%suser/activate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
        WebServiceHelpers.showProgressDialog(CodeConfirmationActivity.this, "Activating User");
    }


    private String getUserActivationData(String email, String emailOtp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailOtp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        System.out.println(request.getStatus());
        switch (request.getStatus()) {

        }
    }
}
