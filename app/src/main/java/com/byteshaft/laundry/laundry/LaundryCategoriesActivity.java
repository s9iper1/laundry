package com.byteshaft.laundry.laundry;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.byteshaft.laundry.CheckOutActivity;
import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class LaundryCategoriesActivity extends AppCompatActivity implements
        HttpRequest.OnErrorListener, HttpRequest.OnReadyStateChangeListener {

    private Toolbar mToolbar;
    public TabLayout tabLayout;
    public ViewPager mViewPager;
    public Adapter mAdapter;
    private HttpRequest request;
    public ArrayList<Category> categories;
    private static LaundryCategoriesActivity sInstance;
    public static int sCounter = 0;
    public static ArrayList<LaundryItem> laundryItems;
    public static HashMap<String, ArrayList<LaundryItem>> wholeData;
    public static HashMap<String, Integer> sPositionIndex;

    public static LaundryCategoriesActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCategories();
        setContentView(R.layout.activity_selection);
        sInstance = this;
        categories = new ArrayList<>();
        wholeData = new HashMap<>();
        sPositionIndex = new HashMap<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout.setupWithViewPager(mViewPager);
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
        if (id == R.id.checkout) {
            startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sCounter = 0;
    }

    private class Adapter extends FragmentPagerAdapter {

        FragmentManager fragmentManager;
        private ArrayList<Fragment> fragments;

        Adapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            getPageTitle(position);
            return new FirstFragment(categories.get(position).getCategoryName());
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.i("PAGE TITLE", "title " + categories.get(position).getCategoryName());
            return categories.get(position).getCategoryName();
        }
    }

    private void getCategories() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%slaundry/categories", AppGlobals.BASE_URL));
        request.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", request.getResponseText());
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setCategoryId(jsonObject.getInt("id"));
                                category.setCategoryName(jsonObject.getString("name"));
                                categories.add(category);
                            }
                            getCategoryData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
        }
    }

    private void getCategoryData() {
        if (sCounter < LaundryCategoriesActivity.getInstance().categories.size()) {
            HttpRequest http = new HttpRequest(getApplicationContext());
            http.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
                @Override
                public void onReadyStateChange(HttpRequest request, int readyState) {
                    switch (readyState) {
                        case HttpRequest.STATE_DONE:
                            switch (request.getStatus()) {
                                case HttpURLConnection.HTTP_OK:
                                    int index = sPositionIndex.get(request.getResponseURL());
                                    Log.i("TAG", request.getResponseText());
                                    laundryItems = new ArrayList<>();
                                    try {
                                        JSONArray jsonArray = new JSONArray(request.getResponseText());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            LaundryItem laundryItem = new LaundryItem();
                                            laundryItem.setName(jsonObject.getString("name"));
                                            laundryItem.setPrice(jsonObject.getString("price"));
                                            laundryItem.setImageUri(jsonObject.getString("image"));
                                            laundryItems.add(laundryItem);
                                        }
                                        wholeData.put(categories.get(sCounter)
                                                .getCategoryName(), laundryItems);
                                        sCounter = sCounter+1;
                                        if (sCounter == categories.size()) {
                                            mAdapter = new Adapter(getSupportFragmentManager());
                                            mViewPager.setAdapter(mAdapter);
                                            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                                @Override
                                                public void onTabSelected(TabLayout.Tab tab) {
                                                    mViewPager.setCurrentItem(tab.getPosition());
                                                }

                                                @Override
                                                public void onTabUnselected(TabLayout.Tab tab) {

                                                }

                                                @Override
                                                public void onTabReselected(TabLayout.Tab tab) {

                                                }
                                            });
                                        }
                                        getCategoryData();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                    }
                }
            });
            http.setOnErrorListener(this);
            final String url = String.format("%slaundry/categories/%d", AppGlobals.BASE_URL,
                    LaundryCategoriesActivity.getInstance().categories.get(sCounter).getCategoryId());
            sPositionIndex.put(url, sCounter);
            http.open("GET", url);
            http.send();
        }
    }


    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
