package com.jr.tallybars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.widget.BaseAdapter;

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

    }

    private MyListData[] setTempListData(){

        MyListData[] myListData = new MyListData[] {
                new MyListData("Music Scales", android.R.drawable.ic_dialog_email),
                new MyListData("Music Pieces", android.R.drawable.ic_dialog_info),
                new MyListData("Duolingo", android.R.drawable.ic_delete),
                new MyListData("LingoDeer", android.R.drawable.ic_dialog_dialer),
                new MyListData("test1", android.R.drawable.ic_dialog_alert),
                new MyListData("test2", android.R.drawable.ic_dialog_map),
                new MyListData("test3", android.R.drawable.ic_dialog_email),
                new MyListData("test4", android.R.drawable.ic_dialog_info),
                new MyListData("test5", android.R.drawable.ic_delete),
                new MyListData("test6", android.R.drawable.ic_dialog_dialer),
                new MyListData("test7", android.R.drawable.ic_dialog_alert),
                new MyListData("test8", android.R.drawable.ic_dialog_map),
        };

        return myListData;
    }
}