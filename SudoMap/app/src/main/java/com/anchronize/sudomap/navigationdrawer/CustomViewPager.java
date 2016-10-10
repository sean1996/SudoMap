package com.anchronize.sudomap.navigationdrawer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by glarencezhao on 4/17/16.
 * Modified from StackOverflow Solution
 * http://stackoverflow.com/questions/9650265/how-do-disable-paging-by-swiping-with-finger-in-viewpager-but-still-be-able-to-s/13437997#13437997
 */
public class CustomViewPager extends ViewPager{

    private boolean canSwipe;

    public CustomViewPager(Context context, AttributeSet attr){
        super(context, attr);
        this.canSwipe = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(canSwipe)
            return super.onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(canSwipe)
            return super.onInterceptTouchEvent(ev);
        return false;
    }

    public void setPagingEnabled(boolean canSwipe){
        this.canSwipe = canSwipe;
    }
}
