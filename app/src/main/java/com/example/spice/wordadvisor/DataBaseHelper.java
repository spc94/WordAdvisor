package com.example.spice.wordadvisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper ins;
    DataBaseHelper getIns(){return ins;}

    private static final String DATABASE_NAME = "WordAdv";
    private static final int DATABASE_VERSION = 1;

    private static final String SingleWords = "singles";
    private static final String SequenceWords = "sequences";

    private static final String ID_COLUMN = "id";
    private static final String NUM_SEQ = "num_seq";
    private static final String WORD_COLUMN = "word";
    private static final String SEQ_ID_COLUMN = "seq_ID";
    private static final String NUM_SINGLE = "num_singles";

    private static final String CREATE_SINGLES_TABLE = "CREATE TABLE "
            + SingleWords + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY, "
            + WORD_COLUMN + " TEXT, "
            + NUM_SINGLE + " INT"
            + ")";

    private static final String CREATE_SEQUENCE_TABLE = "CREATE TABLE "
            + SequenceWords + "("
            + ID_COLUMN + " INT, "
            + SEQ_ID_COLUMN + " INT, "
            + NUM_SEQ + " INT, "
            + "FOREIGN KEY("+ID_COLUMN+") REFERENCES "+SingleWords+"("+ID_COLUMN+"), "
            + "FOREIGN KEY("+NUM_SEQ+") REFERENCES "+SingleWords+"("+ID_COLUMN+")"
            + ")";

    private static final String DROP_SEQUENCE_TABLE ="DROP TABLE " + SequenceWords;
    private static final String DROP_SINGLES_TABLE  ="DROP TABLE " + SingleWords;

    private static DataBaseHelper instance;
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEQUENCE_TABLE);
        db.execSQL(CREATE_SINGLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SingleWords);
        db.execSQL("DROP TABLE IF EXISTS " + SequenceWords);
        // Create tables again
        onCreate(db);
    }

    public boolean addWordToSingles(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (wordExistsOnSingles(word)==false) {
            values.put(WORD_COLUMN,word);
            values.put(NUM_SINGLE,1);
            db.insert(SingleWords, null, values);
            return true;
        }
        else {
            incrementSingles(word);
            return false;
        }
    }


    public void addWordToSequence(String prevWord, String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(checkSequenceExists(prevWord, word) == false){
            if(getIDFromWord(prevWord)==-2)
                addWordToSingles(prevWord);
            if(getIDFromWord(word)==-2)
                addWordToSingles(word);
            values.put(ID_COLUMN, getIDFromWord(prevWord));
            values.put(SEQ_ID_COLUMN, getIDFromWord(word));
            values.put(NUM_SEQ, 1);
            db.insert(SequenceWords, null, values);
        }
        else{
            incrementSequence(prevWord,word);
        }

    }


    public int getIDFromWord(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM singles";
        Cursor mCursor = db.rawQuery(count,null);
        mCursor.moveToFirst();
        int iCount = mCursor.getInt(0);
        mCursor.close();
        if(iCount <=0)
            return -1;

        String query = "SELECT id FROM singles WHERE word = ?";
        Cursor mCursor2 = db.rawQuery(query, new String[] {word});
        if(mCursor2.moveToFirst()==false) //Caso não exista na tabela
            return -2;
        int cursorValue = mCursor2.getInt(0);
        mCursor2.close();
        return cursorValue; //Retorna ID da palavra
    }

    public boolean wordExistsOnSingles(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                "FROM singles " +
                "WHERE word=?";

        Cursor mCursor = db.rawQuery(query,new String[]{word});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total == 0)
            return false;
        else
            return true;
    }


    public boolean wordExistsOnLeftSequence(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                "FROM sequences " +
                "WHERE id=?";
        Cursor mCursor = db.rawQuery(query,new String[]{""+getIDFromWord(word)});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total == 0)
            return false;
        else
            return true;
    }

    public boolean wordExistsOnRightSequence(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                "FROM sequences " +
                "WHERE seq_ID=?";

        Cursor mCursor = db.rawQuery(query,new String[]{""+getIDFromWord(word)});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total == 0)
            return false;
        else
            return true;
    }

    public void incrementSingles(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{""+getIDFromWord(word)};
        ContentValues values = new ContentValues();
        values.put(NUM_SINGLE,getNumSingles(word)+1);
        db.update(SingleWords,values, "id=?",args);
    }

    public void incrementSequence(String prevWord, String word){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{""+getIDFromWord(prevWord),""+getIDFromWord(word)};
        ContentValues values = new ContentValues();
        values.put(NUM_SEQ,getNumSeq(prevWord,word)+1);
        db.update(SequenceWords,values, "id=? AND seq_ID=?",args);
    }

    public int getNumSingles(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT num_singles " +
                "FROM singles " +
                "WHERE id=?";
        Cursor mCursor = db.rawQuery(query,new String[]{""+getIDFromWord(word)});
        mCursor.moveToFirst();
        int result = mCursor.getInt(0);
        mCursor.close();
        Log.d("DEBUG3","Number in table: "+result);
        return result;
    }

    public int getNumSeq(String prevWord, String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT num_seq " +
                    "FROM sequences " +
                    "WHERE id=? AND seq_ID=?";
        Cursor mCursor = db.rawQuery(query,new String[]{""+getIDFromWord(prevWord),
                ""+getIDFromWord(word)});
        mCursor.moveToFirst();
        int result = mCursor.getInt(0);
        mCursor.close();
        return result;
    }

    public boolean checkSequenceExists(String prevWord, String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                    "FROM sequences " +
                    "WHERE id=? AND seq_ID=?";
        Cursor mCursor = db.rawQuery(query,new String[]{""+getIDFromWord(prevWord),
                                                        ""+getIDFromWord(word)});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total== 0)
            return false;
        else
            return true;
    }

}