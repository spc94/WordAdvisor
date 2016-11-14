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
    private int sizeBefore = 0;
    private static NewText ins;
    static NewText getIns(){return ins;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_text);
        ins = this;

        EditText et = (EditText) findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("DEBUG3","Size of text: "+charSequence.length());
                sizeBefore = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("DEBUG3","Size of text: "+charSequence.length());
                if(sizeBefore < charSequence.length()) {
                    char c;
                    DataBaseHelper db = new DataBaseHelper(getIns());
                    int pos = charSequence.length() - 1;
                    if (pos >= 0)
                        c = charSequence.charAt(pos);
                    else
                        return;
                    Log.d("DEBUG4", "Current char: " + c);
                    if (!Character.isLetterOrDigit(c)) {
                        Log.d("DEBUG3", "Special char detected");

                        prevWord = currentWord;
                        Log.d("DEBUG3", "Previous Word: " + prevWord);
                        currentWord = "";
                        // Get Top Sequence from DB using prevWord
                        if (prevWord.equals("")) {
                            db.topIncompleteWord("");
                        } else {
                            db.topSequence(prevWord.toLowerCase());
                        }
                    } else {
                        currentWord = currentWord + c;
                        Log.d("DEBUG3", "Alphanumeric detected");
                        Log.d("DEBUG3", "Current word: " + currentWord);

                        db.topIncompleteWord(currentWord);


                        /* TODO:
                        * Different way of detecting previous word. For example looking until a space is found and reverting the chars
                        * Suggest in the middle of the word, when deleting
                        * */
                    }
                }
                else
                    return;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
