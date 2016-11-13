package com.example.spice.wordadvisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class NewText extends AppCompatActivity {


    private int atSpecialChar = 0;
    private String currentWord= "";
    private String prevWord = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_text);

        EditText et = (EditText) findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0)
                    Log.d("DEBUG3","Nothing yet!");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("DEBUG3","Sequence: "+ charSequence.toString()+"i: "+i+" i1: "+i2 + " i2: "+i2);
                char c;

                c = charSequence.charAt(0);
                if(!Character.isLetterOrDigit(c)) {
                    Log.d("DEBUG3", "Special char detected");
                    atSpecialChar=1;

                    prevWord = currentWord;
                    currentWord = "";
                    // Get Top Sequence from DB using prevWord
                }
                else{
                    currentWord = currentWord + c;
                    atSpecialChar=0;
                    //if(!prevWord.equals(""))

                        //Get Top Sequence for prevWord + currentWord%
                        //If no results, suggest single word for currentWord%

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
