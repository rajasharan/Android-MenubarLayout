package com.rajasharan.layout;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajasharan on 7/7/15.
 */
public class MenubarLayout extends ViewGroup {
    private static final String TAG = "MenubarLayout";

    private MenubarView mMenuBarView;
    private MenuSelectorView mMenuSelectorView;
    private CenterLinearLayoutManager mCenterLayoutManager;
    private int mMenuHeight;
    private List<String> mMenuNames;
    private List<View> mUserLayouts;
    private View mCurrentUserLayout;
    private int mCurrentUserIndex;
    private LayoutTransition mMenuSelectorTransition;
    private LayoutTransition mUserLayoutDisappearing;
    private LayoutTransition mUserLayoutAppearing;
    private ObjectAnimator mSlideDown;
    private ObjectAnimator mSlideUp;
    private ObjectAnimator mSlideRightDisappearing;
    private ObjectAnimator mSlideRightAppearing;
    private ObjectAnimator mSlideLeftDisappearing;
    private ObjectAnimator mSlideLeftAppearing;
    private PropertyValuesHolder mTranslationYDown;
    private PropertyValuesHolder mTranslationYUp;
    private PropertyValuesHolder mTranslationXRightDisappearing;
    private PropertyValuesHolder mTranslationXRightAppearing;
    private PropertyValuesHolder mTranslationXLeftDisappearing;
    private PropertyValuesHolder mTranslationXLeftAppearing;
    private PropertyValuesHolder mAlphaDown;
    private PropertyValuesHolder mAlphaUp;
    private Interpolator mDeccelerate;
    private Interpolator mAccelerate;

    public MenubarLayout(Context context) {
        this(context, null);
    }

    public MenubarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenubarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenubarLayout);
        mMenuHeight = a.getDimensionPixelSize(R.styleable.MenubarLayout_menu_height, -1);
        a.recycle();

        if (mMenuHeight == -1) {
            mMenuHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        }

        mMenuNames = new ArrayList<>();
        mUserLayouts = new ArrayList<>();
        mCurrentUserLayout = null;
        mCurrentUserIndex = -1;

        mTranslationYDown = PropertyValuesHolder.ofFloat("translationY", 0f, 1f);
        mAlphaDown = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        mSlideDown = ObjectAnimator.ofPropertyValuesHolder(mMenuSelectorView, mTranslationYDown, mAlphaDown);

        mTranslationYUp = PropertyValuesHolder.ofFloat("translationY", 1f, 0f);
        mAlphaUp = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        mSlideUp = ObjectAnimator.ofPropertyValuesHolder(mMenuSelectorView, mTranslationYUp, mAlphaUp);

        mTranslationXRightDisappearing = PropertyValuesHolder.ofFloat("translationX", 0f, 1f);
        mTranslationXRightAppearing = PropertyValuesHolder.ofFloat("translationX", 0f, 1f);
        mSlideRightDisappearing = ObjectAnimator.ofPropertyValuesHolder(mCurrentUserLayout, mTranslationXRightDisappearing, mAlphaUp);
        mSlideRightAppearing = ObjectAnimator.ofPropertyValuesHolder(mCurrentUserLayout, mTranslationXRightAppearing, mAlphaDown);

        mTranslationXLeftDisappearing = PropertyValuesHolder.ofFloat("translationX", 1f, 0f);
        mTranslationXLeftAppearing = PropertyValuesHolder.ofFloat("translationX", 1f, 0f);
        mSlideLeftDisappearing = ObjectAnimator.ofPropertyValuesHolder(mCurrentUserLayout, mTranslationXLeftDisappearing, mAlphaUp);
        mSlideLeftAppearing = ObjectAnimator.ofPropertyValuesHolder(mCurrentUserLayout, mTranslationXLeftAppearing, mAlphaDown);

        LayoutTransition.TransitionListener listener = new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                //Log.d(TAG, view.toString());
                //Log.d(TAG, container.toString());
                //Log.d(TAG, transition.getAnimator(transitionType).toString());
                if (transitionType == LayoutTransition.DISAPPEARING) {
                    startViewTransition(view);
                }
            }
            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (transitionType == LayoutTransition.DISAPPEARING) {
                    endViewTransition(view);
                }
            }
        };
        mDeccelerate = new DecelerateInterpolator(4f);
        mAccelerate = new AccelerateInterpolator(4f);

        mMenuSelectorTransition = new LayoutTransition();
        mMenuSelectorTransition.setAnimator(LayoutTransition.APPEARING, mSlideDown);
        mMenuSelectorTransition.setInterpolator(LayoutTransition.APPEARING, mDeccelerate);
        mMenuSelectorTransition.setAnimator(LayoutTransition.DISAPPEARING, mSlideUp);
        mMenuSelectorTransition.setInterpolator(LayoutTransition.DISAPPEARING, mDeccelerate);
        //mMenuSelectorTransition.setDuration(3000);

        mUserLayoutDisappearing = new LayoutTransition();
        mUserLayoutAppearing = new LayoutTransition();

        mMenuSelectorTransition.addTransitionListener(listener);
        mUserLayoutDisappearing.addTransitionListener(listener);
        mUserLayoutAppearing.addTransitionListener(listener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams lp = getLayoutParams(view);
            mMenuNames.add(lp.mName);
        }

        saveAndRemoveAllViews();

        mMenuBarView = new MenubarView(getContext(), false, "DEFAULT TITLE");

        mMenuSelectorView = new MenuSelectorView(getContext());
        mMenuSelectorView.setAdapter(new MenubarAdapter(mMenuNames, 10, this));

        mCenterLayoutManager = new CenterLinearLayoutManager(getContext());
        mMenuSelectorView.setLayoutManager(mCenterLayoutManager);

        addView(mMenuBarView);

        //Log.d(TAG, "onFininshInflate");
    }

    private void saveAndRemoveAllViews() {
        mUserLayouts.clear();
        for (int i=0; i<getChildCount(); i++) {
            mUserLayouts.add(getChildAt(i));
        }
        removeAllViews();
    }

    private boolean measureCurrentView(int widthSpec, int heightSpec) {
        if (mCurrentUserLayout == null) {
            return false;
        }
        LayoutParams lp = getLayoutParams(mCurrentUserLayout);
        if (lp == null || lp.mName == null) {
            return false;
        }

        mCurrentUserLayout.measure(widthSpec, heightSpec);
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
        int menuHeightSpec = MeasureSpec.makeMeasureSpec(mMenuHeight, MeasureSpec.EXACTLY);
        mMenuBarView.measure(menuWidthSpec, menuHeightSpec);
        mMenuSelectorView.measure(menuWidthSpec, menuHeightSpec);
        mCenterLayoutManager.setMenubarHeight(mMenuHeight);

        int viewWidthSpec = widthSpec;
        int viewHeightSpec = MeasureSpec.makeMeasureSpec(height - mMenuHeight, MeasureSpec.EXACTLY);

        measureCurrentView(viewWidthSpec, viewHeightSpec);

        //Log.d(TAG, String.format("onMeasure: mMenuBarView (%d, %d)", width, height/mHeightDivideBy));
        //Log.d(TAG, String.format("onMeasure: mMenuSelectorView (%d, %d)", width, height/mHeightDivideBy));
        //Log.d(TAG, String.format("onMeasure: mCurrentView (%d, %d)", width, height - height/mHeightDivideBy));
    }

    private boolean layoutCurrentView(int l, int t, int r, int b) {
        if (mCurrentUserLayout == null) {
            return false;
        }
        LayoutParams lp = getLayoutParams(mCurrentUserLayout);
        if (lp == null || lp.mName == null) {
            return false;
        }

        mCurrentUserLayout.layout(l, t, r, b);
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
        int offset = mMenuHeight;
        int offset2 = offset * 2;
        int viewTop = t + offset2;

        mMenuBarView.layout(l, t, r, offset);
        mMenuSelectorView.layout(l, t+offset, r, offset2);

        layoutCurrentView(l, viewTop, r, b);

        //Log.d(TAG, String.format("onLayout: mMenuBarView:(%d, %d, %d, %d)", l, t, r, offset));
        //Log.d(TAG, String.format("onLayout: mMenuSelectorView:(%d, %d, %d, %d)", l, t+offset, r, offset2));
        //Log.d(TAG, String.format("onLayout: mCurrentView:(%d, %d, %d, %d)", l, viewTop, r, b));
    }

    private void animateMenuSelectorView() {
        float h = mMenuSelectorView.getHeight();
        mTranslationYDown.setFloatValues(-h, 0f);
        mTranslationYUp.setFloatValues(0f, -h);

        setLayoutTransition(mMenuSelectorTransition);
        if (mMenuBarView.mActivate) {
            addView(mMenuSelectorView);
        } else {
            removeView(mMenuSelectorView);
        }
        setLayoutTransition(null);
    }

    /*package*/ void toggleMenubar() {
        mMenuBarView.mActivate = !mMenuBarView.mActivate;
        mMenuBarView.invalidate();
        animateMenuSelectorView();
    }

    /*package*/ void changeCurrentView(int index, String newTitle) {
        View newView = mUserLayouts.get(index);
        if (newView == mCurrentUserLayout) {
            return;
        }

        mMenuBarView.mTitle = newTitle.toUpperCase();
        mMenuBarView.invalidate();
        //toggleMenubar();

        boolean clockwise;
        if (mCurrentUserIndex > index) {
            clockwise = true;
        } else {
            clockwise = false;
        }
        animateCurrentUserLayout(newView, clockwise);
        mCurrentUserIndex = index;
        mCurrentUserLayout = newView;
    }

    private void animateCurrentUserLayout(View newView, boolean clockwise) {
        float w = getWidth();
        mTranslationXRightDisappearing.setFloatValues(0, w);
        mTranslationXRightAppearing.setFloatValues(-w, 0);

        mTranslationXLeftDisappearing.setFloatValues(0, -w);
        mTranslationXLeftAppearing.setFloatValues(w, 0);

        if (clockwise) {
            if (mCurrentUserLayout != null) {
                mUserLayoutDisappearing.setAnimator(LayoutTransition.DISAPPEARING, mSlideRightDisappearing);
                mUserLayoutDisappearing.setInterpolator(LayoutTransition.DISAPPEARING, mDeccelerate);
                setLayoutTransition(mUserLayoutDisappearing);
                removeView(mCurrentUserLayout);
            }

            mUserLayoutAppearing.setAnimator(LayoutTransition.APPEARING, mSlideRightAppearing);
            mUserLayoutAppearing.setInterpolator(LayoutTransition.APPEARING, mAccelerate);
            setLayoutTransition(mUserLayoutAppearing);
            addView(newView);
            setLayoutTransition(null);
        }
        else {
            if (mCurrentUserLayout != null) {
                mUserLayoutDisappearing.setAnimator(LayoutTransition.DISAPPEARING, mSlideLeftDisappearing);
                mUserLayoutDisappearing.setInterpolator(LayoutTransition.DISAPPEARING, mDeccelerate);
                setLayoutTransition(mUserLayoutDisappearing);
                removeView(mCurrentUserLayout);
                setLayoutTransition(null);
            }

            mUserLayoutAppearing.setAnimator(LayoutTransition.APPEARING, mSlideLeftAppearing);
            mUserLayoutAppearing.setInterpolator(LayoutTransition.APPEARING, mAccelerate);
            setLayoutTransition(mUserLayoutAppearing);
            addView(newView);
            setLayoutTransition(null);
        }
    }

    private LayoutParams getLayoutParams(View view) {
        if (view == null) return null;
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
