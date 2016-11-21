package com.example.spice.wordadvisor;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static junit.framework.Assert.assertNotNull;


public class MainActivity extends AppCompatActivity {
    private static MainActivity ins;
    private SQLiteDatabase sqlDB;
    private DataBaseHelper dbHelper;
    private DictionaryDatabaseHelper ddbHelper;
    MainActivity getInstance(){
        return ins;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;
        //this.deleteDatabase("WordAdv");
        ddbHelper = new DictionaryDatabaseHelper(this);
        dbHelper = new DataBaseHelper(this);
        //sqlDB.execSQL("PRAGMA foreign_keys=ON");
        try {
            ddbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG","Attempt to check word psycho");
        Log.d("DEBUG",""+ddbHelper.checkWordExists("psycho"));

    }

    public void onClickAddWord(View view){
        Intent intent = new Intent(getInstance(),AddWord.class);
        startActivity(intent);
    }

    public void onClickImport(View view){
        Intent intent = new Intent(getInstance(),ImportText.class);
        startActivity(intent);
    }

    public void onClickNewText(View view){
        Intent intent = new Intent(getInstance(),NewText.class);
        startActivity(intent);
    }

    public void onClickRemove(View view){
        Intent intent = new Intent(getInstance(),RemoveWord.class);
        startActivity(intent);
    }

}
