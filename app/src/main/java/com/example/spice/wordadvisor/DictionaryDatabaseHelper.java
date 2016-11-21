package com.example.spice.wordadvisor;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DictionaryDatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.example.spice.wordadvisor/databases/";

    private static String DB_NAME = "DictionaryWords.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public DictionaryDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if(dbExist){
        }else{

            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }


    private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkWordExists(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                "FROM words " +
                "WHERE field1=?";
        Cursor mCursor = db.rawQuery(query,new String[]{word.toLowerCase()});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total== 0)
            return false;
        else
            return true;
    }
}