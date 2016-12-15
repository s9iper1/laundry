package com.byteshaft.laundry.laundry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.laundryItems;
import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.sCounter;
import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.wholeData;

/**
 * Created by s9iper1 on 12/14/16.
 */

public class RecycleAbleFragment extends Fragment implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    public RecyclerView mRecyclerView;
    private static CustomView viewHolder;
    private CustomAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HttpRequest httpRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getCategoryData();
        return inflater.inflate(R.layout.recyclerable_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity()
                .getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.specific_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.category_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorAccent, R.color.primary_light);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void getCategoryData() {
        Log.i("TAG", "counter " + sCounter);
        if (sCounter < LaundryCategoriesActivity.getInstance().categories.size()) {
            HttpRequest httpRequest = new HttpRequest(getActivity().getApplicationContext());
            if (sCounter > 0) {
                httpRequest.setOnReadyStateChangeListener(this);
            } else {
                httpRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
                    @Override
                    public void onReadyStateChange(HttpRequest request, int readyState) {
                        Log.i("TAG", "Response :" + sCounter + " "+ request.getResponseText());
                        laundryItems = new ArrayList<>();
                        mAdapter = new CustomAdapter(laundryItems);
                        mRecyclerView.setAdapter(mAdapter);
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0 ; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                LaundryItem laundryItem = new LaundryItem();
//                                laundryItem.setId(jsonObject.getInt("id"));
                                laundryItem.setName(jsonObject.getString("name"));
                                laundryItem.setPrice(jsonObject.getString("price"));
                                laundryItem.setImageUri(jsonObject.getString("image"));
                                laundryItems.add(laundryItem);
                                mAdapter.notifyDataSetChanged();
                            }
                            wholeData.add(laundryItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            httpRequest.setOnErrorListener(this);
            final String url = String.format("%slaundry/categories/%d", AppGlobals.BASE_URL,
                    LaundryCategoriesActivity.getInstance().categories.get(sCounter).getCategoryId());
            Log.i("TAG", url + " category :" + LaundryCategoriesActivity.getInstance().categories
                    .get(sCounter).getCategoryName());
            if (sCounter > 0) {
               new  android.os.Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
//                       httpRequest.open("GET", url);
//                       httpRequest.send();
                   }
               }, 5000);
            } else {
                httpRequest.open("GET", url);
                httpRequest.send();
            }
            sCounter = sCounter + 1;
        } else {
            mAdapter = new CustomAdapter(wholeData.get(
                    LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", request.getResponseText());
                        laundryItems = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0 ; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                LaundryItem laundryItem = new LaundryItem();
//                                laundryItem.setId(jsonObject.getInt("id"));
                                laundryItem.setName(jsonObject.getString("name"));
                                laundryItem.setPrice(jsonObject.getString("price"));
                                laundryItem.setImageUri(jsonObject.getString("image"));
                                laundryItems.add(laundryItem);
                            }
                            wholeData.add(laundryItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<LaundryItem> items;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;


        public CustomAdapter(ArrayList<LaundryItem> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<LaundryItem> categories) {
            this.items = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.delegate_category, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            LaundryItem laundryItem = items.get(position);
            viewHolder.titleTextView.setText(laundryItem.getName());
            Picasso.with(getActivity())
                    .load(laundryItem.getImageUri())
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (mRecyclerView.findViewHolderForAdapterPosition(position) != null) {

                            }
                        }

                        @Override
                        public void onError() {
                            if (mRecyclerView.findViewHolderForAdapterPosition(position) != null) {

                            }

                        }
                    });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                mListener.onItem(items.get(rv.getChildPosition(childView)), (TextView)
//                        rv.findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
//                                itemView.findViewById(R.id.specific_category_title));
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
    }

    public interface OnItemClickListener {
        void onItem(Integer item, TextView textView);
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView price;

        public CustomView(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.item_name);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            price = (TextView) itemView.findViewById(R.id.item_price);

        }
    }
}
