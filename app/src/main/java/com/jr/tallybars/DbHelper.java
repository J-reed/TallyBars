package com.jr.tallybars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import static android.database.DatabaseUtils.dumpCursorToString;

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

    public int getMinTallyItemValueFromGroup(int group_id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] args = {Integer.toString(group_id)};
        Cursor q = sqLiteDatabase.rawQuery("SELECT MIN(Tally) AS Tally FROM Items WHERE groupId=?;", args);
        q.moveToFirst();
        int min_tally_ID = q.getColumnIndex("Tally");

        return q.getInt(min_tally_ID);
    }

    public int getGroupItemTallyValueFromDisplayedIndex(int group_id, int displayed_list_index){
        Cursor q = getGroupItems(group_id);
        q.moveToFirst();
        q.move(displayed_list_index);

        int tallyIndex = q.getColumnIndex("Tally");
        return q.getInt(tallyIndex);
    }

    public void setGroupItemTally(int group_id, int displayed_list_index, int tally_value){

        Cursor q = getGroupItems(group_id);
        q.moveToFirst();
        q.move(displayed_list_index);

        int idIndex = q.getColumnIndex("id");
        int item_nameIndex = q.getColumnIndex("Itemname");

        int id = q.getInt(idIndex);
        String item_name = q.getString(item_nameIndex);


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues content_values = new ContentValues();

        content_values.put("id", id);
        content_values.put("groupId", group_id);
        content_values.put("Itemname", item_name);
        content_values.put("Tally", tally_value);

        String[] args = {Integer.toString(id), Integer.toString(group_id)};
        sqLiteDatabase.update("Items",content_values, "id=? AND groupId=?", args);

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

    public synchronized Cursor getGroupItems(int group_id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] columns = {"id", "Itemname"," Tally"};
        String[] args = {Integer.toString(group_id)};

        return sqLiteDatabase.query("Items", columns, "groupId=?", args, null,null,null,null);
    }

    public HashMap<String, Integer> groupItemsToHashmap(int group_id){

        HashMap<String, Integer> h = new HashMap<>();
        Cursor q = this.getGroupItems(group_id);

        q.moveToFirst();
        for(int i = 0; i < q.getCount(); i++){
            h.put(q.getString(q.getColumnIndex("Itemname")), q.getInt(q.getColumnIndex("Tally")));
            q.moveToNext();
        }

        return h;
    }



    //Test data function
/*
    private void addTestGroups(){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        this.onUpgrade(sqLiteDatabase, 0,0);

    }
*/
}
