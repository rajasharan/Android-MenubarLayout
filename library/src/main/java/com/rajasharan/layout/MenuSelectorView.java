package com.rajasharan.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

/**
 * Created by rajasharan on 7/8/15.
 */
/*package*/ class MenuSelectorView extends RecyclerView {

    public MenuSelectorView(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawARGB(128, 0, 0, 0);
        super.dispatchDraw(canvas);
    }
}
