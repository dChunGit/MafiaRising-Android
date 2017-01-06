package com.illum.MafiaRising;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

//class for a ViewPager that allows either horizontal or vertical swipes for each page
//each page can have a set swipe direction attribute or it will use the default swipe direction
// which can be set in an attribute
public class BidirectionalViewPager extends ViewPager {

    //default overall swipe direction, horizontal
    //can be overridden by pageSwipeDirection of BidirectionalViewPage
    String pagerSwipeDirection;
    //static string constants
    static String swipeHorizontalString;
    static String swipeVerticalString;

    public BidirectionalViewPager(Context context) {
        super(context);
        init();
    }

    public BidirectionalViewPager(Context context, AttributeSet attribs) {
        super(context, attribs);

        swipeHorizontalString = getResources().getString(R.string.swipe_direction_horizontal);
        swipeVerticalString = getResources().getString(R.string.swipe_direction_vertical);

        TypedArray attribArray = context.obtainStyledAttributes(
                attribs,
                R.styleable.BidirectionalViewPager);

        pagerSwipeDirection = attribArray.getString(R.styleable.BidirectionalViewPager_pagerSwipeDirection);
        if (pagerSwipeDirection == null) {
            pagerSwipeDirection = swipeHorizontalString;
        }

        attribArray.recycle();

        init();
    }

    //set slide effect and no overscroll effect (indication that reached end and cannot swipe)
    private void init() {
        setPageTransformer(true, new BidirectionalPageTransformer());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    //class for sliding transformation effect, applied to every page that is loaded
    private class BidirectionalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            //page is not visible, hide itself
            if(position < -1 || position > 1) {
                view.setAlpha(0);
            }
            //page is somewhere on screen, make it visible and transform it appropriately
            // i (maybe?) gets the last-swiped view; the current swipe direction should copy
            //  the direction of that view
            // e.g. 3 screens, 1st - swipe L/R, 2nd - swipe U/D, 3rd - swipe L/R
            //  user swipes R, swipe D, swipe R
            //  the visible views should transform appropriately
            //  so 1+2 slide L and 2+3 slide Up
            // unfortunately, there is a bug where if the user swipes back to a previous screen,
            //  the transform will be wrong if the directions were different
            // e.g. using previous example, user swipes R, D, then L
            //  2 will slide vertically while 3 will slide horizontally => not right
            // solution? need way to determine which child view is the "previous" screen and get
            //  the swipe direction of that screen properly which current method fails to do
            else {
                view.setAlpha(1);
                //get the (position?) of the current view in the viewPager
                int i = indexOfChild(view);
                //this page is on the left
                // currently, there is no difference
                if (position < 0) {
                    //"supposed" to get the previous view
                    if(i > 1) {
                        i--;
                    }
                    //Log.i("INFO", "L: "+Integer.toString(i));
                    BidirectionalViewPage curPage = (BidirectionalViewPage) getChildAt(i);
                    String pageSwipeDirection = curPage.getPageSwipeDirection();
                    if((pageSwipeDirection != null && pageSwipeDirection.equals(swipeVerticalString)) || pagerSwipeDirection.equals(swipeVerticalString)) {
                        view.setTranslationX(view.getWidth() * -position);
                        float yPos = position * view.getHeight();
                        view.setTranslationY(yPos);
                    }
                }
                //this page is on the right
                else {
                    if(i > 1) {
                        i--;
                    }
                    //Log.i("INFO", "R: "+Integer.toString(i));
                    BidirectionalViewPage curPage = (BidirectionalViewPage) getChildAt(i);
                    String pageSwipeDirection = curPage.getPageSwipeDirection();
                    if((pageSwipeDirection != null && pageSwipeDirection.equals(swipeVerticalString)) || pagerSwipeDirection.equals(swipeVerticalString)) {
                        view.setTranslationX(view.getWidth() * -position);
                        float yPos = position * view.getHeight();
                        view.setTranslationY(yPos);
                    }
                }
            }
        }
    }

    //allows to "detect" vertical swipes
    private MotionEvent swapXY(MotionEvent evt) {
        float width = getWidth();
        float height = getHeight();

        float finalX = (evt.getY() / height) * width;
        float finalY = (evt.getX() / width) * height;

        evt.setLocation(finalX, finalY);

        return evt;
    }

    /* maybe not needed?
    @Override
    public boolean onInterceptTouchEvent(MotionEvent evt) {
        if(pagerSwipeDirection.equals(swipeVerticalString)) {
            boolean intercepted = super.onInterceptTouchEvent(swapXY(evt));
            swapXY(evt);
            return intercepted;
        }
        else {
            return super.onInterceptTouchEvent(evt);
        }
    }*/

    /* maybe not needed?
    public boolean onTouchChildEvent(MotionEvent evt, View view) {
        BidirectionalViewPage p = (BidirectionalViewPage) view;

        String pageSwipeDirection = p.getPageSwipeDirection();
        if((pageSwipeDirection != null && pageSwipeDirection.equals(swipeVerticalString)) || pagerSwipeDirection.equals(swipeVerticalString)) {
            return super.onTouchEvent(swapXY(evt));
        }
        else {
            return super.onTouchEvent(evt);
        }
    }*/

    //handle touch event
    //determine which page was touched, get its swipe direction and allow vertical swipe if needed
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        int x = Math.round(evt.getX());
        int y = Math.round(evt.getY());
        //figure out which child was touched
        int numChildren = getChildCount();
        for(int i=0;i<numChildren;++i) {
            View child = getChildAt(i);
            Rect bb = new Rect();
            child.getGlobalVisibleRect(bb);
            //this child was touched
            if(bb.contains(x,y)) {
                BidirectionalViewPage curPage = (BidirectionalViewPage) child;
                //get the child's swipe direction
                String pageSwipeDirection = curPage.getPageSwipeDirection();
                //handle if vertical viewPager
                if((pageSwipeDirection != null && pageSwipeDirection.equals(swipeVerticalString)) || pagerSwipeDirection.equals(swipeVerticalString)) {
                    return super.onTouchEvent(swapXY(evt));
                }
                else {
                    return super.onTouchEvent(evt);
                }
            }
        }
        return super.onTouchEvent(evt);
    }
}
