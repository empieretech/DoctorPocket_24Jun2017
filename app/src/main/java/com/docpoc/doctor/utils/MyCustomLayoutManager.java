package com.docpoc.doctor.utils;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

public class MyCustomLayoutManager extends LinearLayoutManager {
    //We need mContext to create our LinearSmoothScroller
    private Context mContext;
 
    public MyCustomLayoutManager(Context context) {
        super(context);
        mContext = context;
    }
 
    //Override this method? Check.
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, int position) {
 
        //Create your RecyclerView.SmoothScroller instance? Check.
        LinearSmoothScroller smoothScroller =
            new LinearSmoothScroller(mContext) {
 
            //Automatically implements this method on instantiation.
            @Override
            public PointF computeScrollVectorForPosition
                (int targetPosition) {
                return null;
            }
        };
 
        //Docs do not tell us anything about this,
        //but we need to set the position we want to scroll to.
        smoothScroller.setTargetPosition(position);
 
        //Call startSmoothScroll(SmoothScroller)? Check.
        startSmoothScroll(smoothScroller);
    }
}