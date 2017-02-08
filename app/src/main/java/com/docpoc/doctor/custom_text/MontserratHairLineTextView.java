package com.docpoc.doctor.custom_text;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.docpoc.doctor.utils.MyCustomTypeface;

public class MontserratHairLineTextView extends TextView {

    public MontserratHairLineTextView(Context context) {
        super(context);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/monstreat_hairline.ttf")));

        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public MontserratHairLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/monstreat_hairline.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public MontserratHairLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface((MyCustomTypeface.getTypeFace(context, "fonts/monstreat_hairline.ttf")));
        this.setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}