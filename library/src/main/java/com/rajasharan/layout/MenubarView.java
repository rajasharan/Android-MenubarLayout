package com.rajasharan.layout;

import android.animation.ObjectAnimator;
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
    private ObjectAnimator mAnimator;
    private float mDegrees;

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

        mAnimator = ObjectAnimator.ofFloat(this, "angle", 0f, 45f);
        //mAnimator.setDuration(4000);
    }

    private void setAngle(float deg) {
        mDegrees = deg;
        int l = getLeft();
        int t = getTop();
        int r = l + getHeight() + getHeight()/3;
        int b = t + getHeight() + getHeight()/3;
        invalidate(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int l = getLeft();
        int t = getTop();
        int r = getRight();
        int b = getBottom();

        canvas.drawRect(l, t, r, b, mBackgroundPaint);
        drawAnimatedIcon(canvas);
        drawTitle(canvas);
    }

    private void drawTitle(Canvas canvas) {
        int l = getLeft();
        int w = getWidth();
        int t = getTop();
        int h = getHeight();
        float startX = l + w/2;
        float startY = t + h/2 + h/6;
        canvas.drawText(mTitle, startX, startY, mTextpaint);
    }

    private void drawAnimatedIcon(Canvas canvas) {
        if (mActivate) {
            morphToCross(canvas);
        }
        else {
            drawBurgerIcon(canvas);
        }
    }

    private void morphToCross(Canvas canvas) {
        int h = getHeight();
        float half = h/2;
        float start = h/3;
        float stop = half;

        canvas.save();
        canvas.rotate(mDegrees, start, start);
        canvas.translate(0f, -h/4);
        canvas.drawLine(start, start, h, start, mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-mDegrees, start, stop);
        canvas.translate(0f, h/4);
        canvas.drawLine(start, stop, h, stop, mPaint);
        canvas.restore();
    }

    private void drawBurgerIcon(Canvas canvas) {
        int h = getHeight();
        float half = h/2;
        float start = h/3;
        float stop = half;

        canvas.drawLine(start, start, h, start, mPaint);
        canvas.drawLine(start, stop, h, stop, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "mMenubarView onTouchEvent");
        if (!isTouchOnMenuIcon(event)) {
            return false;
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            MenubarLayout parent = (MenubarLayout) getParent();
            parent.toggleMenubar();
            if (!mAnimator.isRunning() && !mAnimator.isStarted()) {
                mAnimator.start();
            } else {
                mAnimator.end();
                mAnimator.start();
            }
        }
        return true;
    }

    private boolean isTouchOnMenuIcon(MotionEvent event) {
        int l = getLeft();
        int t = getTop();
        int r = l + getHeight() + getHeight()/3;
        int b = t + getHeight() + getHeight()/3;
        float x = event.getX();
        float y = event.getY();
        if (x < l || x > r || y < t || y > b) {
            return false;
        }
        return true;
    }
}
