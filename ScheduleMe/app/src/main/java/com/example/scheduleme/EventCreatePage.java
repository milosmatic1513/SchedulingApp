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

    CalendarEntry calendarEntry;
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

        calendarEntry = (CalendarEntry) getIntent().getSerializableExtra("task");
        if(calendarEntry !=null){
            editTextTitle.setText(calendarEntry.getTitle());
            editTextDescription.setText(calendarEntry.getDescription());
            switchImportant.setChecked(calendarEntry.isImportant());
            spinnerRepeat.setSelection(calendarEntry.getRepeating());
            editTextDate.setText(calendarEntry.getDayOfMonth()+"/"+calendarEntry.getMonthNumeric()+"/"+calendarEntry.getYear());
            editTextDate.setTag(Long.toString(calendarEntry.getDate()));
            editTextFrom.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4));
            editTextTo.setText(calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));
        }

        //listeners
        editTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(calendarEntry!=null) {
                    try {
                        hour = Integer.parseInt(calendarEntry.getTimeStart().substring(0,2));
                        minutes = Integer.parseInt(calendarEntry.getTimeStart().substring(2,4));
                    }catch (Exception e) {
                        Toast.makeText(EventCreatePage.this,"Error While Parsing Time",Toast.LENGTH_SHORT);
                    }
                }
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
                if(calendarEntry!=null) {
                    try {
                        hour = Integer.parseInt(calendarEntry.getTimeEnd().substring(0,2));
                        minutes = Integer.parseInt(calendarEntry.getTimeEnd().substring(2,4));
                    }catch (Exception e) {
                        Toast.makeText(EventCreatePage.this,"Error While Parsing Time",Toast.LENGTH_SHORT);
                    }
                }
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
                if(calendarEntry!=null) {
                    try {
                        year = Integer.parseInt(calendarEntry.getYear());
                        month = Integer.parseInt(calendarEntry.getMonthNumeric());
                        day = Integer.parseInt(calendarEntry.getDayOfMonth());
                    }catch (Exception e) {
                        Toast.makeText(EventCreatePage.this,"Error While Parsing Date",Toast.LENGTH_SHORT);
                    }
                }
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
            CalendarEntry newCalendarEntry= new CalendarEntryBuilder()
                    .setTitle(editTextTitle.getText().toString())
                    .setDescription(editTextDescription.getText().toString())
                    .setTimeStart(editTextFrom.getText().toString().replace(":",""))
                    .setTimeEnd(editTextTo.getText().toString().replace(":",""))
                    .setImportant(switchImportant.isChecked())
                    .setDate(Long.parseLong(editTextDate.getTag().toString()))
                    .setRepeating(spinnerRepeat.getSelectedItemPosition())
                    .build();

            if(calendarEntry==null) {
                DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/").push();
                myRef.setValue(newCalendarEntry);
                finish();
            }
            else
            {
                if(calendarEntry.getDatabaseID().trim().length()!=0){
                    DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/"+calendarEntry.getDatabaseID());
                    myRef.setValue(newCalendarEntry);
                    Intent intent=new Intent();
                    newCalendarEntry.setDatabaseID(calendarEntry.getDatabaseID());
                    intent.putExtra("calendarEntry",newCalendarEntry);
                    setResult(2,intent);
                    finish();
                }
                Toast.makeText(EventCreatePage.this,"There was an error ",Toast.LENGTH_SHORT);

            }


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