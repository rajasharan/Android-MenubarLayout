package com.rajasharan.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rajasharan on 7/8/15.
 */
/*package*/ class MenubarView extends View {
    private static final String TAG = "MenubarView";

    private TextPaint mTextpaint;
    private Paint mPaint;
    private Paint mBackgroundPaint;

    /*package*/ String mTitle;
    /*package*/ boolean mActivate;

    public MenubarView(Context context, boolean activate, String title) {
        super(context);
        init(context, activate, title);
    }

    private void init(Context context, boolean activate, String title) {
        mTextpaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextpaint.setARGB(255, 255, 255, 255);
        mTextpaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                context.getResources().getDisplayMetrics()));
        mTextpaint.setTextAlign(Paint.Align.CENTER);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setARGB(255, 255, 255, 255);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(Color.argb(160, 0, 0, 0));

        mActivate = activate;
        mTitle = title;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int l = getLeft();
        int t = getTop();
        int r = getRight();
        int b = getBottom();

        canvas.drawRect(l, t, r, b, mBackgroundPaint);
        if(mActivate) {
            drawCrossIcon(canvas);
        } else {
            drawBurgerIcon(canvas);
        }
        drawTitle(canvas);

        //Log.d(TAG, String.format("MenubarView: (%d, %d) - (%d, %d)", l, t, r, b));
    }

    private void drawTitle(Canvas canvas) {
        int l = getLeft();
        int w = getWidth();
        int t = getTop();
        int h = getHeight();
        float startX = l + w/2;
        float startY = t + h/2 + h/4;
        canvas.drawText(mTitle, startX, startY, mTextpaint);
    }

    private void drawBurgerIcon(Canvas canvas) {
        int h = getHeight();
        float half = h/2;
        float start = h/3;
        float stop = half;
        canvas.drawLine(start, start, h, start, mPaint);
        canvas.drawLine(start, stop, h, stop, mPaint);
    }

    private void drawCrossIcon(Canvas canvas) {
        int h = getHeight();
        int start = h/4;
        h = h - start;
        int stop = h;
        canvas.drawLine(start, start, stop, stop, mPaint);
        canvas.drawLine(start, stop, stop, start, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "mMenubarView onTouchEvent");
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            MenubarLayout parent = (MenubarLayout) getParent();
            parent.toggleMenubar();
        }
        return true;
    }
}
