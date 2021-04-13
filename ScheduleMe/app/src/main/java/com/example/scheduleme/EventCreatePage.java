package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.CalendarEntryBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventCreatePage extends AppCompatActivity {


    //firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    TimePickerDialog pickerTime;
    DatePickerDialog pickerDate;
    EditText editTextTitle;
    EditText editTextDescription;
    EditText editTextFrom;
    EditText editTextTo;
    EditText editTextDate;
    Spinner spinnerRepeat;
    Switch switchImportant;

    Button createButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create_page);

        //Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        //component initialization
        editTextTitle =(EditText) findViewById(R.id.editTextTitle);
        editTextDescription =(EditText) findViewById(R.id.editTextDescription);
        editTextFrom=(EditText) findViewById(R.id.editTextTimeFrom);
        editTextFrom.setInputType(InputType.TYPE_NULL);
        editTextTo=(EditText) findViewById(R.id.editTextTimeTo);
        editTextTo.setInputType(InputType.TYPE_NULL);
        editTextDate=(EditText)findViewById(R.id.editTextDate);
        editTextDate.setInputType(InputType.TYPE_NULL);
        switchImportant=(Switch)findViewById(R.id.switchImportant);
        createButton = (Button)findViewById(R.id.createButton);
        spinnerRepeat = (Spinner)findViewById(R.id.spinner);

        //listeners
        editTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                pickerTime = new TimePickerDialog(EventCreatePage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                if(sHour<10 && sMinute<10)
                                { editTextFrom.setText("0"+sHour + ":0" + sMinute);}
                                else if(sMinute<10)
                                { editTextFrom.setText(sHour + ":0" + sMinute);}
                                else if(sHour<10)
                                { editTextFrom.setText("0"+sHour + ":" + sMinute);}
                                else
                                { editTextFrom.setText(sHour + ":" + sMinute);}
                            }
                        }, hour, minutes, true);
                pickerTime.show();
            }
        });
        editTextTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                pickerTime = new TimePickerDialog(EventCreatePage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                if(sHour<10 && sMinute<10)
                                { editTextTo.setText("0"+sHour + ":0" + sMinute);}
                                else if(sMinute<10)
                                { editTextTo.setText(sHour + ":0" + sMinute);}
                                else if(sHour<10)
                                { editTextTo.setText("0"+sHour + ":" + sMinute);}
                                else
                                { editTextTo.setText(sHour + ":" + sMinute);}
                            }
                        }, hour, minutes, true);
                pickerTime.show();
            }
        });
        editTextDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                final Calendar cldr = Calendar.getInstance();
                int year = cldr.get(Calendar.YEAR);
                int month = cldr.get(Calendar.MONTH);
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                // Date picker dialog
                pickerDate = new DatePickerDialog(EventCreatePage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = dayOfMonth+"/"+(month+1)+"/"+year+" 00:00";
                        //add+1 to offset months
                        editTextDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                        try {
                            SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            Date dateParsed = sdf.parse(date);
                            long millis = dateParsed.getTime();
                            editTextDate.setTag(Long.toString(millis));
                            Log.e("date",Long.toString(millis));
                        } catch (ParseException e) {
                            Log.e("tag",e.getLocalizedMessage().toString());
                        }

                    }
                },year,month,day);
            pickerDate.show();
            }

        });

    }

    public void checkCreate(View view) {


        //check if any of the fields are empty
        if (
                editTextTitle.getText().toString().trim().length()==0 ||
                editTextDescription.getText().toString().trim().length()==0  ||
                editTextFrom.getText().toString().trim().length()==0  ||
                editTextTo.getText().toString().trim().length()==0||
                editTextDate.getText().toString().trim().length()==0
        )
        {
            //inform the user that all the fields are required
            Toast.makeText(this,getString(R.string.toast_fill_warning),Toast.LENGTH_LONG).show();
        }
        else
        {

            CalendarEntry calendarEntry= new CalendarEntryBuilder()
                    .setTitle(editTextTitle.getText().toString())
                    .setDescription(editTextDescription.getText().toString())
                    .setTimeStart(editTextFrom.getText().toString().replace(":",""))
                    .setTimeEnd(editTextTo.getText().toString().replace(":",""))
                    .setImportant(switchImportant.isChecked())
                    .setDate(Long.parseLong(editTextDate.getTag().toString()))
                    .setRepeating(spinnerRepeat.getSelectedItemPosition())
                    .build();
            DatabaseReference myRef = database.getReference("Users/"+currentUser.getUid()+"/Tasks/").push();
            myRef.setValue(calendarEntry);
            Intent intent = new Intent(getApplicationContext(),MainPage.class);
            startActivity(intent);

        }
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            createButton.setEnabled(true);
        }
    }

    }