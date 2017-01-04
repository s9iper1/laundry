package com.byteshaft.laundry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddressesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        addAddress = (Button) findViewById(R.id.add_location);
        addAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_location:
                // TODO: 04/01/2017 add location 
        }
    }
}
