package com.jr.tallybars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DbHelper db;
    //MyListData[] groups;
    ArrayList<MyListData> groups;
    MyListAdapter adapter;
    RecyclerView recyclerView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setTitle("TallyBars");

        this.db = new DbHelper(this.getApplicationContext());

        this.groups = getGroupsFromDatabase();

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.adapter = new MyListAdapter(groups);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.adapter);

        this.handleSwipeToDelete();
        this.handleGroupAddPopup();


    }


    private void handleSwipeToDelete(){



        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }



            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {



                if(direction == ItemTouchHelper.LEFT){

                    // backup of removed item for undo purpose
                    final MyListData deletedItem = adapter.get_item(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    // get the removed item name to display it in snack bar
                    final String name = deletedItem.getDescription();
                    final int colour = deletedItem.getColour();
                    // remove the item from recycler view
                    adapter.remove_data_item(viewHolder.getAdapterPosition());

                    final int actual_index = db.deleteGroup(deletedIndex);

                    // showing snack bar with Undo option
                    final Snackbar snackbar = Snackbar
                            .make(viewHolder.itemView, name + " Deleted", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            db.restoreDeletedGroup(name, colour, actual_index);
                            adapter.restore_item(deletedItem, deletedIndex);

                        }
                    });

                    snackbar.setDuration(4000);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();

                }

            }

        };



        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this.recyclerView);

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
                            //groups = getGroupsFromDatabase();
                            adapter.add_data_item(new MyListData(new_group_name, new_color));

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


    private ArrayList<MyListData> getGroupsFromDatabase(){

        Cursor res = this.db.getGroups();
        ArrayList<MyListData> myListData = new ArrayList<>();

        int idIndex = res.getColumnIndex("id");
        int groupNameColumnIndex = res.getColumnIndex("Groupname");
        int groupColourColumnIndex = res.getColumnIndex("Colour");

        res.moveToFirst();
        for(int i = 0; i < res.getCount(); i++){


            String groupName = res.getString(groupNameColumnIndex);
            int colour = res.getInt(groupColourColumnIndex);

            System.out.println(res.getInt(idIndex));
            System.out.println(groupName);
            System.out.println(colour);

            myListData.add(new MyListData(groupName, colour));

            res.moveToNext();
        }

        return myListData;
    }
}