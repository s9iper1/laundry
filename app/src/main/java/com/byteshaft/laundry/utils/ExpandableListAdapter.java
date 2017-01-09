package com.byteshaft.laundry.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byteshaft.laundry.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shahid on 04/01/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    JSONArray mItems;

    public ExpandableListAdapter(Context context, JSONArray items) {
        mContext = context;
        mItems = items;
    }

    static class ViewHolder {
        TextView headerTextView;
        ImageView collapseExpandIndicator;
    }

    static class SubItemsViewHolder {
        TextView pickupCity;
        TextView pickupStreet;
        TextView pickupHouse;
        TextView pickupZipCode;
        TextView pickupLocation;

        // drop textViews
        TextView dropCity;
        TextView dropStreet;
        TextView dropHouse;
        TextView dropZipCode;
        TextView dropLocation;
        RelativeLayout relativeLayout;

    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try {
            return mItems.get(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final SubItemsViewHolder holder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_delegate, null);
            holder = new SubItemsViewHolder();

            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.drop_layout);
            // pickup textViews
            holder.pickupCity = (TextView) convertView.findViewById(R.id.pickup_city);
            holder.pickupStreet = (TextView) convertView.findViewById(R.id.pickup_street_number);
            holder.pickupHouse = (TextView) convertView.findViewById(R.id.pickup_house_name);
            holder.pickupZipCode = (TextView) convertView.findViewById(R.id.pickup_zip_code_);
            holder.pickupLocation = (TextView) convertView.findViewById(R.id.pickup_location);
            holder.pickupCity.setTypeface(AppGlobals.typefaceNormal);
            holder.pickupStreet.setTypeface(AppGlobals.typefaceNormal);
            holder.pickupHouse.setTypeface(AppGlobals.typefaceNormal);
            holder.pickupZipCode.setTypeface(AppGlobals.typefaceNormal);
            holder.pickupLocation.setTypeface(AppGlobals.typefaceNormal);
            // drop textViews
            holder.dropCity = (TextView) convertView.findViewById(R.id.drop_city);
            holder.dropStreet = (TextView) convertView.findViewById(R.id.drop_street_number);
            holder.dropHouse = (TextView) convertView.findViewById(R.id.drop_house_name);
            holder.dropZipCode = (TextView) convertView.findViewById(R.id.drop_zip_code_);
            holder.dropCity.setTypeface(AppGlobals.typefaceNormal);
            holder.dropStreet.setTypeface(AppGlobals.typefaceNormal);
            holder.dropHouse.setTypeface(AppGlobals.typefaceNormal);
            holder.dropZipCode.setTypeface(AppGlobals.typefaceNormal);
            convertView.setTag(holder);
        } else {
            holder = (SubItemsViewHolder) convertView.getTag();
        }
        JSONObject subItems = (JSONObject) getChild(groupPosition, childPosition);
        try {
            holder.pickupCity.setText("City: " + subItems.getString("pickup_city"));
            holder.pickupStreet.setText("Street#: " + subItems.getString("pickup_street"));
            holder.pickupHouse.setText("House#: " + subItems.getString("pickup_house_number"));
            holder.pickupZipCode.setText("Zip Code: " + subItems.getString("pickup_zip"));
//            holder.pickupLocation.setTextColor(Color.BLUE);
            // "https://maps.google.com/maps?q=" + 
            holder.pickupLocation.setText(subItems.getString("location"));

            boolean drop_on_pickup_location = subItems.getBoolean("drop_on_pickup_location");
            if (drop_on_pickup_location) {
                holder.relativeLayout.setVisibility(View.VISIBLE);
                holder.dropCity.setText("City: " + subItems.getString("drop_city"));
                holder.dropStreet.setText("Street#: " + subItems.getString("drop_street"));
                holder.dropHouse.setText("House#: " + subItems.getString("drop_house_number"));
                holder.dropZipCode.setText("Zip Code: " + subItems.getString("drop_zip"));

            } else {
                holder.relativeLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        try {
            return mItems.get(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getGroupCount() {
        return mItems.length();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_header_delegate, null);
            viewHolder = new ViewHolder();
            viewHolder.headerTextView = (TextView) convertView.findViewById(R.id.text_view_location_header);
            viewHolder.collapseExpandIndicator = (ImageView) convertView.findViewById(R.id.image_view_location_expand_collapse);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.headerTextView.setTypeface(AppGlobals.typefaceBold);
        JSONObject header = (JSONObject) getGroup(groupPosition);
        try {
            viewHolder.headerTextView.setAllCaps(true);
            viewHolder.headerTextView.setText(header.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isExpanded) {
            viewHolder.collapseExpandIndicator.setImageResource(R.mipmap.ic_collapse);
        } else {
            viewHolder.collapseExpandIndicator.setImageResource(R.mipmap.ic_expand);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}