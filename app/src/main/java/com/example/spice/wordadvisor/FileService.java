/*package com.example.spice.wordadvisor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.example.spice.wordadvisor.ImportText.getPath;

public class FileService extends Service {
    public FileService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handleData(intent);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void handleData(Intent data){
        File file = new File(getPath(ImportText.getIns(),data.getData()));
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
        //Log.d("DEBUG",textBuilder.toString());
        String[] wordsInFile = textBuilder.toString().split("\\W+");
        int i = 0;
        String prevWord = "";
        DataBaseHelper db = new DataBaseHelper(ImportText.getIns());
        Log.d("DEBUG","Adding words to DB...");
        Toast.makeText(ImportText.getIns(),"Adding new words to the DB...", Toast.LENGTH_SHORT);
        for(String word : wordsInFile){
            Log.d("DEBUG6","Current word: "+word);
            i++;
            if(prevWord.equals("")==true) {
                sendWordToDB(word);
                prevWord = word;
            }
            else {
                sendWordSequenceToDB(prevWord, word);
                prevWord = word;
            }
        }
        Log.d("DEBUG","Added Words to DB");
        Toast.makeText(ImportText.getIns(),"Words successfully imported!", Toast.LENGTH_SHORT);
    }*/

