package com.jr.tallybars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawShapeView extends View {
    private Paint paint;

    private int colour = Color.BLACK;
    private int background_colour = Color.WHITE;
    private int x = 60;
    private int y = 120;
    private int rad = 50;

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
        paint.setColor(this.colour);
    }

    public void setColour(int colour){
        this.colour = colour;
        paint.setColor(this.colour);
        postInvalidate();
    }

    public void setBackgroundColour(int background_colour){
        this.background_colour = background_colour;

        postInvalidate();
    }

    public void setPos(int x, int y, int rad){
        this.x = x;
        this.y = y;
        this.rad = rad;

        postInvalidate();

    }

    public int getColor(){
        return this.colour;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(background_colour);
        canvas.drawCircle(this.x, this.y, this.rad, paint);
    }

}
