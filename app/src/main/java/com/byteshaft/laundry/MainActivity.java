package com.byteshaft.laundry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.laundry.account.CodeConfirmationActivity;
import com.byteshaft.laundry.account.LoginActivity;
import com.byteshaft.laundry.account.ResetPassword;
import com.byteshaft.laundry.account.UpdateProfile;
import com.byteshaft.laundry.laundry.LaundryCategoriesActivity;
import com.byteshaft.laundry.utils.AppGlobals;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity sInstance;
    private View header;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private TextView mName;
    private TextView mEmail;
    NavigationView navigationView;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        AppGlobals.sActivity = MainActivity.this;
        Log.i("TAG", "" + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
//        mAdapter = new CustomAdapter(arrayList);
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnItemTouchListener(new CustomAdapter(arrayList , AppGlobals.getContext()
//                , new CustomAdapter.OnItemClickListener() {
//            @Override
//            public void onItem(String item) {
//                Intent intent = new Intent(getActivity().getApplicationContext(),
//                        SelectedCategoryList.class);
//                intent.putExtra(AppGlobals.CATEGORY_INTENT_KEY, item);
//                startActivity(intent);
//            }
//        }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        MenuItem login,logout, active;
        Menu menu = navigationView.getMenu();
        if (!AppGlobals.isUserLoggedIn()) {
            login = menu.findItem(R.id.login);
            logout = menu.findItem(R.id.nav_logout);
            login.setVisible(true);
            logout.setVisible(false);
        } else {
            login = menu.findItem(R.id.login);
            active = menu.findItem(R.id.active);
            if (!AppGlobals.isUserActive()) {
                active.setVisible(true);
            } else {
                active.setVisible(false);
            }
            logout = menu.findItem(R.id.nav_logout);
            login.setVisible(false);
            logout.setVisible(true);
        }
        mName = (TextView) header.findViewById(R.id.nav_user_name);
        mEmail = (TextView) header.findViewById(R.id.nav_user_email);
        mName.setTypeface(AppGlobals.typefaceBold);
        mEmail.setTypeface(AppGlobals.typefaceNormal);
        if (!AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FULL_NAME).equals("")
                && AppGlobals.isUserActive()) {
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
        if (!AppGlobals.isUserActive() && !AppGlobals.dialogCancel && AppGlobals.isUserLoggedIn()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("User not active");
            alertDialogBuilder.setMessage("please activate your account")
                    .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), CodeConfirmationActivity.class));
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AppGlobals.dialogCancel = true;
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
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
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.active:
                startActivity(new Intent(getApplicationContext(), CodeConfirmationActivity.class));
                break;
            case R.id.nav_update_profile:
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<String> items;
        private CustomView viewHolder;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public interface OnItemClickListener {
            void onItem(String item);
        }

        public CustomAdapter(ArrayList<String> categories, Context context,
                             OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }
                    });
        }

        public CustomAdapter(ArrayList<String> categories) {
            this.items = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.delegate_laundry_items, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                mListener.onItem(items.get(rv.getChildPosition(childView)));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        /* Member class that extends RecyclerView.ViewHolder allows us to access the elements inside
           xml it takes view in constructor
         */
        public class CustomView extends RecyclerView.ViewHolder {
            public TextView textView;
            public ImageView imageView;

            public CustomView(View itemView) {
                super(itemView);
//                textView = (TextView) itemView.findViewById(R.id.category_title);
//                imageView = (ImageView) itemView.findViewById(R.id.selected_category_image);
            }
        }
    }

}
