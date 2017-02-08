package com.docpoc.doctor.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobottoTextView extends TextView {

    public RobottoTextView(Context context) {
        super(context);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));


        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public RobottoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));

        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public RobottoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));
       
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}