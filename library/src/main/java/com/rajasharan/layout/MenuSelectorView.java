package com.rajasharan.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import java.util.List;

/**
 * Created by rajasharan on 7/8/15.
 */
/*package*/ class MenuSelectorView extends RecyclerView {
    private static final String TAG = "MenuSelectorView";

    /*package*/ boolean mActivate;

    public MenuSelectorView(Context context, boolean activate) {
        super(context);
        mActivate = activate;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mActivate) {
            super.dispatchDraw(canvas);
            canvas.drawARGB(128, 0, 0, 0);
        }
    }
}
