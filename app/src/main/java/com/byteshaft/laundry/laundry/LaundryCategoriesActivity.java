package com.byteshaft.laundry.laundry;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
    public static ArrayList<ArrayList<LaundryItem>> wholeData;
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
        wholeData = new ArrayList<>();
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
        if (id == R.id.action_settings) {
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

    private class Adapter extends FragmentStatePagerAdapter {

        FragmentManager fragmentManager;
        Fragment fragment = null;

        Adapter(FragmentManager fm ) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            getPageTitle(num);
            for (int i = 0; i < categories.size() ; i++) {
                if (i == num) {
                    fragment = new RecycleAbleFragment();
                    break;
                }
            }
            return fragment;
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
                            for (int i = 0 ; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setCategoryId(jsonObject.getInt("id"));
                                category.setCategoryName(jsonObject.getString("name"));
                                categories.add(category);
                            }
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
