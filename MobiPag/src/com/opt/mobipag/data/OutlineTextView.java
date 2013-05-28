package com.opt.mobipag.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class OutlineTextView extends TextView {

    private Typeface typeface;

    public OutlineTextView(Context context) {
        super(context);
    }

    public OutlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < 10; i++) {
            super.draw(canvas);
        }
    }
}
