package com.jr.tallybars;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class CustomXAxisRenderer extends XAxisRenderer {

//    boolean toggleY = true;
    int no_letters;

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, int no_letters) {
        super(viewPortHandler, xAxis, trans);
        this.no_letters = no_letters;
    }


    @Override
    protected void drawLabel(Canvas c, String label, int xIndex, float x, float y, PointF anchor, float angleDegrees) {
        String formattedLabel = mXAxis.getValueFormatter().getXValue(label, xIndex, mViewPortHandler);
        System.out.println(formattedLabel);

        ArrayList<String> label_lines = new ArrayList<>();

        int offset = (formattedLabel.length() / no_letters + 1)*30;

        int i_last = 0;
        int counter = 0;
        for(int i = 1; i < formattedLabel.length(); i++){

            if(i%no_letters==0){
                Utils.drawText(c, formattedLabel.substring(i_last,i), x, y -offset+(30*counter), mAxisLabelPaint, anchor, angleDegrees);
                i_last = i;
                counter++;
            }

        }
        Utils.drawText(c, formattedLabel.substring(i_last), x, y -offset+(30*counter), mAxisLabelPaint, anchor, angleDegrees);


//        if(toggleY){
//            Utils.drawText(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
//        }
//        else{
//            Utils.drawText(c, formattedLabel, x, y-40, mAxisLabelPaint, anchor, angleDegrees);
//        }
//
//        toggleY = !toggleY;
    }
}