package com.jr.tallybars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawShapeView extends View {
    private Paint paint;

    public DrawShapeView(Context context) {
        super(context);
        init(context);
    }

    public DrawShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(60, 120, 50, paint);
    }

}
