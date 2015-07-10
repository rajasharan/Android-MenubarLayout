package com.rajasharan.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * Created by rajasharan on 7/8/15.
 */
/*package*/ class MenuSelectorView extends View {
    private static final String TAG = "MenuSelectorView";

    private List<String> mMenuNames;
    private TextPaint mTextpaint;
    private Paint mBackgroundPaint;
    private float mGap;
    /*package*/ boolean mActivate;

    public MenuSelectorView(Context context, boolean activate, List<String> menus) {
        super(context);
        init(context, activate, menus);
    }

    private void init(Context context, boolean activate, List<String> menus) {
        mMenuNames = menus;
        mGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                context.getResources().getDisplayMetrics());
        mActivate = activate;

        mTextpaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextpaint.setARGB(255, 255, 255, 255);
        mTextpaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                context.getResources().getDisplayMetrics()));
        mTextpaint.setTextAlign(Paint.Align.LEFT);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(Color.argb(128, 0, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int l = getLeft();
        int t = getTop();
        int r = getRight();
        int b = getBottom();
        int w = getWidth();
        int h = getHeight();

        float nextX = l;
        float nextY = t + h/2 + h/3;

        if (mActivate) {
            canvas.drawRect(l, t, r, b, mBackgroundPaint);
            for (String text: mMenuNames) {
                if (nextX > r) {
                    break;
                }
                canvas.drawText(text, nextX, nextY, mTextpaint);
                nextX = nextX + mTextpaint.measureText(text) + mGap;
                Log.d(TAG, String.format("menu (%s): (%d, %d) - (%d, %d) | [%s]", text, l,t,r,b, nextX));
            }
        }
        //Log.d(TAG, String.format("menu(-): (%d, %d) - (%d, %d) | [%s]", l,t,r,b, nextX));
    }
}
