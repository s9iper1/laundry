package com.byteshaft.laundry;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.WebServiceHelpers;
import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

import static com.byteshaft.laundry.CheckOutActivity.pickOption;

public class PickLDropLaundryActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CompoundButton.OnCheckedChangeListener, HttpRequest.OnErrorListener, HttpRequest.OnReadyStateChangeListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private Marker currLocationMarker;
    private Double longitude;
    private Double latitude;
    private int counter = 0;
    private String city = "";
    private String address = "";
    private String zipCode = "";
    private String houseNumber = "";
    private EditText addressTitle;
    private EditText cityEditText;
    private EditText streetEditText;
    private EditText zipCodeEditText;
    private EditText houseNumberEditText;
    private Animation slideDown;
    private Animation slideUp;
    private RelativeLayout relativeLayout;
    private TextView switchTextView;
    private Switch deliverySwitch;
    private boolean switchOn = false;
    private EditText deliveryHouseNumber;
    private EditText deliveryCityEditText;
    private EditText deliveryStreetEditText;
    private EditText deliveryZipCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_laundry);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorPrimaryDark)));
        addressTitle = (EditText) findViewById(R.id.address_title);
        cityEditText = (EditText) findViewById(R.id.city_address);
        houseNumberEditText = (EditText) findViewById(R.id.house_number);
        streetEditText = (EditText) findViewById(R.id.street);
        zipCodeEditText = (EditText) findViewById(R.id.zip_code);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_delivery);
        deliverySwitch = (Switch) findViewById(R.id.same_delivery_address);
        deliveryCityEditText = (EditText) findViewById(R.id.delivery_city_address);
        deliveryStreetEditText = (EditText) findViewById(R.id.delivery_street);
        deliveryZipCodeEditText = (EditText) findViewById(R.id.delivery_zip_code);
        switchTextView = (TextView) findViewById(R.id.switch_text);
        deliveryHouseNumber = (EditText) findViewById(R.id.delivery_house_number);
        addressTitle.setTypeface(AppGlobals.typefaceNormal);
        cityEditText.setTypeface(AppGlobals.typefaceNormal);
        houseNumberEditText.setTypeface(AppGlobals.typefaceNormal);
        streetEditText.setTypeface(AppGlobals.typefaceNormal);
        zipCodeEditText.setTypeface(AppGlobals.typefaceNormal);
        deliverySwitch.setTypeface(AppGlobals.typefaceNormal);
        deliveryCityEditText.setTypeface(AppGlobals.typefaceNormal);
        deliveryStreetEditText.setTypeface(AppGlobals.typefaceNormal);
        deliveryZipCodeEditText.setTypeface(AppGlobals.typefaceNormal);
        switchTextView.setTypeface(AppGlobals.typefaceNormal);
        deliveryHouseNumber.setTypeface(AppGlobals.typefaceNormal);
        deliverySwitch.setOnCheckedChangeListener(this);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pickOption = false;
        finish();
    }

    private boolean validateFields() {
        if (deliverySwitch.isEnabled()) {
            if (addressTitle.getText().toString().trim().isEmpty() ||
                    cityEditText.getText().toString().trim().isEmpty() ||
                    streetEditText.getText().toString().trim().isEmpty() ||
                    zipCodeEditText.getText().toString().trim().isEmpty() ||
                    houseNumberEditText.getText().toString().trim().isEmpty()
                    ) {
                Log.i("TAG", "first");
                Toast.makeText(this, "All fields are Required", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (addressTitle.getText().toString().trim().isEmpty() ||
                    cityEditText.getText().toString().trim().isEmpty() ||
                    streetEditText.getText().toString().trim().isEmpty() ||
                    zipCodeEditText.getText().toString().trim().isEmpty() ||
                    deliveryCityEditText.getText().toString().trim().isEmpty() ||
                    deliveryStreetEditText.getText().toString().trim().isEmpty() ||
                    deliveryZipCodeEditText.getText().toString().trim().isEmpty() ||
                    houseNumberEditText.getText().toString().trim().isEmpty() ||
                    deliveryHouseNumber.getText().toString().trim().isEmpty()) {
                Log.i("TAG", "second");
                Toast.makeText(this, "All fields are Required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_address_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_address) {
            if (validateFields()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("location", latitude + ", " + longitude);
                    jsonObject.put("name", addressTitle.getText().toString());
                    jsonObject.put("pickup_city", cityEditText.getText().toString());
                    jsonObject.put("pickup_house_number", houseNumberEditText.getText().toString());
                    jsonObject.put("pickup_street", streetEditText.getText().toString());
                    jsonObject.put("pickup_zip", zipCodeEditText.getText().toString());
                    Log.i("TAG", "switch state "+ switchOn);
                    if (!switchOn) {
                        Log.i("TAG", "Running drop part");
                        jsonObject.put("drop_city", deliveryCityEditText.getText().toString());
                        jsonObject.put("drop_house_number", deliveryHouseNumber.getText().toString());
                        jsonObject.put("drop_street", deliveryStreetEditText.getText().toString());
                        jsonObject.put("drop_zip", deliveryZipCodeEditText.getText().toString());
                        jsonObject.put("drop_on_pickup_location", "false");
                    }
                    Log.i("TAG", "Data " + jsonObject.toString());
                    addLocation(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addLocation(JSONObject jsonObject) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/addresses", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(jsonObject.toString());
        WebServiceHelpers.showProgressDialog(this, "Processing");
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        WebServiceHelpers.dismissProgressDialog();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                WebServiceHelpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                        finish();
                        Toast.makeText(this, "Address added!", Toast.LENGTH_SHORT).show();
                        break;
                    default:Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();

                }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!b) {
            switchTextView.setText(getResources().getString(R.string.delivery_address_change));
            switchOn = false;
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setAnimation(slideDown);
            deliveryCityEditText.setText(cityEditText.getText().toString());
        } else {
            switchTextView.setText(getResources().getString(R.string.delivery_address_same_as_pick_address));
            switchOn = true;
            relativeLayout.setVisibility(View.GONE);
            relativeLayout.setAnimation(slideUp);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                );

                longitude = latLng.longitude;
                latitude = latLng.latitude;
                System.out.println(latLng + "Position");
                String Position = "" + latLng;
                System.out.println(Position + "current Position");
                System.out.println(longitude + "longitude");
                System.out.println(latitude + "latitude");

                if (pickOption) {
                    Log.i("TAG", "null" + String.valueOf(longitude == null));
                    CheckOutActivity.sPickLocationLongitude = longitude;
                    CheckOutActivity.sPickLocationLatitude = latitude;
                } else {
                    Log.i("TAG", "null" + String.valueOf(longitude == null));
                    CheckOutActivity.sDropLocationLongitude = longitude;
                    CheckOutActivity.sDropLocationLatitude = latitude;
                }
                getCompleteAddressString(latitude, longitude);
            }
        });

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }


    private String getCompleteAddressString(double lattitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, 1);
            if (addresses != null) {
                address = addresses.get(0).getAddressLine(0);
                if (!address.trim().isEmpty() && address != null) {
                    cityEditText.setText(address);
                }
                city = addresses.get(0).getLocality();
                if (!city.trim().isEmpty() && city != null) {
                    cityEditText.setText(address + " " + city);
                }
                zipCode = addresses.get(0).getPostalCode();
                if (!zipCode.trim().isEmpty() && zipCode != null) {
                    zipCodeEditText.setText(zipCode);
                }
                Log.i("TAG", "address " + address + city + zipCode);
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            currLocationMarker = mMap.addMarker(markerOptions);
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currLocationMarker = mMap.addMarker(markerOptions);

        if (counter < 1) {
            longitude = latLng.longitude;
            latitude = latLng.latitude;
            getCompleteAddressString(latitude, longitude);
        }
        counter++;
        //zoom to current position:
        if (pickOption) {
            CheckOutActivity.sPickLocationLongitude = latLng.longitude;
            CheckOutActivity.sPickLocationLatitude = latLng.latitude;
        } else {
            CheckOutActivity.sDropLocationLongitude = latLng.longitude;
            CheckOutActivity.sDropLocationLatitude = latLng.latitude;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
}
