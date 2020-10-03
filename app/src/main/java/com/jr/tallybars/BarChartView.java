package com.jr.tallybars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.renderer.XAxisRenderer;
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
        this.barChart.setAutoScaleMinMaxEnabled(true);

        this.db = new DbHelper(this.getApplicationContext());



        this.group_id = db.getGroupIdFromDisplayedIndex(position);
        this.items = db.groupItemsToHashmap(this.group_id);

        ArrayList<MyListData> items_mylistdata = new ArrayList<>();
        Cursor q = db.getGroupItems(group_id);
        q.moveToFirst();
        int item_name_index = q.getColumnIndex("Itemname");

        for(int i = 0; i < q.getCount(); i++){
            items_mylistdata.add(new MyListData(q.getString(item_name_index), 0, 0,0,0,0));
            q.moveToNext();
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
                toggleButton.setSelected(!toggleButton.isSelected());
                drawbars();
            }
        });

    }

    public void increment_tally(int list_position){
        // Get value from database
        int tally_value = db.getGroupItemTallyValueFromDisplayedIndex(group_id, list_position);
        // Add 1
        tally_value += 1;
        // Store value in database
        db.setGroupItemTally(group_id, list_position, tally_value);

        drawbars();
    }

    public void decrement_tally(int list_position){
        int tally_value = db.getGroupItemTallyValueFromDisplayedIndex(group_id, list_position);
        // Subtract 1
        tally_value -= 1;
        // Store value in database
        db.setGroupItemTally(group_id, list_position, tally_value);

        drawbars();
    }

    private void drawbars(){



        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        BarData barData = new BarData(labels, barDataSet);
        barChart.setData(barData);
        barChart.setDescription("");


        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);

        // Read values from database
        Cursor c = db.getGroupItems(group_id);
        c.moveToFirst();

        // Draw Chart from scratch





        int longest_string_length = -1;
        for (int i = 0; i < c.getCount(); i++) {
            int itemname_col = c.getColumnIndex("Itemname");
            int tally_col = c.getColumnIndex("Tally");


            float db_tally_value = c.getInt(tally_col);
            float tally_value = toggleButton.isSelected() ? db_tally_value : db_tally_value - db.getMinTallyItemValueFromGroup(group_id);

            barEntries.add(new BarEntry(tally_value,i));

            String label_value = c.getString(itemname_col);
            if(label_value.length() > longest_string_length){
                longest_string_length = label_value.length();
            }

            labels.add(label_value);
            c.moveToNext();
        }


        int width_in_chars = 60;
        int width__in_chars_per_bar = labels.size() > 0 ? width_in_chars / labels.size() : -1;

        float no_lines = (longest_string_length / width__in_chars_per_bar) + 1;

        int offset_mulitplier = 12;

        barChart.setExtraTopOffset(no_lines * offset_mulitplier);
        barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(barChart.getAxisLeft().getAxisDependency()), width__in_chars_per_bar));




        barChart.notifyDataSetChanged();
        barChart.postInvalidate();
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

                            items = db.groupItemsToHashmap(group_id);
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
                    adapter.notifyDataSetChanged();

                    final int actual_index = db.deleteGroupItem(deletedIndex, group_id);

                    // showing snack bar with Undo option
                    final Snackbar snackbar = Snackbar
                            .make(viewHolder.itemView, name + " Deleted", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            db.restoreDeletedItem(name, group_id, tally, actual_index);
                            items = db.groupItemsToHashmap(group_id);
                            adapter.restore_item(deletedItem, deletedIndex);
                            adapter.notifyDataSetChanged();
                            drawbars();
                        }
                    });

                    items = db.groupItemsToHashmap(group_id);
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