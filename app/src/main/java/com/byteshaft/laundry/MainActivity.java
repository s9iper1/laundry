package com.byteshaft.laundry;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.laundry.account.LoginActivity;
import com.byteshaft.laundry.account.ResetPassword;
import com.byteshaft.laundry.fragments.UpdateProfile;
import com.byteshaft.laundry.laundry.LaundryCategoriesActivity;
import com.byteshaft.laundry.utils.AppGlobals;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity sInstance;
    private View header;

    private TextView mName;
    private TextView mEmail;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        if (!AppGlobals.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.activity_main);
        AppGlobals.sActivity = MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hello there");
                startActivity(new Intent(MainActivity.this, LaundryCategoriesActivity.class));
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mName = (TextView) header.findViewById(R.id.nav_user_name);
        mEmail = (TextView) header.findViewById(R.id.nav_user_email);
        if (!AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME).equals("")) {
            String simpleName = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME);
            String firstUpperCaseName = simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
            mName.setText(firstUpperCaseName);
        } else {
            mName.setText("username");
        }
        if (!AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL).equals("")) {
            mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        } else {
            mEmail.setText("abc@xyz.com");
        }

//        CircleImageView circularImageView = (CircleImageView) header.findViewById(R.id.imageView);
//        if (AppGlobals.isUserLoggedIn()) {
//            final Resources res = getResources();
//            int[] array = getResources().getIntArray(R.array.letter_tile_colors);
//            final BitmapWithCharacter tileProvider = new BitmapWithCharacter();
//            final Bitmap letterTile = tileProvider.getLetterTile(AppGlobals.
//                            getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME),
//                    String.valueOf(array[new Random().nextInt(array.length)]), 100, 100);
//            circularImageView.setImageBitmap(letterTile);
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.login:
                startActivity(new Intent(getApplicationContext(), AddressesActivity.class));
                break;
            case R.id.nav_update_profile:
                loadFragment(new UpdateProfile());
                break;
            case R.id.nav_reset_password:
                startActivity(new Intent(getApplicationContext(), ResetPassword.class));
                break;
            case R.id.nav_logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Do you really want to logout?").setCancelable(false).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedpreferences = AppGlobals.getPreferenceManager();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();
                                AppGlobals.logout = true;
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }
//        int id = item.getItemId();
//
//        if (id == R.id.login) {
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            // Handle the camera action
//        } else if (id == R.id.nav_update_profile) {
//            loadFragment(new UpdateProfile());
//
//        } else if (id == R.id.nav_reset_password) {
//            loadFragment(new ResetPassword());
//
//        } else if (id == R.id.nav_logout) {
//            loadFragment(new LogoutFragment());
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void loadFragment(Fragment fragment) {
        FragmentTransaction tx = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }
}
