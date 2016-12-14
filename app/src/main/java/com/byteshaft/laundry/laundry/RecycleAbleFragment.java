package com.byteshaft.laundry.laundry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.laundry.R;

/**
 * Created by s9iper1 on 12/14/16.
 */

public class RecycleAbleFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_drawer, container, false);
    }
}
