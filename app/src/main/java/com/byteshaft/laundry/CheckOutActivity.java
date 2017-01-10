package com.byteshaft.laundry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.laundry.account.LoginActivity;
import com.byteshaft.laundry.laundry.OrderItem;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.WebServiceHelpers;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import static com.byteshaft.laundry.AddressesActivity.sAddressId;
import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.order;


public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private int weight = 2;
    private Button selectLocation;
    private static final int PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int DROP_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static double sPickLocationLatitude = 0.0;
    public static double sPickLocationLongitude = 0.0;
    public static double sDropLocationLatitude = 0.0;
    public static double sDropLocationLongitude = 0.0;
    public static boolean pickOption = false;
    private Button sendButton;
    private ListView listView;
    private ArrayList<Integer> keysArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectLocation = (Button) findViewById(R.id.select_location);
        listView = (ListView) findViewById(R.id.order_list);
        selectLocation.setOnClickListener(this);
        sendButton = (Button) findViewById(R.id.send);
        selectLocation.setTypeface(AppGlobals.typefaceNormal);
        sendButton.setTypeface(AppGlobals.typefaceNormal);
        sendButton.setOnClickListener(this);
        keysArrayList = new ArrayList<>();
        for (Map.Entry<Integer, OrderItem> map : order.entrySet()) {
            keysArrayList.add(map.getKey());
        }
        Adapter adapter = new Adapter(getApplicationContext(), R.layout.delegate_order_list, keysArrayList);
        listView.setAdapter(adapter);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_location:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    if (!locationEnabled()) {
                        // notify user
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage("Location is not enabled");
                        dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                //get gps
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub

                            }
                        });
                        dialog.show();
                    } else {
                        pickOption = true;
                        if (AppGlobals.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), AddressesActivity.class));
                        } else {
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Not logged in !");
                            alertDialogBuilder.setMessage("Do you want to login?");
                            alertDialogBuilder.setCancelable(false);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                            });
                            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
                break;
            case R.id.send:
                if (sAddressId == -1) {
                    Toast.makeText(this, "please select your address", Toast.LENGTH_SHORT).show();
                    break;
                }
                JSONArray jsonArray = new JSONArray();
                for (Integer key : keysArrayList) {
                    OrderItem orderItem = order.get(key);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        System.out.println(jsonObject + "object");
                        jsonObject.put("item", orderItem.getId());
                        jsonObject.put("quantity", orderItem.getQuantity());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(jsonArray + "array");
                orderRequest(jsonArray);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION:
            case DROP_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION) {
                        if (!locationEnabled()) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setMessage("Location is not enabled");
                            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                    //get gps
                                }
                            });
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            dialog.show();
                        } else {
                            pickOption = true;
                            startActivity(new Intent(getApplicationContext(), PickLDropLaundryActivity.class));
                        }
                    } else {
                        if (!locationEnabled()) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setMessage("Location is not enabled");
                            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                    //get gps
                                }
                            });
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            dialog.show();
                        } else {
                            pickOption = false;
                            startActivity(new Intent(getApplicationContext(), PickLDropLaundryActivity.class));
                        }
                    }


                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean locationEnabled() {
        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    private class Adapter extends ArrayAdapter<Integer> {

        private ArrayList<Integer> orderData;
        private ViewHolder viewHolder;

        public Adapter(Context context, int resource, ArrayList<Integer> orderData) {
            super(context, resource);
            this.orderData = orderData;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_order_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.order_name);
                viewHolder.quantity = (TextView) convertView.findViewById(R.id.order_quantity);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.order_image);
                viewHolder.price = (TextView) convertView.findViewById(R.id.order_price);
                viewHolder.name.setTypeface(AppGlobals.typefaceNormal);
                viewHolder.quantity.setTypeface(AppGlobals.typefaceNormal);
                viewHolder.price.setTypeface(AppGlobals.typefaceNormal);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OrderItem orderItem = order.get(orderData.get(position));
            String titleLowerCase = orderItem.getName();
            String firstUpper = titleLowerCase.substring(0, 1).toUpperCase() + titleLowerCase.substring(1);
            viewHolder.name.setText(firstUpper);
            viewHolder.quantity.setText("Qty: " + orderItem.getQuantity());
            int price = 0;
            Log.i("TAG", "qty " + orderItem.getQuantity());
            if (Integer.valueOf(orderItem.getQuantity()) > 1) {
                Log.i("TAG", "qty  condition matched");
                for (int i = 1; i <= Integer.valueOf(orderItem.getQuantity()); i++) {
                    price = price + Integer.valueOf(orderItem.getPrice());
                }
                viewHolder.price.setText("Total:" + price + " SAR");
            } else {
                viewHolder.price.setText("Total:" + orderItem.getPrice());
            }
            Picasso.with(AppGlobals.getContext())
                    .load(orderItem.getImageUrl())
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                        }
                    });
            return convertView;
        }

        @Override
        public int getCount() {
            return orderData.size();
        }
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
                        AppGlobals.alertDialog(CheckOutActivity.this, "Request Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getApplicationContext(), "Your request has been received", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
        }
    }

    private void orderRequest(JSONArray itemsquantity) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%slaundry/request", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(orderRequestData(itemsquantity));
        WebServiceHelpers.showProgressDialog(CheckOutActivity.this, "Sending your order..");
    }

    private String orderRequestData(JSONArray itemsquantity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", sAddressId);
            jsonObject.put("service_items", itemsquantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }

    private class ViewHolder {
        public TextView name;
        public TextView quantity;
        public TextView price;
        public ImageView imageView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }
}
