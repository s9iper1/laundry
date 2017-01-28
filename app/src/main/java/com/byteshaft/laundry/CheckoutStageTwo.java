package com.byteshaft.laundry;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byteshaft.laundry.account.LoginActivity;
import com.byteshaft.laundry.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.byteshaft.laundry.CheckOutActivity.sAddressId;

/**
 * Created by s9iper1 on 1/14/17.
 */

public class CheckoutStageTwo extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    private Spinner selectLocation;
    private static final int PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int DROP_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private JSONArray jsonArray;
    private ArrayList<String> locationNames;
    private String addNewLocation = "Add new address";
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static final String DATE_PICKER_TAG = "date_picker";
    private static final String TIME_PICKER_TAG = "time_picker";
    private ImageButton dropTime;
    private String dropDateString;
    private String dropTimeString;
    private boolean dropDateSelected = false;
    private TextView selectTime;
    private SwitchCompat switchCompat;
    private static final int LOCATION_OFF = 0;
    private ArrayAdapter<String> locationAdapter;
    private TextView totalPrice;
    private int totalPriceOfItems = 0;
    private int expressPrice = 0;
    private int normalPrice = 0;
    private Button sendRequest;
    private String laundryType = "normal";
    private static CheckoutStageTwo sInstance;

    public static CheckoutStageTwo getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stage_two);
        sInstance = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Checkout");
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        selectLocation = (Spinner) findViewById(R.id.spinner);
        dropTime = (ImageButton) findViewById(R.id.button_drop_time);
        selectTime = (TextView) findViewById(R.id.select_time);
        switchCompat = (SwitchCompat) findViewById(R.id.express_service);
        totalPrice = (TextView) findViewById(R.id.total_price);
        sendRequest = (Button) findViewById(R.id.send_request);
        sendRequest.setOnClickListener(this);
        switchCompat.setOnCheckedChangeListener(this);
        dropTime.setOnClickListener(this);
        switchCompat.setTypeface(AppGlobals.typefaceBold);
        refreshData();
        printMap(CheckOutActivity.sTotalPrice);
        selectLocation.setOnItemSelectedListener(this);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(CheckoutStageTwo.this, R.style.MyDialogTheme,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(CheckoutStageTwo.this,
                R.style.MyDialogTheme, this,
                calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), true);
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

    public void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            totalPriceOfItems  = totalPriceOfItems + Integer.valueOf(String.valueOf(pair.getValue()));

        }
        Log.i("TAG", "total " + totalPriceOfItems);
        totalPrice.setText(totalPriceOfItems +  " SAR");
        normalPrice = totalPriceOfItems;
        expressPrice = totalPriceOfItems + totalPriceOfItems;
    }

    private void refreshData() {
        jsonArray = AddressesActivity.jsonArray;
        locationNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                locationNames.add(AddressesActivity.hashMap.get(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        locationNames.add(addNewLocation);
        locationAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, locationNames);
        selectLocation.setAdapter(locationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.spinner:
                break;
            case R.id.button_drop_time:
                dropDateSelected = false;
                datePickerDialog.setTitle("Select date");
                long addOneDay;
                if (switchCompat.isChecked()) {
                    addOneDay = TimeUnit.MILLISECONDS.convert(0, TimeUnit.DAYS);
                } else {
                    addOneDay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
                }
                long tenDays = TimeUnit.MILLISECONDS.convert(10, TimeUnit.DAYS);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + addOneDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + tenDays);
                datePickerDialog.show();
                break;
            case R.id.send_request:
                if (!dropDateSelected) {
                    Toast.makeText(this, "please select drop time & date", Toast.LENGTH_SHORT).show();
                    return;
                }
                CheckOutActivity.getInstance().sendData(dropDateString + " "+ dropTimeString, laundryType);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case LOCATION_OFF:
                if (locationEnabled()) {
                    startActivity(new Intent(getApplicationContext(), PickLDropLaundryActivity.class));
                }
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
                                    startActivityForResult(myIntent, LOCATION_OFF);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (locationNames.get(i).equals(addNewLocation)) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                if (!locationEnabled()) {
                    // notify user
                    String message;
                    String title;
                    if (jsonArray.length() < 1) {
                        message = "Location is not enabled. \nPlease add your address.";
                        title = "Note";
                    } else {
                        message = "Location is not enabled.";
                        title = "Location";
                    }
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle(title);
                    dialog.setMessage(message);
                    dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(myIntent, LOCATION_OFF);
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
                    if (AppGlobals.isUserLoggedIn()) {
                        startActivity(new Intent(getApplicationContext(), PickLDropLaundryActivity.class));
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
        } else {
            try {
                sAddressId = AddressesActivity.hashMap.get(i).getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.i("TAG", "year " + i + "month " + i1 + "date " + i2);
        dropDateString = i2 + "-" + (i1 + 1) + "-" + i;
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        Log.i("TAG", "hour " + i + "minute " + i1);
        if (i < 13 || i > 20 && !switchCompat.isChecked()) {
            Toast.makeText(this, "Delivery time must be between 14:00 - 20:00", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    timePickerDialog.setTitle("Please select Drop Time");
                    timePickerDialog.show();
                }
            }, 1000);
            return;
        } else if (i < hour+2 && switchCompat.isChecked()) {
            Toast.makeText(this, "Delivery time must be after two hours of pickup", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    timePickerDialog.setTitle("Please select Drop Time");
                    timePickerDialog.show();
                }
            }, 1000);
            return;
        }
        Log.i("TAG", "hour" + hour);
        if (i < hour) {
            Toast.makeText(this, "Drop Time must be one day after Pickup please select again", Toast.LENGTH_SHORT)
                    .show();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    timePickerDialog.setTitle("Please select Drop Time");
                    timePickerDialog.show();
                }
            }, 1000);
            return;
        }
        dropTimeString = i + ":" + i1;
        dropDateSelected = true;
        Toast.makeText(this, "Time set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            laundryType = "express";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Express Laundry");
            alertDialogBuilder.setMessage("Express laundry service will allow you to get your laundry done rapidly." +
                    "Minimum drop time you can select is 2 hours. Double cost applied.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            selectTime.setText("Select Drop Time(Minimum 2 hours after pickup)");
            totalPrice.setText(expressPrice + " SAR");
        } else {
            laundryType = "normal";
            selectTime.setText("Select Drop Time(Must be between 14:00 - 20:00)");
            totalPrice.setText((normalPrice) + " SAR");
        }
    }
}
