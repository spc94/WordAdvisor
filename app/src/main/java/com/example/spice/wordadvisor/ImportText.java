package com.example.spice.wordadvisor;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ImportText extends AppCompatActivity {

    private static ImportText ins;
    ProgressDialog progressDialog;
    private static ImportText getIns(){return ins;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_text);
        ins = this;

    }

    public void onClickImportFile(View view) throws IOException {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        try {
            startActivityForResult(fileIntent, 1);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try{
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("A ler o ficheiro...");
                        progressDialog.show();
                        new ReadTask(data){
                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                progressDialog.dismiss();
                                if (o instanceof String){
                                    Toast.makeText(getIns(), "Unexpected error: " + o, Toast.LENGTH_LONG).show();
                                }
                                Log.d("DEBUG","Added Words to DB");
                                Toast.makeText(getIns(),"Words successfully imported!", Toast.LENGTH_SHORT);
                            }
                        }.execute();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
        }
    }

    public void sendWordToDB(String word){

        if(checkWordIsOnlyLetters(word)==true ||
                checkWordIsOnlyDigits(word)==true){
            DataBaseHelper db = new DataBaseHelper(this);
            db.addWordToSingles(word);
            db.close();
        }

    }

    public void sendWordSequenceToDB(String prevWord, String word){

        if(checkWordIsOnlyLetters(word)==true ||
                checkWordIsOnlyDigits(word)==true){
            DataBaseHelper db = new DataBaseHelper(this);
            db.addWordToSequence(prevWord,word);
            //db.addWordToSingles(word);
            db.close();
        }

    }

    public void sendWordSequenceSequenceToDB(String prevWord, String word, String nextWord){

        if(checkWordIsOnlyLetters(nextWord)==true ||
                checkWordIsOnlyDigits(nextWord)==true){
            DataBaseHelper db = new DataBaseHelper(this);
            db.addWordToSequenceSequence(prevWord,word,nextWord);
            //db.addWordToSingles(nextWord);
            db.close();
        }

    }



    public boolean checkWordIsOnlyLetters(String word){
        char[] chars = word.toCharArray();
        if(chars.length==0)
            return false;
        for(char c : chars){
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkWordIsOnlyDigits(String word){
        char[] chars = word.toCharArray();
        if(chars.length==0)
            return false;
        for(char c : chars){
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    public static String getPath(final Context context, final Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                String storageDefinition;

                if("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                } else {
                    if(Environment.isExternalStorageRemovable()){
                        storageDefinition = "EXTERNAL_STORAGE";

                    } else{
                        storageDefinition = "SECONDARY_STORAGE";
                    }

                    return System.getenv(storageDefinition) + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public class ReadTask extends AsyncTask {
        Intent data;

        public ReadTask(Intent data) {
            this.data = data;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                File file = new File(getPath(getIns(),data.getData()));
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                StringBuilder textBuilder = new StringBuilder();
                String line;
                try {
                    while((line = br.readLine())!=null){
                        textBuilder.append(line);
                        textBuilder.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] wordsInFile = textBuilder.toString().split("\\W+");
                int i = 0;
                String prevPrevWord = "";
                String prevWord     = "";
                Log.d("DEBUG","Adding words to DB...");
                for(String word : wordsInFile){
                    i++;
                    if(prevPrevWord.equals("")==true) {
                        Log.d("DEBUG","Word pp "+i+": "+word);
                        sendWordToDB(word);
                        prevPrevWord = word;
                    }
                    else if(prevWord.equals("")==true){
                        Log.d("DEBUG","Word p "+i+": "+word);
                        sendWordSequenceToDB(prevPrevWord, word);
                        prevWord = word;
                    }
                    else{
                        Log.d("DEBUG","Word "+i+": "+word);
                        sendWordSequenceToDB(prevWord,word);
                        sendWordSequenceSequenceToDB(prevPrevWord,prevWord,word);
                        prevPrevWord = prevWord;
                        prevWord = word;
                    }
                }
                Log.d("DEBUG","Number of words: "+i);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return 0;
        }
    }
}