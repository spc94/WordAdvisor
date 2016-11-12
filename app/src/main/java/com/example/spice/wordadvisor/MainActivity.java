package com.example.spice.wordadvisor;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.Objects;

import static junit.framework.Assert.assertNotNull;


public class MainActivity extends AppCompatActivity {

    private static final int CURRENT_DATABASE_VERSION = 42;
    private SQLiteDatabase mDatabase;
    private File mDatabaseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG","Starting debug");
        DataBaseHelper db = new DataBaseHelper(this);
        String prevWord = "";
        String word1 = "The";
        String word2 = "Rain";
        String word3 = "Rain";
        Log.d("TEST","Adding Word1 to Singles");
        db.addWordToSingles(word1);
        if(prevWord.equals("")==true){
            prevWord = word1;
        }
        Log.d("TEST","Adding Word2 to Singles");
        db.addWordToSingles(word2);
        Log.d("TEST","Adding Word2 to Seq");
        db.addWordToSequence(prevWord,word2);
        Log.d("TEST","Adding Word3 to Singles");
        db.addWordToSingles(word3);
        Log.d("TEST","Adding Word3 to Seq");
        db.addWordToSequence(prevWord,word3);





    }
}
