package com.byteshaft.laundry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.ExpandableListAdapter;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressesActivity extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener {

    private Button addAddress;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private ProgressDialog progress;
    private String mToken;
    private JSONArray array;
    public static int sSelectedPosition = -1;
    public static int sAddressId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToken = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN);
        addAddress = (Button) findViewById(R.id.add_location);
        addAddress.setTypeface(AppGlobals.typefaceNormal);
        addAddress.setOnClickListener(this);
        expListView = (ExpandableListView) findViewById(R.id.address_list);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(l);
                    int childPosition = ExpandableListView.getPackedPositionChild(l);
                    Log.i("TAG", "group position" + groupPosition);
                    try {
                        JSONObject jsonObject = array.getJSONObject(groupPosition);
                        Log.i("TAG", "id " + jsonObject.getString("id"));
                        sAddressId = jsonObject.getInt("id");
                        sSelectedPosition = groupPosition;
                        listAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(mToken);
        getLocationData();
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
                    array = new JSONArray(request.getResponseText());
                    listAdapter = new ExpandableListAdapter(this, array);
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                expListView.setAdapter(listAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }
}
