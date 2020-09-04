package com.jr.tallybars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.graphics.Color;

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

        int black = Color.rgb(0,0,0);
        int garb = Color.rgb(147,202,95);

        return new MyListData[] {
                new MyListData("Music Scales", garb),
                new MyListData("Music Pieces", black),
                new MyListData("Duolingo", black),
                new MyListData("LingoDeer", black),
                new MyListData("test1", black),
                new MyListData("test2", black)
        };

    }
}