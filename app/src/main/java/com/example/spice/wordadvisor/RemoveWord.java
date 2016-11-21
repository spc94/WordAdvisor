package com.example.spice.wordadvisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RemoveWord extends AppCompatActivity {

    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        final EditText etRemoveWord = (EditText) findViewById(R.id.etWord);

        etRemoveWord.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                removeWord(etRemoveWord.getText().toString().toLowerCase());
                if(flag == true)
                    finish();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void removeWord(String word){

        if(checkWordIsOnlyLetters(word)==true ||
                checkWordIsOnlyDigits(word)==true){
            DataBaseHelper db = new DataBaseHelper(this);
            db.removeWord(word);
            flag = true;
        }

    }

    public boolean checkWordIsOnlyLetters(String word){
        char[] chars = word.toCharArray();

        for(char c : chars){
            if(!Character.isLetter(c)) {
                Toast.makeText(this,"Word must be only letters or only digits",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public boolean checkWordIsOnlyDigits(String word){
        char[] chars = word.toCharArray();

        for(char c : chars){
            if(!Character.isDigit(c)){
                Toast.makeText(this,"Word must be only letters or only digits",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
