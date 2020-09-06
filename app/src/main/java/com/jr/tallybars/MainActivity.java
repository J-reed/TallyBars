package com.jr.tallybars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {

    DbHelper db;
    MyListData[] groups;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DbHelper(this.getApplicationContext());

        this.groups = getGroupsFromDatabase();

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(groups);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(adapter);

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

                // Position the circle drawing nicely in the canvas
                final DrawShapeView displayColour = (DrawShapeView) popupView.findViewById(R.id.drawShapeCircle);
                displayColour.setBackgroundColour(Color.rgb(241,236,218));
                displayColour.setPos(60,80,50);

                // Setup SeekBar Listeners
                final SeekBar rSeekBar = (SeekBar) popupView.findViewById(R.id.rSeekBar);
                setupSeekBarListener(rSeekBar, displayColour, 0);

                final SeekBar gSeekBar = (SeekBar) popupView.findViewById(R.id.gSeekBar);
                setupSeekBarListener(gSeekBar, displayColour, 1);

                final SeekBar bSeekBar = (SeekBar) popupView.findViewById(R.id.bSeekBar);
                setupSeekBarListener(bSeekBar, displayColour, 2);

                //Handle the Ok button
                final EditText enterGroupNameTextBox = (EditText) popupView.findViewById(R.id.enterGroupNameTextBox);
                final Button popupOkButton = (Button) popupView.findViewById(R.id.popupOkButton);
                popupOkButton.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        String new_group_name = enterGroupNameTextBox.getText().toString().trim();
                        int new_color = Color.rgb(rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());

                        if(!new_group_name.isEmpty()){
                            db.insertGroup(new_group_name, new_color);
                            groups = getGroupsFromDatabase();
                            MyListAdapter adapter = new MyListAdapter(groups);
                            recyclerView.setAdapter(adapter);
                            recyclerView.invalidate();
                            popupWindow.dismiss();
                            popupView.performClick();
                        }
                        else{
                            enterGroupNameTextBox.setError("Please enter a group name!");
                        }


                    }
                });
            }
        });

    }

    private void setupSeekBarListener(SeekBar bar, final DrawShapeView displayColour, final int index){

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                int current_colour = displayColour.getColor();

                int new_colour;
                switch (index){
                    case 0:
                        new_colour = Color.rgb(i,Color.green(current_colour), Color.blue(current_colour));
                        break;
                    case 1:
                        new_colour = Color.rgb(Color.red(current_colour),i, Color.blue(current_colour));
                        break;
                    case 2:
                        new_colour = Color.rgb(Color.red(current_colour), Color.green(current_colour), i);
                        break;
                    default:
                        new_colour = current_colour;
                }

                displayColour.setColour(new_colour);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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
                new MyListData("test3", Color.BLUE),

        };

    }

    private MyListData[] getGroupsFromDatabase(){

        Cursor res = this.db.getGroups();
        MyListData[] myListData = new MyListData[res.getCount()];

        int groupNameColumnIndex = res.getColumnIndex("Groupname");
        int groupColourColumnIndex = res.getColumnIndex("Colour");

        res.moveToFirst();
        for(int i = 0; i < res.getCount(); i++){

            String groupName = res.getString(groupNameColumnIndex);
            int colour = res.getInt(groupColourColumnIndex);

            myListData[i] = new MyListData(groupName, colour);

            res.moveToNext();
        }

        return myListData;
    }
}