package com.byteshaft.laundry.laundry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.laundry.R;

import java.util.ArrayList;

/**
 * Created by s9iper1 on 12/14/16.
 */

public class RecycleAbleFragment extends Fragment {

    public  RecyclerView mRecyclerView;
    private static CustomView viewHolder;
    private CustomAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerable_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final GridLayoutManager _sGridLayoutManager = new GridLayoutManager(getActivity()
                .getApplicationContext(),2);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.specific_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout)  view.findViewById(R.id.category_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorAccent, R.color.primary_light);
        mRecyclerView.setLayoutManager(_sGridLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<Integer> items;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;


        public CustomAdapter(ArrayList<Integer> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<Integer> categories) {
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
            viewHolder.idTextView.setText(String.valueOf(items.get(position)));

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

        public interface OnItemClickListener {
            void onItem(Integer item, TextView textView);
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView titleTextView;
        public ImageView imageView;
        public TextView description;
        public TextView price;

        public CustomView(View itemView) {
            super(itemView);

        }
    }
}
