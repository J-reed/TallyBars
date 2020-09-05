package com.jr.tallybars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyListData[] myListData = setTempListData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.handleGroupAddPopup();

    }

    private void handleGroupAddPopup(){

        //Handle creating popup
        ImageButton groupAddButton = (ImageButton) findViewById(R.id.groupAddButton);
        groupAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show a popup view



                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.group_add_popup, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        popupView.performClick();
                        return true;
                    }
                });

                final DrawShapeView displayColour = (DrawShapeView) popupView.findViewById(R.id.drawShapeCircle);
                displayColour.setBackgroundColour(Color.rgb(241,236,218));
                displayColour.setPos(60,80,50);

                SeekBar rSeekBar = (SeekBar) popupView.findViewById(R.id.rSeekBar);
                rSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int current_colour = displayColour.getColor();
                        displayColour.setColour(Color.rgb(i, Color.green(current_colour), Color.blue(current_colour)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                SeekBar gSeekBar = (SeekBar) popupView.findViewById(R.id.gSeekBar);
                gSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int current_colour = displayColour.getColor();
                        displayColour.setColour(Color.rgb(Color.red(current_colour), i, Color.blue(current_colour)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                SeekBar bSeekBar = (SeekBar) popupView.findViewById(R.id.bSeekBar);
                bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int current_colour = displayColour.getColor();
                        displayColour.setColour(Color.rgb(Color.red(current_colour), Color.green(current_colour), i));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });







//        Button popupOkButton = (Button) findViewById(R.id.popupOkButton);
//        popupOkButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                String newGroupName = ((EditText) findViewById(R.id.enterGroupNameTextBox)).getText().toString();
//
//                int r = Integer.parseInt(((EditText) findViewById(R.id.rNumberEditText)).getText().toString());
//                int g = Integer.parseInt(((EditText) findViewById(R.id.gNumberEditText)).getText().toString());
//                int b = Integer.parseInt(((EditText) findViewById(R.id.bNumberEditText)).getText().toString());
//
//                int colour = Color.rgb(r,g,b);
//
//                MyListData newGroup = new MyListData(newGroupName, colour);
//            }
//        });

    }

    private MyListData[] setTempListData(){

        int black = Color.rgb(0,0,0);
        int garb = Color.rgb(147,202,95);

        return new MyListData[] {
                new MyListData("Music Scales", garb),
                new MyListData("Music Pieces", black),
                new MyListData("Duolingo", black),
                new MyListData("LingoDeer", black),
                new MyListData("test1", garb),
                new MyListData("test2", black),
                new MyListData("test3", black),
                new MyListData("test4", black),
                new MyListData("test5", Color.BLUE),
                new MyListData("test6", black),
                new MyListData("test7", black),
                new MyListData("test8", black)
        };

    }
}