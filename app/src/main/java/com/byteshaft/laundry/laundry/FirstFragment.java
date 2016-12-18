package com.byteshaft.laundry.laundry;

import android.annotation.SuppressLint;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.wholeData;

/**
 * Created by s9iper1 on 12/14/16.
 */

@SuppressLint("ValidFragment")
public class FirstFragment extends Fragment {

    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static FirstFragment sInstance;
    private View mBaseView;
    private GridLayoutManager gridLayoutManager;
    private String fragmentName;

    public static FirstFragment getInstance() {
        return sInstance;
    }

    public FirstFragment(String fragment) {
        fragmentName = fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TAG", "onCreateView");
        mBaseView = inflater.inflate(R.layout.recyclerable_layout, container, false);
        return mBaseView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sInstance = this;
        Log.i("TAG", "onViewCreated");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridLayoutManager = new GridLayoutManager(getActivity()
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
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        CustomAdapter mAdapter = new CustomAdapter(wholeData.get(fragmentName));
        mRecyclerView.setAdapter(mAdapter);

    }

    //    @Override
//    public void onPause() {
//        super.onPause();
//        if (getFragmentManager().findFragmentByTag
//                (LaundryCategoriesActivity.getInstance().categories.get(
//                        LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()).getCategoryName()) != null)
//            getFragmentManager().findFragmentByTag(LaundryCategoriesActivity.getInstance().categories.get(
//                    LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()).getCategoryName()).setRetainInstance(true);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getFragmentManager().findFragmentByTag(LaundryCategoriesActivity.getInstance().categories.get(
//                LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()).getCategoryName()) != null)
//            getFragmentManager().findFragmentByTag(LaundryCategoriesActivity.getInstance().categories.get(
//                    LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()).getCategoryName()).getRetainInstance();
//    }
//
//    private void getCategoryData() {
//        Log.i("TAG", "counter " + sCounter);
//        if (sCounter < LaundryCategoriesActivity.getInstance().categories.size()) {
//            Log.i("TAG", "condition if");
//            HttpRequest http = new HttpRequest(getActivity().getApplicationContext());
//            http.setOnReadyStateChangeListener(this);
//            http.setOnErrorListener(this);
//            final String url = String.format("%slaundry/categories/%d", AppGlobals.BASE_URL,
//                    LaundryCategoriesActivity.getInstance().categories.get(sCounter).getCategoryId());
//            Log.i("TAG", url + " category :" + LaundryCategoriesActivity.getInstance().categories
//                    .get(sCounter).getCategoryName());
//            sPositionIndex.put(url, sCounter);
//            http.open("GET", url);
//            http.send();
//            sCounter = sCounter + 1;
//        }
////        else {
////            Log.i("TAG", "condition else");
////            if (wholeData.size() >= LaundryCategoriesActivity
////                    .getInstance().mViewPager.getCurrentItem()) {
////                Log.i("TAG", "condition else -> if");
////                CustomAdapter mAdapter = new CustomAdapter(wholeData.get(
////                        LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()));
////                mRecyclerView.setAdapter(mAdapter);
////            }
////        }
//    }
//
//    public void onPageChanged() {
//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("TAG", "tab change " + LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem());
//                CustomAdapter mAdapter = new CustomAdapter(wholeData.get(
//                        LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()));
//                mRecyclerView.setAdapter(mAdapter);
//                mAdapter.notifyDataSetChanged();
//            }
//        }, 2000);
//    }
//
//    @Override
//    public void onReadyStateChange(HttpRequest request, int readyState) {
//        switch (readyState) {
//            case HttpRequest.STATE_DONE:
//                switch (request.getStatus()) {
//                    case HttpURLConnection.HTTP_OK:
//                        int index = sPositionIndex.get(request.getResponseURL());
//                        Log.i("TAG", request.getResponseText());
//                        laundryItems = new ArrayList<>();
//                        try {
//                            JSONArray jsonArray = new JSONArray(request.getResponseText());
//                            if (jsonArray.length() > 0) {
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                    LaundryItem laundryItem = new LaundryItem();
////                                laundryItem.setId(jsonObject.getInt("id"));
//                                    laundryItem.setName(jsonObject.getString("name"));
//                                    laundryItem.setPrice(jsonObject.getString("price"));
//                                    laundryItem.setImageUri(jsonObject.getString("image"));
//                                    laundryItems.add(laundryItem);
//                                }
//                                wholeData.add(index, laundryItems);
//                            } else {
//                                wholeData.add(index, laundryItems);
//                            }
//                            if (index == LaundryCategoriesActivity
//                                    .getInstance().mViewPager.getCurrentItem()) {
//                                Log.i("TAG", "adapter " + wholeData.get(
//                                        LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()).size());
//                                CustomAdapter mAdapter = new CustomAdapter(wholeData.get(
//                                        LaundryCategoriesActivity.getInstance().mViewPager.getCurrentItem()));
//                                mRecyclerView.setAdapter(mAdapter);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//        }
//    }

    private class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<LaundryItem> items;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private CustomView viewHolder;


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
            Log.i("TAG", "name " + laundryItem.getName());
            viewHolder.titleTextView.setText(laundryItem.getName());
            viewHolder.price.setText(String.valueOf(laundryItem.getPrice()));
            Picasso.with(AppGlobals.getContext())
                    .load(laundryItem.getImageUri())
                    .resize(300, 300)
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

    private static class CustomView extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView price;

        public CustomView(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);

        }
    }
}
