package com.example.spice.wordadvisor;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import static junit.framework.Assert.assertNotNull;


public class MainActivity extends AppCompatActivity {

    private static final int CURRENT_DATABASE_VERSION = 42;
    private SQLiteDatabase mDatabase;
    private File mDatabaseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dbDir = this.getDir("", this.MODE_PRIVATE);
        mDatabaseFile = new File(dbDir, "database_test.db");

        if (mDatabaseFile.exists()) {
            mDatabaseFile.delete();
        }
        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFile.getPath(), null);
        assertNotNull(mDatabase);
        mDatabase.setVersion(CURRENT_DATABASE_VERSION);

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.addWordToSingles("Cão");
        dbAdapter.addWordToSingles("Gato");
        Log.d("DEBUG",""+dbAdapter.wordExistsOnSingles("Cão"));
        Log.d("DEBUG",""+dbAdapter.wordExistsOnSingles("João"));
        Log.d("DEBUG",""+dbAdapter.getIDFromWord("Cão"));
        Log.d("DEBUG",""+dbAdapter.getIDFromWord("Gato"));


    }
}
