package com.byteshaft.laundry;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {

//    private Button addButton;
//    private Button minusButton;
//    private TextView weightTextView;
    private int weight = 2;
    private Button pickLocation;
    private Button dropLocation;
    private static final int PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int DROP_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static double sPickLocationLatitude = 0.0;
    public static double sPickLocationLongitude = 0.0;
    public static double sDropLocationLatitude = 0.0;
    public static double sDropLocationLongitude = 0.0;
    public static boolean pickOption = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        pickLocation = (Button) findViewById(R.id.pick_up_location);
        dropLocation = (Button) findViewById(R.id.drop_location);
        pickLocation.setOnClickListener(this);
        dropLocation.setOnClickListener(this);
//        addButton = (Button) findViewById(R.id.add);
//        minusButton = (Button) findViewById(R.id.minus);
//        weightTextView = (TextView) findViewById(R.id.weight);
//        addButton.setOnClickListener(this);
//        minusButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.add:
//                if (weight < 20) {
//                    weight++;
//                    weightTextView.setText(String.valueOf(weight) + "Kg");
//                } else {
//                    Toast.makeText(this, "Over weight", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.minus:
//                if (weight > 2) {
//                    weight--;
//                    weightTextView.setText(String.valueOf(weight) + "Kg");
//                } else {
//                    Toast.makeText(this, "Minimum weight is 2Kg", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.pick_up_location:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PICK_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    if(!locationEnabled()) {
                        // notify user
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage("Location is not enabled");
                        dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub
                                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
                }
                break;
            case R.id.drop_location:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            DROP_LAUNDRY_MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    if(!locationEnabled()) {
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
                        if(!locationEnabled()) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setMessage("Location is not enabled");
                            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
                        if(!locationEnabled()) {
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
        LocationManager lm = (LocationManager)getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled || network_enabled;

    }
}
