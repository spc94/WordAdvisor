package com.example.spice.wordadvisor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WordAdv";
    private static final int DATABASE_VERSION = 1;

    public static final String SingleWords = "singles";
    public static final String SequenceWords = "sequences";

    public static final String ID_COLUMN = "id";
    public static final String NUM_SEQ = "num_seq";
    public static final String WORD_COLUMN = "word";
    public static final String SEQ_ID_COLUMN = "seq_ID";

    public static final String CREATE_SINGLES_TABLE = "CREATE TABLE "
            + SingleWords + "(" + ID_COLUMN + " INTEGER PRIMARY KEY, "
            + WORD_COLUMN + " TEXT, " + ")";

    public static final String CREATE_SEQUENCE_TABLE = "CREATE TABLE "
            + SequenceWords + "(" + ID_COLUMN + " INT, "
            + "FOREIGN KEY(" + ID_COLUMN + ") REFERENCES "
            + SingleWords + "(id) " + SEQ_ID_COLUMN + " INT, "
            + "FOREIGN KEY(" + SEQ_ID_COLUMN + ") REFERENCES "
            + SingleWords + "(id) " + NUM_SEQ + "INT, " + ")";

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

    }
}