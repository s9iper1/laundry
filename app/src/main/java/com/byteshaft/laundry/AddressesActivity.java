package com.byteshaft.laundry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.byteshaft.laundry.account.LoginActivity;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.ExpandableListAdapter;
import com.byteshaft.laundry.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressesActivity extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener {

    private Button addAddress;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private ProgressDialog progress;
    private String mToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        mToken = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN);
        getLocationData();
        addAddress = (Button) findViewById(R.id.add_location);
        addAddress.setOnClickListener(this);
        expListView = (ExpandableListView) findViewById(R.id.address_list);
    }


    private void getLocationData() {
        progress = ProgressDialog.show(this, "Please wait..",
                "Getting Locations", true);
        HttpRequest mRequest = new HttpRequest(AppGlobals.getContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("GET", AppGlobals.LOCATIONS_URL);
        mRequest.setRequestHeader("Authorization", "Token " + mToken);
        mRequest.send();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_location:
                startActivity(new Intent(this, PickLDropLaundryActivity.class));
                break;
        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                progress.dismiss();
                System.out.println(request.getResponseText());
                try {
                    JSONArray array = new JSONArray(request.getResponseText());
                    listAdapter = new ExpandableListAdapter(this, array);
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                expListView.setAdapter(listAdapter);
        }
    }
}
