package com.example.admin1.gymtracker.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.admin1.gymtracker.R;

/*
  Important Note:
  This class was already part of my CA3 Assignment.  I have reused it entirely.
  The original class was created with the help of
  http://stackoverflow.com/questions/31242812/how-can-a-divider-line-be-added-in-an-android-recyclerview
  Inserts a diver line between cards.
*/

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration{
    private Drawable mDivider;

    public SimpleDividerItemDecoration(Context context) {
        mDivider = ResourcesCompat.getDrawable(context.getResources(), R.drawable.recycle_list_divider, null);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
