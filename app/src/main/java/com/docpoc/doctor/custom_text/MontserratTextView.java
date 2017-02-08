package com.docpoc.doctor.custom_text;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.docpoc.doctor.utils.MyCustomTypeface;

public class MontserratTextView extends TextView {

    public MontserratTextView(Context context) {
        super(context);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));

        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public MontserratTextView(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public MontserratTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/montserrat_regular.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}