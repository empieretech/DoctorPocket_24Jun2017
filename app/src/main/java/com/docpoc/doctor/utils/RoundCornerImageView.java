package com.docpoc.doctor.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Sagar Sojitra on 09-01-16.
 */
public class RoundCornerImageView extends ImageView {


    private float mCornerRadius = 10.0f;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // Round some corners betch!
        /*Drawable myDrawable = getDrawable();

        if (myDrawable != null && myDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Paint paint = ((BitmapDrawable) myDrawable).getPaint();
            final int color = 0xff000000;
            Rect bitmapBounds = myDrawable.getBounds();
            final RectF rectF = new RectF(bitmapBounds);
            // Create an off-screen bitmap to the PorterDuff alpha blending to work right
            int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.MATRIX_SAVE_FLAG |
                            Canvas.CLIP_SAVE_FLAG |
                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                            Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            // Resize the rounded rect we'll clip by this view's current bounds
            // (super.onDraw() will do something similar with the drawable to draw)
            getImageMatrix().mapRect(rectF);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

            Xfermode oldMode = paint.getXfermode();
            // This is the paint already associated with the BitmapDrawable that super draws
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);
        } else {*/
            super.onDraw(canvas);
      //  }
    }


}