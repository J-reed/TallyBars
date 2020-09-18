package com.jr.tallybars;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<MyListData> listdata;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<MyListData> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = this.listdata.get(position);
        holder.textView.setText(myListData.getDescription());
        holder.drawShapeView.setColour(myListData.getColour());
        holder.drawShapeView.setPos(myListData.getX(), myListData.getY(), myListData.getRad());
        holder.drawShapeView.setBackgroundColour(myListData.getBackgroundColour());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.listdata.size();
    }

    public void remove_data_item(int position){
        this.listdata.remove(position);
        notifyItemRemoved(position);
    }

    public void restore_item(MyListData item, int position){
        this.listdata.add(position, item);
        notifyItemInserted(position);
    }

    public void add_data_item(MyListData item){
        this.listdata.add(item);
        notifyItemInserted(this.listdata.size()-1);
    }

    public MyListData get_item(int position){
        return this.listdata.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DrawShapeView drawShapeView;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.drawShapeView = (DrawShapeView) itemView.findViewById(R.id.drawShapeView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.listItems);
        }
    }
}
