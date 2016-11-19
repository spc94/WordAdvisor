package com.example.spice.wordadvisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper ins = null;
    public static DataBaseHelper getIns(Context ctx){
            if (ins == null) {
                ins = new DataBaseHelper(ctx.getApplicationContext());
            }
        return ins;
    }

    private static final String DATABASE_NAME = "WordAdv";
    private static final int DATABASE_VERSION = 1;

    private static final String SingleWords = "singles";
    private static final String SequenceWords = "sequences";
    private static final String SequenceSequenceWords = "sequencesSequences";

    private static final String ID_COLUMN = "id";
    private static final String NUM_SEQ = "num_seq";
    private static final String WORD_COLUMN = "word";
    private static final String SEQ_ID_COLUMN = "seq_ID";
    private static final String SEQ_SEQ_ID_COLUMN = "seq_seq_ID";
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
            + "FOREIGN KEY("+SEQ_ID_COLUMN+") REFERENCES "+SingleWords+"("+ID_COLUMN+")"
            + ")";

    private static final String CREATE_SEQUENCE_SEQUENCE_TABLE = "CREATE TABLE "
            + SequenceSequenceWords + "("
            + ID_COLUMN + " INT, "
            + SEQ_ID_COLUMN + " INT, "
            + SEQ_SEQ_ID_COLUMN + " INT, "
            + NUM_SEQ + " INT, "
            + "FOREIGN KEY("+ID_COLUMN+") REFERENCES "+SingleWords+"("+ID_COLUMN+"), "
            + "FOREIGN KEY("+SEQ_ID_COLUMN+") REFERENCES "+SingleWords+"("+ID_COLUMN+"), "
            + "FOREIGN KEY("+SEQ_SEQ_ID_COLUMN+") REFERENCES "+SingleWords+"("+ID_COLUMN+")"
            + ")";

    private static final String DROP_SEQUENCE_TABLE ="DROP TABLE " + SequenceWords;
    private static final String DROP_SINGLES_TABLE  ="DROP TABLE " + SingleWords;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEQUENCE_TABLE);
        db.execSQL(CREATE_SINGLES_TABLE);
        db.execSQL(CREATE_SEQUENCE_SEQUENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SingleWords);
        db.execSQL("DROP TABLE IF EXISTS " + SequenceWords);
        db.execSQL("DROP TABLE IF EXISTS " + SequenceSequenceWords);
        // Create tables again
        onCreate(db);
    }

    public boolean addWordToSingles(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (wordExistsOnSingles(word)==false) {
            values.put(WORD_COLUMN,word.toLowerCase());
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

    public void addWordToSequenceSequence(String prevWord, String word, String nextWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(checkSequenceSequenceExists(prevWord, word, nextWord) == false){
            if(getIDFromWord(prevWord)==-2)
                addWordToSingles(prevWord);
            if(getIDFromWord(word)==-2)
                addWordToSingles(word);
            if(getIDFromWord(nextWord)==-2)
                addWordToSingles(nextWord);
            values.put(ID_COLUMN, getIDFromWord(prevWord));
            values.put(SEQ_ID_COLUMN, getIDFromWord(word));
            values.put(SEQ_SEQ_ID_COLUMN, getIDFromWord(nextWord));
            values.put(NUM_SEQ, 1);
            db.insert(SequenceSequenceWords, null, values);
        }
        else{
            incrementSequenceSequence(prevWord,word,nextWord);
        }

    }

    public boolean checkSequenceSequenceExists(String prevWord, String word, String nextWord){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                "FROM sequencesSequences " +
                "WHERE id=(SELECT id FROM singles WHERE word=?) AND " +
                "seq_ID=(SELECT id FROM singles WHERE word=?) AND " +
                "seq_seq_ID=(SELECT id FROM singles WHERE word=?)";
        Cursor mCursor = db.rawQuery(query,new String[]{prevWord,word,nextWord});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total== 0)
            return false;
        else
            return true;
    }

    public void incrementSequenceSequence(String prevWord, String word, String nextWord){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{prevWord,word,nextWord};
        ContentValues values = new ContentValues();
        values.put(NUM_SEQ,getNumSeqSeq(prevWord,word,nextWord)+1);
        db.update(SequenceSequenceWords,values, "id=(SELECT id FROM singles WHERE word=?) " +
                "AND seq_ID=(SELECT id FROM singles WHERE word=?) " +
                "AND seq_seq_ID=(SELECT id FROM singles WHERE word=?)",args);
    }

    public int getNumSeqSeq(String prevWord, String word, String nextWord){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT num_seq " +
                "FROM sequencesSequences " +
                "WHERE id=(SELECT id FROM singles WHERE word=?) AND " +
                "seq_ID=(SELECT id FROM singles WHERE word=?) AND " +
                "seq_seq_ID=(SELECT id FROM singles WHERE word=?)";
        Cursor mCursor = db.rawQuery(query,new String[]{prevWord,word, nextWord});
        mCursor.moveToFirst();
        int result = mCursor.getInt(0);
        mCursor.close();
        return result;
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

    public void incrementSingles(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{word};
        ContentValues values = new ContentValues();
        values.put(NUM_SINGLE,getNumSingles(word)+1);
        db.update(SingleWords,values, "id=(SELECT id FROM singles WHERE word=?)",args);
    }

    public void incrementSequence(String prevWord, String word){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{prevWord,word};
        ContentValues values = new ContentValues();
        values.put(NUM_SEQ,getNumSeq(prevWord,word)+1);
        db.update(SequenceWords,values, "id=(SELECT id FROM singles WHERE word=?) " +
                "AND seq_ID=(SELECT id FROM singles WHERE word=?)",args);
    }



    public int getNumSingles(String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT num_singles " +
                       "FROM singles " +
                       "WHERE id = (SELECT id FROM singles WHERE word=?)";
        Cursor mCursor = db.rawQuery(query,new String[]{word});
        mCursor.moveToFirst();
        int result = mCursor.getInt(0);
        mCursor.close();
        return result;
    }

    public int getNumSeq(String prevWord, String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT num_seq " +
                "FROM sequences " +
                "WHERE id=(SELECT id FROM singles WHERE word=?) AND " +
                "seq_ID=(SELECT id FROM singles WHERE word=?)";
        Cursor mCursor = db.rawQuery(query,new String[]{prevWord,word});
        mCursor.moveToFirst();
        int result = mCursor.getInt(0);
        mCursor.close();
        return result;
    }

    public boolean checkSequenceExists(String prevWord, String word){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT count(*) " +
                       "FROM sequences " +
                       "WHERE id=(SELECT id FROM singles WHERE word=?) AND " +
                       "seq_ID=(SELECT id FROM singles WHERE word=?)";
        Cursor mCursor = db.rawQuery(query,new String[]{prevWord,word});
        mCursor.moveToFirst();
        int total = mCursor.getInt(0);
        mCursor.close();
        if(total== 0)
            return false;
        else
            return true;
    }

    public String[] topSequence(String prevWord){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] top = new String[3];
        String query = "SELECT word , SUM(seq.num_seq) as qSum " +
                "FROM singles s " +
                "INNER JOIN sequences seq ON s.id = seq.seq_ID " +
                "WHERE seq.id = (SELECT id FROM singles s WHERE s.word=?) " +
                "GROUP BY seq.seq_ID " +
                "ORDER BY qSum DESC " +
                "LIMIT 3";
        Cursor mCursor = db.rawQuery(query, new String[]{prevWord});
        Log.d("DEBUG4","Cursor Initialized");
        Log.d("DEBUG4","Word used: "+prevWord);
        int i = 0;
        if(mCursor.moveToFirst()){
            Log.d("DEBUG4","Cursor moved to first");
            do{
                Log.d("DEBUG4","Word at "+i+" = "+mCursor.getString(0));
                top[i] = mCursor.getString(0);
                i++;
                if(i == 3){
                    mCursor.close();
                    return top;
                }
            }while (mCursor.moveToNext());
        }
        mCursor.close();
        return top;
    }

    public String[] topIncompleteWord(String incompleteWord){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] top = new String[3];
        String query = "SELECT word, SUM(s.num_singles) as qSum " +
                "FROM singles s " +
                "WHERE word like ? " +
                "GROUP BY s.id " +
                "ORDER BY qSum DESC " +
                "LIMIT 3";
        Cursor mCursor = db.rawQuery(query, new String[]{incompleteWord+"%"});
        int i = 0;
        if(mCursor.moveToFirst()){
            do{
                Log.d("DEBUG4","Word at "+i+" = "+mCursor.getString(0));
                top[i] = mCursor.getString(0);
                i++;
                if(i == 3){
                    mCursor.close();
                    return top;
                }
            }while (mCursor.moveToNext());
        }
        mCursor.close();
        return top;


    }

}