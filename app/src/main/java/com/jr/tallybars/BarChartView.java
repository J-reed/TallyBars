package com.jr.tallybars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

public class BarChartView extends AppCompatActivity {

    private BarChart barChart;
    private ToggleButton toggleButton;
    private RecyclerView recyclerView;

    private HashMap<String, Integer> items;
    private int group_id;

    private DbHelper db;
    private MyListAdapter adapter;

    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private ArrayList<Integer> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tallybar_page);

        Bundle extras = this.getIntent().getExtras();
        int position = extras.getInt("position");
        String group_name = extras.getString("group_name");

        this.getSupportActionBar().setTitle("TallyBars | Group: "+group_name);

        this.barChart = (BarChart) findViewById(R.id.barchart);
        this.toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        this.recyclerView = (RecyclerView) findViewById(R.id.items_recyclerView);

        this.barChart.getAxisLeft().setAxisMinValue(0);
        this.barChart.getAxisRight().setAxisMinValue(0);

        this.db = new DbHelper(this.getApplicationContext());



        this.group_id = db.getGroupIdFromDisplayedIndex(position);
        this.items = db.getGroupItemsInUsefulForm(this.group_id);

        ArrayList<MyListData> items_mylistdata = new ArrayList<>();
        for(String s : this.items.keySet()){
            items_mylistdata.add(new MyListData(s, 0, 0,0,0,0));
        }

        this.adapter = new MyListAdapter(items_mylistdata, true, this);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.adapter);

        this.drawbars();
        this.handleItemsAddPopup();
        this.handleSwipeToDelete();



        this.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(toggleButton.getText());
                System.out.println(toggleButton.isSelected());
                toggleButton.setSelected(!toggleButton.isSelected());
                drawbars();
            }
        });

    }

    public void increment_tally(int list_position){



        int value = values.get(list_position) + 1;
        db.setItemTally(group_id, list_position, (int)value);
        this.items = db.getGroupItemsInUsefulForm(this.group_id);

        drawbars();

    }

    public void decrement_tally(int list_position){

        int value = values.get(list_position) - 1;
        db.setItemTally(group_id, list_position, (int)value);
        this.items = db.getGroupItemsInUsefulForm(this.group_id);

        drawbars();


    }

    private void drawbars(){

        entries = new ArrayList<>();
        labels = new ArrayList<>();
        values = new ArrayList<>();
        //Total
        if(this.toggleButton.isSelected()){
            System.out.println("TOTAL");
            int i = 0;
            for (String s : this.items.keySet()){
                labels.add(s);

                int val = this.items.get(s);
                values.add(val);
                entries.add(new BarEntry((float)val, i));
                i++;
            }
        }
        //Balanced
        else{
            System.out.println("BALANCED");

            int minimum = 0;

            int j = 0;
            for (String s : this.items.keySet()){

                labels.add(s);
                int val = this.items.get(s);

                if(j == 0) {
                    minimum = val;
                }

                if(val < minimum){
                    minimum = val;
                }

                values.add(val);
                j++;
            }

            for (int i = 0; i < values.size(); i++){
                entries.add(new BarEntry((float)(values.get(i)-minimum), i));
            }

        }

        BarDataSet bardataset = new BarDataSet(entries, "Cells");
        BarData data = new BarData(labels, bardataset);

        barChart.setData(data); // set the data and list of labels into chart
        barChart.setDescription("");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.notifyDataSetChanged();
        barChart.animateY(1);

    }


    private void handleItemsAddPopup(){

        //Handle creating popup
        ImageButton itemAddButton = (ImageButton) findViewById(R.id.itemAddButton);
        itemAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show a popup view

                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.item_add_popup, null);

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


                //Handle the Ok button
                final EditText enterItemNameTextBox = (EditText) popupView.findViewById(R.id.enterItemNameTextBox);
                final Button popupOkButton = (Button) popupView.findViewById(R.id.item_popupOkButton);
                popupOkButton.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        String new_item_name = enterItemNameTextBox.getText().toString().trim();

                        if(!new_item_name.isEmpty()){
                            db.insertItemToGroup(group_id, new_item_name);
                            adapter.add_data_item(new MyListData(new_item_name, 0,0,0,0,0));

                            items = db.getGroupItemsInUsefulForm(group_id);
                            drawbars();

                            popupWindow.dismiss();
                            popupView.performClick();
                        }
                        else{
                            enterItemNameTextBox.setError("Please enter an item name!");
                        }


                    }
                });
            }
        });

    }

    private void handleSwipeToDelete(){



        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            boolean delete_db_group = true;

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {



                if(direction == ItemTouchHelper.LEFT){

                    // backup of removed item for undo purpose
                    final MyListData deletedItem = adapter.get_item(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    // get the removed item name to display it in snack bar
                    final String name = deletedItem.getDescription();
                    final int tally = items.get(name);
                    // remove the item from recycler view
                    adapter.remove_data_item(viewHolder.getAdapterPosition());

                    final int actual_index = db.deleteGroupItem(deletedIndex, group_id);

                    // showing snack bar with Undo option
                    final Snackbar snackbar = Snackbar
                            .make(viewHolder.itemView, name + " Deleted", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            db.restoreDeletedItem(name, group_id, tally, actual_index);
                            items = db.getGroupItemsInUsefulForm(group_id);
                            adapter.restore_item(deletedItem, deletedIndex);
                            drawbars();
                        }
                    });

                    items = db.getGroupItemsInUsefulForm(group_id);
                    drawbars();

                    snackbar.setDuration(4000);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();

                }

            }

        };



        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this.recyclerView);

    }


}