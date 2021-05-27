package com.example.scheduleme.Utilities;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.CalendarEntryBuilder;

import java.util.Random;

public class DatabaseFaker {
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    String[] wordList =new String[] {"spiffy","trees","engine","abhorrent","snobbish","tent"};
    String[] timeList =new String[] {"0230","0540","1150","1830","2025","2240"};
    Random random;
    public DatabaseFaker(FirebaseUser currentUser, FirebaseDatabase database, DatabaseReference reference){
        this.currentUser = currentUser;
        this.database =database;
        this.reference=reference;
        random=new Random();
    }
    public void generateData()
    {

       /* reference.push().setValue(new CalendarEntryBuilder()
                .setTitle(wordList[random.nextInt(wordList.length)]+" "+wordList[random.nextInt(wordList.length)])
                .setTimeStart(timeList[random.nextInt(timeList.length)])
                .setTimeEnd(timeList[random.nextInt(timeList.length)])
                .setDate(1618866000000l)
                .build()
        );*/
    }
}
