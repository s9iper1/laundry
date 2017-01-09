package com.byteshaft.laundry.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by s9iper1 on 1/9/17.
 */

public class HeadingTextView extends TextView {

    public HeadingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setType(context);
    }

    public HeadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public HeadingTextView(Context context) {
        super(context);
        setType(context);
    }

    private void setType(Context context){
        this.setTypeface(AppGlobals.typefaceBold);
    }
}
