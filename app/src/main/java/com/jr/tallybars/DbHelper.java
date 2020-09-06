package com.jr.tallybars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DbName = "TallyBarsDB";


    public DbHelper(Context context){
        super(context, DbName, null, 1);

       // this.addTestGroups();

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Groups(id INTEGER PRIMARY KEY, Groupname VARCHAR, Colour INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Items(id INTEGER PRIMARY KEY, groupId INTEGER, Itemname VARCHAR, Tally INTEGER, FOREIGN KEY(groupId) REFERENCES Groups(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Items");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Groups");
        this.onCreate(sqLiteDatabase);
    }


    //Insertion and Deletion Functions

    public boolean insertGroup(String group_name, int colour){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();

        content_values.put("Groupname", group_name);
        content_values.put("Colour", colour);

        sqLiteDatabase.insert("Groups", null, content_values);

        return true;
    }

    public boolean insertItemToGroup(int group_id, String item_name){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();

        content_values.put("groupId", group_id);
        content_values.put("Itemname", item_name);
        content_values.put("Tally", 0);

        sqLiteDatabase.insert("Items", null, content_values);

        return true;
    }

    public boolean deleteGroup(int group_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String[] args = {Integer.toString(group_id)};

        sqLiteDatabase.delete("Groups", "id=?", args);
        return true;
    }

    public boolean deleteGroupItem(int item_id, int group_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String[] args = {Integer.toString(item_id), Integer.toString(group_id)};

        sqLiteDatabase.delete("Items", "id=? AND groupId=?", args);
        return true;
    }

    //Data retrieval functions

    public Cursor getGroups(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] columns = {"Groupname", "Colour"};

        return sqLiteDatabase.query("Groups", columns, null, null, null, null,null,null);
    }

    public Cursor getGroupItems(int group_id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] columns = {"Itemname"," Tally"};
        String[] args = {Integer.toString(group_id)};

        return sqLiteDatabase.query("Items", columns, "groupId=?", args, null,null,null,null);
    }

    //Test data function

    private void addTestGroups(){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        this.onUpgrade(sqLiteDatabase, 0,0);

//        int black = Color.rgb(0,0,0);
//        int garb = Color.rgb(147,202,95);
//
//        insertGroup("Music Scales", garb);
//        insertGroup("Music Pieces", black);
//        insertGroup("Duolingo", black);
//        insertGroup("LingoDeer", Color.BLUE);
//        insertGroup("test1", Color.BLUE);
//        insertGroup("test2", Color.BLUE);

    }

}
