package com.rajasharan.layout;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajasharan on 7/7/15.
 */
public class MenubarLayout extends ViewGroup {
    private static final String TAG = "MenubarLayout";

    private MenubarView mMenuBarView;
    private MenuSelectorView mMenuSelectorView;
    private int mHeightDivideBy;
    private List<String> mMenuNames;
    private List<View> mUserLayouts;
    private View mCurrentUserLayout;
    private LayoutTransition mTransition;
    private ObjectAnimator mSlideDown;
    private ObjectAnimator mSlideUp;
    private PropertyValuesHolder mTranslationYDown;
    private PropertyValuesHolder mTranslationYUp;
    private PropertyValuesHolder mAlphaDown;
    private PropertyValuesHolder mAlphaUp;

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
        mHeightDivideBy = 20;
        mMenuNames = new ArrayList<>();
        mUserLayouts = new ArrayList<>();
        mCurrentUserLayout = null;

        mTranslationYDown = PropertyValuesHolder.ofFloat("translationY", 0f, 1f);
        mAlphaDown = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        mSlideDown = ObjectAnimator.ofPropertyValuesHolder(mMenuSelectorView, mTranslationYDown, mAlphaDown);

        mTranslationYUp = PropertyValuesHolder.ofFloat("translationY", 1f, 0f);
        mAlphaUp = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        mSlideUp = ObjectAnimator.ofPropertyValuesHolder(mMenuSelectorView, mTranslationYUp, mAlphaUp);

        mTransition = new LayoutTransition();
        mTransition.setAnimator(LayoutTransition.APPEARING, mSlideDown);
        mTransition.setInterpolator(LayoutTransition.APPEARING, new DecelerateInterpolator(4f));
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, mSlideUp);
        mTransition.setInterpolator(LayoutTransition.DISAPPEARING, new DecelerateInterpolator(4f));

        mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                MenubarLayout.this.startViewTransition(view);
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                MenubarLayout.this.endViewTransition(view);
            }
        });
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
        mMenuSelectorView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
        int menuHeightSpec = MeasureSpec.makeMeasureSpec(height/mHeightDivideBy, MeasureSpec.EXACTLY);
        mMenuBarView.measure(menuWidthSpec, menuHeightSpec);
        mMenuSelectorView.measure(menuWidthSpec, menuHeightSpec);

        int viewWidthSpec = widthSpec;
        int viewHeightSpec = MeasureSpec.makeMeasureSpec(height - height/mHeightDivideBy, MeasureSpec.EXACTLY);

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
        int offset = b/mHeightDivideBy;
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
        float h = 2 * mMenuSelectorView.getHeight();
        mTranslationYDown.setFloatValues(-h, 0f);
        mTranslationYUp.setFloatValues(0f, -h);

        setLayoutTransition(mTransition);
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
        toggleMenubar();

        if (mCurrentUserLayout != null) {
            removeView(mCurrentUserLayout);
        }
        addView(newView);
        mCurrentUserLayout = newView;
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
