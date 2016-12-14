package com.byteshaft.laundry.laundry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.laundry.utils.WebServiceHelpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class LaundryCategoriesActivity extends AppCompatActivity implements MaterialTabListener,
        HttpRequest.OnErrorListener, HttpRequest.OnReadyStateChangeListener {

    private Toolbar mToolbar;
    private MaterialTabHost mTabHost;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private HttpRequest request;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCategories();
        setContentView(R.layout.activity_selection);
        categories = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);

            }
        });
    }

    private void addTab(String title) {
        mTabHost.addTab(mTabHost.newTab().setText(title).setTabListener(LaundryCategoriesActivity.this));
        mTabHost.notifyDataSetChanged();
        mAdapter.setCount(mAdapter.getCount() + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dynamic_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (R.id.add_tabs == id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Add Tab");
            alert.setMessage("Your Tab Name");

// Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mTabHost.addTab(mTabHost.newTab().setText(input.getText().toString()).setTabListener(LaundryCategoriesActivity.this));
                    mTabHost.notifyDataSetChanged();
                    mAdapter.setCount(mAdapter.getCount() + 1);

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int count = 0;
        FragmentManager fragmentManager;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            RecycleAbleFragment recycleableFragment = new RecycleAbleFragment();
            return recycleableFragment;

        }

        @Override
        public int getCount() {
            return count;
        }

        void setCount(int newCount) {
            count = newCount;
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabs)[position];
        }
    }

    private void getCategories() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%slaundry/categories", AppGlobals.BASE_URL));
        request.send();
        WebServiceHelpers.showProgressDialog(this, "Logging In");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                WebServiceHelpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", request.getResponseText());
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0 ; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setCategoryId(jsonObject.getInt("id"));
                                category.setCategoryName(jsonObject.getString("name"));
                                categories.add(category);
                                addTab(category.getCategoryName());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
        }
    }


    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
