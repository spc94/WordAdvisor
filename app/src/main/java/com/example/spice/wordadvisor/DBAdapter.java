package com.example.spice.wordadvisor;

/**
 * Created by spice on 11/6/16.
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DBAdapter {

    private SQLiteDatabase database;
    private DataBaseHelper dbHelper;
    private String[] singlesCollumns  = {dbHelper.ID_COLUMN, dbHelper.WORD_COLUMN};
    private String[] sequenceCollumns = {dbHelper.ID_COLUMN, dbHelper.SEQ_ID_COLUMN,
                                            dbHelper.NUM_SEQ};

    public DBAdapter(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    public boolean addWordToSingles(String word){
        ContentValues values = new ContentValues();

        if (wordExistsOnSingles(word)==false) {
            values.put(dbHelper.WORD_COLUMN,word);
            database.insert(dbHelper.SingleWords, null, values);
            Log.d("tag","Inserted into Singles");
        }
        else
            Log.d("tag","Not inserted into Singles");
        return true;
    }

    public boolean addWordToSequenceLeft(String word){
        ContentValues values = new ContentValues();

        if (wordExistsOnLeftSequence(word)==false) {
            values.put(dbHelper.ID_COLUMN,getIDFromWord(word));
            database.insert(dbHelper.SequenceWords, null, values);
            Log.d("tag","Inserted into Sequence-Right");
        }
        else
            Log.d("tag","Not inserted into Sequence-Right");
        return true;
    }

    public boolean addWordToSequenceRight(String word){
        ContentValues values = new ContentValues();

        if (wordExistsOnRightSequence(word)==false) {
            values.put(dbHelper.SEQ_ID_COLUMN,getIDFromWord(word));
            database.insert(dbHelper.SequenceWords, null, values);
            Log.d("tag","Inserted into Sequence-Left");
        }
        else
            Log.d("tag","Not inserted into Sequence-Left");
        return true;
    }


    public int getIDFromWord(String word){
        String query = "select id from singles where word = '?'";
        Cursor mCursor = database.rawQuery(query,new String[]{word});

        return mCursor.getInt(0);
    }

    public boolean wordExistsOnSingles(String word){
        String query = "select count(1)" +
                "from singles" +
                "where id = ?";
        Cursor mCursor = database.rawQuery(query,new String[]{String.valueOf(getIDFromWord(word))});
        Log.d("tag",""+mCursor.getCount());
        if(mCursor.getCount() == 0)
            return false;
        else
            return true;
    }


    public boolean wordExistsOnLeftSequence(String word){
        String query = "select count(1)" +
                "from sequences" +
                "where id = ?";
        Cursor mCursor = database.rawQuery(query,new String[]{String.valueOf(getIDFromWord(word))});
        Log.d("tag",""+mCursor.getCount());
        if(mCursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public boolean wordExistsOnRightSequence(String word){
        String query = "select count(1)" +
                "from sequences" +
                "where seq_id = ?";
        Cursor mCursor = database.rawQuery(query,new String[]{String.valueOf(getIDFromWord(word))});
        Log.d("tag",""+mCursor.getCount());
        if(mCursor.getCount() == 0)
            return false;
        else
            return true;
    }

}

