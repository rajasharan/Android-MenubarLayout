package com.rajasharan.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

/**
 * Created by rajasharan on 8/6/15.
 */
/*package*/ class CenterLinearLayoutManager extends LinearLayoutManager {
    private int mMenuHeight;

    public CenterLinearLayoutManager(Context context) {
        this(context, HORIZONTAL, false);
    }

    public CenterLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mMenuHeight = -1;
    }

    @Override
    public void layoutDecorated(View child, int left, int top, int right, int bottom) {
        if (mMenuHeight != -1) {
            int delta = (mMenuHeight - (bottom - top) ) / 2;
            top = top + delta;
            bottom = bottom + delta;
        }
        super.layoutDecorated(child, left, top, right, bottom);
    }

    /*package*/ void setMenubarHeight(int height) {
        mMenuHeight = height;
    }
}
