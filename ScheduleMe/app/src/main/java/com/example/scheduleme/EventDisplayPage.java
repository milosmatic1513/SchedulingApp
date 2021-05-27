package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.Utilities.ImageUtilities;

public class EventDisplayPage extends AppCompatActivity {

    static int EDIT_ACTIVITY_REQUEST=2;
    TextView titleTextView;
    TextView timeTextView;
    TextView descriptionEditText;
    TextView repeatingTextView;
    TextView dateTextView;

    CalendarEntry calendarEntry;
    LinearLayout descriptionBox;
    LinearLayout imageBox;
    LinearLayout locationBox;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display_page);

        //component initialization
        titleTextView=findViewById(R.id.titleBottomView);
        timeTextView=findViewById(R.id.timeBottomView);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        repeatingTextView = findViewById(R.id.repeatingBottomView);
        dateTextView = findViewById(R.id.dateViewBottomView);
        descriptionBox=findViewById(R.id.descriptionBoxBottomView);
        imageBox=findViewById(R.id.imageBoxBottomView);
        imageView=findViewById(R.id.imageBottomView);
        locationBox=findViewById(R.id.locationBoxBottomView);
        locationBox.setVisibility(View.GONE);

        calendarEntry =(CalendarEntry) getIntent().getSerializableExtra("CalendarEntry");
        if (calendarEntry==null)
        {
            Toast.makeText(getApplicationContext(),"There was an error loading the task", Toast.LENGTH_SHORT);
        }
        else
        {
            titleTextView.setText(calendarEntry.getTitle());
            timeTextView.setText("Date : "+calendarEntry.calculateHourFrom()+":"+calendarEntry.calculateMinuteFrom()+"-"+calendarEntry.calculateHourTo()+":"+calendarEntry.calculateMinuteTo());
            dateTextView.setText(calendarEntry.getDayOfMonth()+"/"+calendarEntry.getMonth()+"/"+calendarEntry.getYear());

            if(calendarEntry.getDescription().length()==0)
            {
                descriptionBox.setVisibility(View.GONE);
            }
            else
            {
                descriptionEditText.setText(calendarEntry.getDescription());
            }
            if(calendarEntry.getBase64Image().length()==0)
            {
                imageBox.setVisibility(View.GONE);
            }
            else
            {
                Bitmap bitmap = ImageUtilities.base64ToBitmap(calendarEntry.getBase64Image());
                if(bitmap!=null) {
                    imageView.setImageBitmap(bitmap);
                }
                else
                {
                    //set error image
                    imageBox.setVisibility(View.GONE);
                }
            }

            String [] items =getResources().getStringArray(R.array.spinnerItems);
            repeatingTextView.setText("Repeating : "+items[calendarEntry.getRepeating()]);
        }
    }

    public void done(View view) {
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
                timeTextView.setText(calendarEntry.calculateHourFrom()+":"+calendarEntry.calculateMinuteFrom()+"-"+calendarEntry.calculateHourTo()+":"+calendarEntry.calculateMinuteTo());
                if(calendarEntry.getDescription().length()==0)
                {
                    descriptionBox.setVisibility(View.GONE);
                }
                else
                {
                    descriptionEditText.setText(calendarEntry.getDescription());
                }

                if(calendarEntry.getBase64Image().length()==0)
                {
                    imageBox.setVisibility(View.GONE);
                }
                else
                {
                    Bitmap bitmap = ImageUtilities.base64ToBitmap(calendarEntry.getBase64Image());
                    if(bitmap!=null) {
                        imageBox.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bitmap);
                    }
                    else
                    {
                        //set error image
                        imageBox.setVisibility(View.GONE);
                    }
                }
                String [] items =getResources().getStringArray(R.array.spinnerItems);
                repeatingTextView.setText("Repeating : "+items[calendarEntry.getRepeating()]);
                this.calendarEntry=calendarEntry;
            }
        }
    }
}