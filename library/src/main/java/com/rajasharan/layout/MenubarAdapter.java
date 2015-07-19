package com.rajasharan.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rajasharan on 7/18/15.
 */
/*package*/ class MenubarAdapter extends RecyclerView.Adapter<MenubarAdapter.MenuHolder> implements View.OnClickListener {
    private static final String TAG = "MenubarAdapter";

    private MarginLayoutParams mLayoutParams;
    private List<String> mMenuNames;
    private MenubarLayout mRoot;

    public MenubarAdapter(List<String> menunames, int spacing, MenubarLayout root) {
        super();
        mMenuNames = menunames;
        mLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacing,
                root.getContext().getResources().getDisplayMetrics());
        mRoot = root;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView menu = new TextView(parent.getContext());
        menu.setLayoutParams(mLayoutParams);
        menu.setOnClickListener(this);
        MenuHolder holder = new MenuHolder(menu);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        holder.menu.setText(mMenuNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mMenuNames.size();
    }

    @Override
    public void onClick(View v) {
        TextView menu = (TextView) v;
        String text = menu.getText().toString();

        boolean flag = false;
        int i;
        for (i=0; i<mMenuNames.size(); i++) {
            if (text.equalsIgnoreCase(mMenuNames.get(i))) {
                flag = true;
                break;
            }
        }

        if(flag) {
            mRoot.changeCurrentView(i);
        }

        Log.d(TAG, String.format("[%s] clicked: (i, flag)=(%s, %s)", text, i, flag));
    }

    static class MenuHolder extends RecyclerView.ViewHolder {
        TextView menu;

        public MenuHolder(View itemView) {
            super(itemView);
            menu = (TextView) itemView;
        }
    }
}
