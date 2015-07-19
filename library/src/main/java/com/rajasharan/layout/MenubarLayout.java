package com.rajasharan.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajasharan on 7/7/15.
 */
public class MenubarLayout extends ViewGroup {
    private static final String TAG = "MenubarLayout";

    private int mCurrentViewIndex;
    private MenubarView mMenuBarView;
    private MenuSelectorView mMenuSelectorView;
    private int mHeightDivideBy;
    private List<String> mMenuNames;

    public MenubarLayout(Context context) {
        this(context, null);
    }

    public MenubarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenubarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCurrentViewIndex = -1;
        mHeightDivideBy = 20;
        mMenuNames = new ArrayList<>();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams lp = getLayoutParams(view);
            mMenuNames.add(lp.mName);
        }

        for (int i=1; i<100; i++) {
            mMenuNames.add("Item - " + i);
        }


        mMenuBarView = new MenubarView(getContext(), false, "MAIN LOgO HERE");
        mMenuSelectorView = new MenuSelectorView(getContext(), false);
        mMenuSelectorView.setAdapter(new MenubarAdapter(mMenuNames, 5, this));
        mMenuSelectorView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        addView(mMenuBarView, 0);
        addView(mMenuSelectorView, 1);

        //Log.d(TAG, "onFininshInflate");
    }

    private boolean measureCurrentView(int widthSpec, int heightSpec) {
        if (mCurrentViewIndex == -1) {
            return false;
        }

        View view = getChildAt(mCurrentViewIndex);
        LayoutParams lp = getLayoutParams(view);
        if (lp.mName == null) {
            return false;
        }

        view.measure(widthSpec, heightSpec);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthSpec, heightSpec);

        int menuWidthSpec = widthSpec;
        int menuHeightSpec = MeasureSpec.makeMeasureSpec(height/mHeightDivideBy, MeasureSpec.EXACTLY);
        mMenuBarView.measure(menuWidthSpec, menuHeightSpec);
        mMenuSelectorView.measure(menuWidthSpec, menuHeightSpec);

        int viewWidthSpec = widthSpec;
        int viewHeightSpec = MeasureSpec.makeMeasureSpec(height - height/mHeightDivideBy, MeasureSpec.EXACTLY);
        if (!measureCurrentView(viewWidthSpec, viewHeightSpec)) {
            for (int i = 0; i < getChildCount() && i != mCurrentViewIndex; i++) {
                View view = getChildAt(i);
                LayoutParams lp = getLayoutParams(view);
                if (lp.mName != null) {
                    mCurrentViewIndex = i;
                    view.measure(viewWidthSpec, viewHeightSpec);
                    break;
                }
            }
        }

        //Log.d(TAG, String.format("onMeasure: mMenuBarView (%d, %d)", width, height/mHeightDivideBy));
        //Log.d(TAG, String.format("onMeasure: mMenuSelectorView (%d, %d)", width, height/mHeightDivideBy));
        //Log.d(TAG, String.format("onMeasure: mCurrentView (%d, %d)", width, height - height/mHeightDivideBy));
    }

    private boolean layoutCurrentView(int l, int t, int r, int b) {
        if (mCurrentViewIndex == -1) {
            return false;
        }

        View view = getChildAt(mCurrentViewIndex);
        LayoutParams lp = getLayoutParams(view);
        if (lp.mName == null) {
            return false;
        }

        view.layout(l, t, r, b);
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //mMenuBarView.draw(canvas);
        //mMenuSelectorView.draw(canvas);
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offset = b/mHeightDivideBy;
        int offset2 = offset * 2;
        int viewTop = t + offset2;

        mMenuBarView.layout(l, t, r, offset);
        mMenuSelectorView.layout(l, t+offset, r, offset2);

        if (!layoutCurrentView(l, viewTop, r, b)) {
            for (int i = 0; i < getChildCount() && i != mCurrentViewIndex; i++) {
                View view = getChildAt(i);
                LayoutParams lp = getLayoutParams(view);
                if (lp.mName != null) {
                    mCurrentViewIndex = i;
                    view.layout(l, viewTop, r, b);
                    break;
                }
            }
        }

        Log.d(TAG, String.format("onLayout: mMenuBarView:(%d, %d, %d, %d)", l, t, r, offset));
        Log.d(TAG, String.format("onLayout: mMenuSelectorView:(%d, %d, %d, %d)", l, t+offset, r, offset2));
        Log.d(TAG, String.format("onLayout: mCurrentView:(%d, %d, %d, %d)", l, viewTop, r, b));
    }

    /*package*/ void toggleMenuPress() {
        mMenuBarView.mActivate = !mMenuBarView.mActivate;
        mMenuSelectorView.mActivate = !mMenuSelectorView.mActivate;
        //invalidate(mMenuBarView.getLeft(), mMenuBarView.getTop(), mMenuSelectorView.getRight(), mMenuSelectorView.getBottom());
        mMenuBarView.invalidate();
        mMenuSelectorView.invalidate();
    }

    /*package*/ void changeCurrentView(int index) {
        if (mCurrentViewIndex == index) return;
        mCurrentViewIndex = index;
        //invalidate(mMenuSelectorView.getLeft(), mMenuSelectorView.getBottom(), getRight(), getBottom());
        invalidate();
    }

    private LayoutParams getLayoutParams(View view) {
        return (LayoutParams) view.getLayoutParams();
    }

    private static class LayoutParams extends ViewGroup.LayoutParams {
        String mName;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            init(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        private void init(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenubarLayout);
            mName = a.getString(R.styleable.MenubarLayout_menu_name);
            a.recycle();

            if (mName == null || mName.trim().length() == 0) {
                throw new UnsupportedOperationException("'menu_name' param missing from XML for one of your included layouts");
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
