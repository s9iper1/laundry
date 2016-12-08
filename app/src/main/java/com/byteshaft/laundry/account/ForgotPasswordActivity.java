package com.byteshaft.laundry.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.byteshaft.laundry.R;

/**
 * Created by husnain on 6/12/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private Button mRecoverButton;
    private EditText mEmail;
    private String mEmailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);
        mEmail = (EditText) findViewById(R.id.email_address);
        mRecoverButton = (Button) findViewById(R.id.recover);
        mRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                }

            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.
                matcher(mEmailString).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        }
        return valid;
    }
}
