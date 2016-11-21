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
        setContentView(R.layout.activity_remove_word);

        final EditText etRemoveWord = (EditText) findViewById(R.id.etRemoveWord);

        etRemoveWord.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                removeWord(etRemoveWord.getText().toString().toLowerCase());
                if(flag == true){
                    finish();
                }
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
            if(db.removeWord(word)==true) {
                Toast.makeText(this, "The word was removed successfully!", Toast.LENGTH_SHORT).show();
                flag = true;
            }
            else {
                Toast.makeText(this, "The word doesn't exist on the Database!", Toast.LENGTH_SHORT).show();
                flag = false;
            }
        }
        else
            Toast.makeText(this,"The word must be comprised of only letters or only digits!",Toast.LENGTH_SHORT).show();

    }

    public boolean checkWordIsOnlyLetters(String word){
        char[] chars = word.toCharArray();

        for(char c : chars){
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkWordIsOnlyDigits(String word){
        char[] chars = word.toCharArray();

        for(char c : chars){
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
}
