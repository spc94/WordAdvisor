package com.example.spice.wordadvisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewText extends AppCompatActivity {


    private int atSpecialChar = 0;
    private String currentWord= "";
    private String prevWord = "";
    private String prevPrevWord = "";
    private int sizeBefore = 0;
    private static NewText ins;
    private static String currentText = "";
    Button bt1;
    Button bt2;;
    Button bt3;
    EditText et;
    static NewText getIns(){return ins;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_text);
        ins = this;
        bt1  = (Button) findViewById(R.id.buttonFirstPlace);
        bt2  = (Button) findViewById(R.id.buttonSecondPlace);
        bt3  = (Button) findViewById(R.id.buttonThirdPlace);
        et = (EditText) findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("DEBUG3","Size of text: "+charSequence.length());
                sizeBefore = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("DEBUG3","Size of text: "+charSequence.length());
                currentText = charSequence.toString();
                if(sizeBefore < charSequence.length()) {
                    char c;
                    String[] topWords;
                    String[] topWordsAux;
                    DataBaseHelper db = new DataBaseHelper(getIns());
                    int pos = charSequence.length() - 1;
                    if (pos >= 0)
                        c = charSequence.charAt(pos);
                    else
                        return;
                    Log.d("DEBUG4", "Current char: " + c);
                    if (!Character.isLetterOrDigit(c)) {
                        Log.d("DEBUG3", "Special char detected");

                        prevPrevWord = prevWord;
                        prevWord = currentWord;
                        Log.d("DEBUG3", "Previous Previous Word: " + prevPrevWord);
                        currentWord = "";
                        // Get Top Sequence from DB using prevWord
                        if (prevPrevWord.equals("") && prevWord.equals("")) {
                            topWords = db.topIncompleteWord("");
                        }
                        else if(prevPrevWord.equals("")){
                            topWords = db.topSequence(prevWord.toLowerCase());
                        }
                        else{
                            topWords = db.topSequenceSequence(prevPrevWord.toLowerCase(),
                                    prevWord.toLowerCase());
                            if(topWords[2]==null) { //If no sequence-sequence results

                                topWordsAux = db.topSequence(prevWord.toLowerCase());
                                Log.d("DEBUG","TOP WORDS AUX 2: "+ topWordsAux[2]);
                                Log.d("DEBUG","TOP WORDS AUX 1: "+ topWordsAux[1]);
                                Log.d("DEBUG","TOP WORDS AUX 0: "+ topWordsAux[0]);
                                Log.d("DEBUG","TOP WORDS 0: "+ topWords[0]);
                                Log.d("DEBUG","TOP WORDS 1: "+ topWords[1]);
                                if(topWordsAux[2]!=null) {
                                    if (!topWordsAux[2].equals(topWords[0]) ||
                                            !topWordsAux[2].equals(topWords[1])) {//If the word from sequence doesn't already belong to a sequence-sequence
                                        topWords[2] = topWordsAux[2];
                                    }
                                }
                            }
                            if(topWords[1]==null){//If only 1 sequence-sequence result

                                topWordsAux = db.topSequence(prevWord.toLowerCase());
                                if(topWordsAux[1]!=null){
                                    if (!topWordsAux[1].equals(topWords[0]))
                                        topWords[1] = topWordsAux[1];
                                }
                            }
                            if(topWords[0]==null)//If no sequence-sequence result
                                topWords = db.topSequence(prevWord.toLowerCase());

                        }
                    } else {
                        currentWord = currentWord + c;
                        Log.d("DEBUG3", "Alphanumeric detected");
                        Log.d("DEBUG3", "Current word: " + currentWord);

                        topWords = db.topIncompleteWord(currentWord);


                        /* TODO:
                        * Different way of detecting previous word. For example looking until a space is found and reverting the chars
                        * Suggest in the middle of the word, when deleting
                        * */
                    }

                        if(topWords[0]!=null)
                            bt1.setText(topWords[0].toString());
                        else
                            bt1.setText("");
                        if(topWords[1]!=null)
                            bt2.setText(topWords[1].toString());
                        else
                            bt2.setText("");
                        if(topWords[2]!=null)
                            bt3.setText(topWords[2].toString());
                        else
                            bt3.setText("");



                }
                else
                    return;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void clickButton1(View view){
        if(bt1.getText().toString().length()>0)
            replaceWord(bt1.getText().toString());
    }

    public void clickButton2(View view){
        if(bt2.getText().toString().length()>0)
            replaceWord(bt2.getText().toString());
    }

    public void clickButton3(View view){
        if(bt3.getText().toString().length()>0)
            replaceWord(bt3.getText().toString());
    }

    public void replaceWord(String wordToReplace){
        String text = et.getText().toString();
        int pos;
        char c;
        if(text.length()>0)
            pos = text.length()-1;
        else
            pos = 0;
        try{
            c = text.charAt(pos);
        }catch(Exception e){return;};
         //check if crashes on empty editText
        Log.d("DEBUG5","Inside click");
        while(Character.isLetterOrDigit(c)){
            Log.d("DEBUG5","Char in Clicky: "+c);
            text = text.substring(0,pos);
            pos--;
            if(pos<0)
                break;
            c = text.charAt(pos);
        }
        if(isFirstWord(text))
            text = text + String.valueOf(wordToReplace.charAt(0)).toUpperCase() +
                    wordToReplace.substring(1,wordToReplace.length());
        else if(isPunctuation(text))
            text = text + " " + String.valueOf(wordToReplace.charAt(0)).toUpperCase() +
                    wordToReplace.substring(1,wordToReplace.length());
        else if(isComma(text))
            text = text + " " + wordToReplace;
        else
            text = text + wordToReplace;
        currentWord = wordToReplace;
        et.setText(text+" ");
        et.setSelection(et.getText().length());
        currentWord = wordToReplace;
    }

    public boolean isFirstWord(String text){
        int pos;
        if(text.length()>0)
            return false;
        return true;
    }

    public boolean isPunctuation(String text){
        String punctuation = ".!?";
        if(punctuation.contains(""+text.charAt(text.length()-1)))
            return true;
        return false;
    }

    public boolean isComma(String text){
        String punctuation = ",";
        if(punctuation.contains(""+text.charAt(text.length()-1)))
            return true;
        return false;
    }



}
