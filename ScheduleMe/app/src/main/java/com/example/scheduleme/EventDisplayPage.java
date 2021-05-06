package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;

import java.io.Serializable;
import java.util.List;

public class EventDisplayPage extends AppCompatActivity {

    static int EDIT_ACTIVITY_REQUEST=2;
    TextView titleTextView;
    TextView timeTextView;
    TextView descriptionEditText;
    TextView repeatingTextView;

    CalendarEntry calendarEntry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display_page);

        //component initialization
        titleTextView=findViewById(R.id.textViewTitleDisplay);
        timeTextView=findViewById(R.id.textViewTimeDisplay);
        descriptionEditText=findViewById(R.id.editTextDescriptionDisplay);
        repeatingTextView = findViewById(R.id.repeatingTextView);

        calendarEntry =(CalendarEntry) getIntent().getSerializableExtra("CalendarEntry");
        if (calendarEntry==null)
        {
            Toast.makeText(getApplicationContext(),"There was an error loading the task", Toast.LENGTH_SHORT);
        }
        else
        {
            titleTextView.setText(calendarEntry.getTitle());
            timeTextView.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4)+"-"+calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));
            descriptionEditText.setText(calendarEntry.getDescription());
            String [] items =getResources().getStringArray(R.array.spinnerItems);
            repeatingTextView.setText("Repeating : "+items[calendarEntry.getRepeating()]);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    public void edit(View view) {
        Intent intent = new Intent(this,EventCreatePage.class);
        intent.putExtra("task",calendarEntry);
        startActivityForResult(intent ,EDIT_ACTIVITY_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==EDIT_ACTIVITY_REQUEST)
        {
            CalendarEntry calendarEntry=null;
            try {
                 calendarEntry=(CalendarEntry) data.getSerializableExtra("calendarEntry");
            }catch (java.lang.NullPointerException e ){
                Toast.makeText(getApplicationContext(),"Editing Canceled",Toast.LENGTH_SHORT);
            }

            if (calendarEntry==null)
            {
                Toast.makeText(getApplicationContext(),"There was an error loading the task", Toast.LENGTH_SHORT);
            }
            else
            {
                titleTextView.setText(calendarEntry.getTitle());
                timeTextView.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4)+"-"+calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));
                descriptionEditText.setText(calendarEntry.getDescription());
                String [] items =getResources().getStringArray(R.array.spinnerItems);
                repeatingTextView.setText("Repeating : "+items[calendarEntry.getRepeating()]);
                this.calendarEntry=calendarEntry;
            }
        }
    }
}