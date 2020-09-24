package com.jr.tallybars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DbName = "TallyBarsDB";


    public DbHelper(Context context){
        super(context, DbName, null, 1);

       //this.addTestGroups();

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Groups(id INTEGER PRIMARY KEY AUTOINCREMENT, Groupname VARCHAR, Colour INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Items(id INTEGER PRIMARY KEY AUTOINCREMENT, groupId INTEGER, Itemname VARCHAR, Tally INTEGER, FOREIGN KEY(groupId) REFERENCES Groups(id));");
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

    public boolean restoreDeletedGroup(String group_name, int colour, int position){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();

        content_values.put("id", position);
        content_values.put("Groupname", group_name);
        content_values.put("Colour", colour);

        sqLiteDatabase.insert("Groups", null, content_values);

        return true;
    }

    public boolean restoreDeletedItem(String item_name, int group_id, int tally, int position){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();

        content_values.put("id", position);
        content_values.put("groupId", group_id);
        content_values.put("Itemname", item_name);
        content_values.put("Tally", tally);

        sqLiteDatabase.insert("Items", null, content_values);

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

    public int deleteGroup(int displayed_list_index){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();


        int id = getGroupIdFromDisplayedIndex(displayed_list_index);

        String[] args = {Integer.toString(id)};
        sqLiteDatabase.delete("Items", "groupId=?",args);
        sqLiteDatabase.delete("Groups", "id=?", args);
        return id;
    }

    public int getGroupIdFromDisplayedIndex(int displayed_list_index){

        Cursor q = getGroups();
        q.moveToFirst();
        q.move(displayed_list_index);

        int idIndex = q.getColumnIndex("id");
        return q.getInt(idIndex);
    }

    public int getGroupItemIdFromDisplayedIndex(int group_id, int displayed_list_index){

        Cursor q = getGroupItems(group_id);
        q.moveToFirst();
        q.move(displayed_list_index);

        int idIndex = q.getColumnIndex("id");
        return q.getInt(idIndex);
    }

    public int deleteGroupItem(int item_id, int group_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();


        int id = getGroupItemIdFromDisplayedIndex(group_id, item_id);
        String[] args = {Integer.toString(id), Integer.toString(group_id)};

        sqLiteDatabase.delete("Items", "id=? AND groupId=?", args);

        return id;
    }

    //Data retrieval functions

    public Cursor getGroups(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] columns = {"id","Groupname", "Colour"};

        return sqLiteDatabase.query("Groups", columns, null, null, null, null,"id",null);
    }

    public Cursor getGroupItems(int group_id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] columns = {"id", "Itemname"," Tally"};
        String[] args = {Integer.toString(group_id)};

        return sqLiteDatabase.query("Items", columns, "groupId=?", args, null,null,null,null);
    }

    public HashMap<String, Integer> getGroupItemsInUsefulForm(int group_id){

        HashMap<String, Integer> h = new HashMap<>();
        Cursor q = this.getGroupItems(group_id);

        q.moveToFirst();
        for(int i = 0; i < q.getCount(); i++){
            h.put(q.getString(q.getColumnIndex("Itemname")), q.getInt(q.getColumnIndex("Tally")));
            q.moveToNext();
        }

        return h;
    }


    public void setItemTally(int group_id, int item_id, int tally){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Tally", tally);

        int id = getGroupItemIdFromDisplayedIndex(group_id, item_id);
        String[] args = {Integer.toString(group_id), Integer.toString(id)};
        sqLiteDatabase.update("Items", contentValues, "groupId=? AND id=?", args);
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
