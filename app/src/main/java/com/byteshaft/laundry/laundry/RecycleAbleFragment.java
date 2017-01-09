package com.byteshaft.laundry.laundry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.laundry.R;
import com.byteshaft.laundry.utils.AppGlobals;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.order;
import static com.byteshaft.laundry.laundry.LaundryCategoriesActivity.wholeData;

/**
 * Created by s9iper1 on 12/14/16.
 */

@SuppressLint("ValidFragment")
public class RecycleAbleFragment extends Fragment {

    public RecyclerView mRecyclerView;
    private static RecycleAbleFragment sInstance;
    private View mBaseView;
    private GridLayoutManager gridLayoutManager;
    private String fragmentName;

    public static RecycleAbleFragment getInstance() {
        return sInstance;
    }

    public RecycleAbleFragment(String fragment) {
        fragmentName = fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.recyclerable_layout, container, false);
        return mBaseView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sInstance = this;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridLayoutManager = new GridLayoutManager(getActivity()
                .getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.specific_recycler);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        CustomAdapter mAdapter = new CustomAdapter(wholeData.get(fragmentName));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new CustomAdapter(wholeData.get(fragmentName),
                getActivity().getApplicationContext(),
                new OnItemClickListener() {
                    @Override
                    public void onItem(Integer item) {

                    }
                }));
    }

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
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            final LaundryItem laundryItem = items.get(position);
            String titleLowerCase = laundryItem.getName();
            String firstUpper = titleLowerCase.substring(0, 1).toUpperCase() + titleLowerCase.substring(1);
            viewHolder.titleTextView.setText(firstUpper);
            viewHolder.price.setText(String.valueOf(laundryItem.getPrice()+" SAR"));
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
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (order.containsKey(laundryItem.getId())) {
                        RelativeLayout relativeLayout = (RelativeLayout) mRecyclerView.findViewHolderForAdapterPosition(position).
                                itemView.findViewById(R.id.layout);
                        relativeLayout.setBackgroundColor(getResources()
                                .getColor(R.color.card_selected_color));
                    } else {
                        RelativeLayout relativeLayout = (RelativeLayout) mRecyclerView.findViewHolderForAdapterPosition(position).
                                itemView.findViewById(R.id.layout);
                        relativeLayout.setBackgroundColor(getResources()
                                .getColor(android.R.color.white));
                    }
                }
            }, 100);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e) &&
                    (!Spinner.class.isInstance(childView))) {
                mListener.onItem(items.get(rv.getChildPosition(childView)).getId());
                OrderItem orderItem = new OrderItem();
                orderItem.setId(items.get(rv.getChildPosition(childView)).getId());
                orderItem.setName(items.get(rv.getChildPosition(childView)).getName());
                Spinner spinner = (Spinner) mRecyclerView
                        .findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
                        itemView.findViewById(R.id.quantity_spinner);
                orderItem.setQuantity(String.valueOf(spinner.getSelectedItem()));
                orderItem.setPrice(items.get(rv.getChildPosition(childView)).getPrice());
                orderItem.setImageUrl(items.get(rv.getChildPosition(childView)).getImageUri());
                setBackgroundColor(rv, childView, orderItem);
                return true;
            }
            return false;
        }

        private void setBackgroundColor(RecyclerView rv, View childView, OrderItem orderItem) {
            RelativeLayout cardView;
            ColorDrawable background;
            if (order.containsKey(items.get(rv.getChildPosition(childView)).getId())) {
                cardView = (RelativeLayout) mRecyclerView
                        .findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
                                itemView.findViewById(R.id.layout);
                cardView.setBackgroundColor(getResources()
                        .getColor(android.R.color.white));
                order.remove(items.get(rv.getChildPosition(childView)).getId());
                background = (ColorDrawable) cardView.getBackground();
            } else {
                Spinner spinner = (Spinner) mRecyclerView
                        .findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
                                itemView.findViewById(R.id.quantity_spinner);
                cardView = (RelativeLayout) mRecyclerView
                        .findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
                                itemView.findViewById(R.id.layout);
                cardView.setBackgroundColor(getResources()
                        .getColor(R.color.card_selected_color));
                order.put(items.get(rv.getChildPosition(childView)).getId(),
                        orderItem);
                background = (ColorDrawable) cardView.getBackground();
            }
//            if (order.containsKey(items.get(rv.getChildPosition(childView)).getId()) &&
//                    background.getColor() != getResources().getColor(R.color.card_selected_color)) {
//                setBackgroundColor(rv, childView);
//            } else {
//                setBackgroundColor(rv, childView);
//            }
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface OnItemClickListener {
        void onItem(Integer item);
    }

    private static class CustomView extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView price;
        public RelativeLayout relativeLayout;
        public CardView cardView;
        public Spinner spinner;

        public CustomView(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            spinner = (Spinner) itemView.findViewById(R.id.quantity_spinner);
            titleTextView.setTypeface(AppGlobals.typefaceNormal);
            price.setTypeface(AppGlobals.typefaceNormal);
            titleTextView.setTypeface(AppGlobals.typefaceNormal);
        }
    }
}
