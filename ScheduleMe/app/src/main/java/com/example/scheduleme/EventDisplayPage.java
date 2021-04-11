package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;

import java.io.Serializable;

public class EventDisplayPage extends AppCompatActivity {

    TextView titleTextView;
    TextView timeTextView;
    EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display_page);

        //component initialization
        titleTextView=findViewById(R.id.textViewTitleDisplay);
        timeTextView=findViewById(R.id.textViewTimeDisplay);
        descriptionEditText=findViewById(R.id.editTextDescriptionDisplay);

        CalendarEntry calendarEntry =(CalendarEntry) getIntent().getSerializableExtra("CalendarEntry");
        if (calendarEntry==null)
        {
            Toast.makeText(getApplicationContext(),"There was an error loading the task", Toast.LENGTH_SHORT);
        }
        else
        {
            titleTextView.setText(calendarEntry.getTitle());
            timeTextView.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4)+"-"+calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));
            descriptionEditText.setText(calendarEntry.getDescription());
        }
    }
}