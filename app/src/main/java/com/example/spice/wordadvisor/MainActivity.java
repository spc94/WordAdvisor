package com.example.spice.wordadvisor;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.Objects;

import static junit.framework.Assert.assertNotNull;


public class MainActivity extends AppCompatActivity {
    private static MainActivity ins;
    MainActivity getInstance(){
        return ins;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;
        Log.d("DEBUG","Starting debug");

    }

    public void onClickAddWord(View view){
        Intent intent = new Intent(getInstance(),AddWord.class);
        startActivity(intent);
    }


}
