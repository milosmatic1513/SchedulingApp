package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.Preferences;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PublicEventsPage extends AppCompatActivity {
    //firebase
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    //components
    ProgressBar progressBar;
    ImageView negativeIcon,positiveIcon;
    EditText publicEventCodeEditText;
    TextView titlePublicEvent,dateViewPublicEvents,timePublicEvents,repeatingPublicEvents,calendarEntryTypePublicEvents;
    ConstraintLayout timeLayoutPublicEvents;
    LinearLayout taskPublicEvents;
    CalendarEntry currentCalendarEntry;
    Button buttonPublicEvents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get and set Language
        String currentLocale = Preferences.getLanguage(this);
        Preferences.setLocale(this, currentLocale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_events_page);
        //components initialization
        database= FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progressBarPublicEvents);
        progressBar.setVisibility(View.GONE);
        negativeIcon = findViewById(R.id.negativeIcon);
        negativeIcon.setVisibility(View.GONE);
        positiveIcon = findViewById(R.id.positiveIcon);
        positiveIcon.setVisibility(View.GONE);
        titlePublicEvent = findViewById(R.id.titlePublicEvent);
        buttonPublicEvents = findViewById(R.id.buttonPublicEvents);
        publicEventCodeEditText=findViewById(R.id.publicEventCodeEditText);
        dateViewPublicEvents=findViewById(R.id.dateViewPublicEvents);
        timePublicEvents=findViewById(R.id.timePublicEvents);
        repeatingPublicEvents=findViewById(R.id.repeatingPublicEvents);
        calendarEntryTypePublicEvents=findViewById(R.id.calendarEntryTypePublicEvents);
        timeLayoutPublicEvents= findViewById(R.id.timeLayoutPublicEvents);
        taskPublicEvents = findViewById(R.id.taskPublicEvents);
        taskPublicEvents.setVisibility(View.GONE);
        //listeners
        publicEventCodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                negativeIcon.setVisibility(View.GONE);
                positiveIcon.setVisibility(View.GONE);
            }
        });

    }

    public void exit(View view) {
            finish();
    }

    public void checkEvent(View view) {
        taskPublicEvents.setVisibility(View.GONE);
        currentCalendarEntry=null;
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = database.getReference("PublicEvents").child(publicEventCodeEditText.getText().toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    progressBar.setVisibility(View.GONE);
                    negativeIcon.setVisibility(View.GONE);
                    positiveIcon.setVisibility(View.VISIBLE);

                    DatabaseReference calendarEntryRef = database.getReference(snapshot.getValue().toString());
                    calendarEntryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotChild) {
                            CalendarEntry calendarEntry = snapshotChild.getValue(CalendarEntry.class);
                            if(calendarEntry != null) {
                                currentCalendarEntry=calendarEntry;


                                taskPublicEvents.setVisibility(View.VISIBLE);
                                titlePublicEvent.setText(calendarEntry.getTitle());

                                dateViewPublicEvents.setText(getString(R.string.date) + calendarEntry.getDayOfMonth() + "/" + calendarEntry.getMonth() + "/" + calendarEntry.getYear());
                                if (calendarEntry.getType() == calendarEntry.TYPE_EVENT) {
                                    timeLayoutPublicEvents.setVisibility(View.VISIBLE);
                                    timePublicEvents.setText(calendarEntry.calculateHourFrom() + ":" + calendarEntry.calculateMinuteFrom() + "-" + calendarEntry.calculateHourTo() + ":" + calendarEntry.calculateMinuteTo());
                                } else {
                                    timeLayoutPublicEvents.setVisibility(View.GONE);
                                }
                                String[] itemsRepeating = getResources().getStringArray(R.array.spinnerItems);
                                repeatingPublicEvents.setText(getString(R.string.event_repeating) + " " + itemsRepeating[calendarEntry.getRepeating()]);

                                String[] itemsType = getResources().getStringArray(R.array.spinnerItemsType);
                                calendarEntryTypePublicEvents.setText(itemsType[calendarEntry.getType()]);
                                buttonPublicEvents.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        currentCalendarEntry=null;
                                        taskPublicEvents.setVisibility(View.GONE);
                                        DatabaseReference refUser = database.getReference("Users").child(mAuth.getUid()).child("TasksReferences").child(snapshot.getKey());
                                        refUser.setValue(snapshot.getValue());
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.publicEventsPageLayout),"Item "+calendarEntry.getTitle() +" added ",Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                });


                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                currentCalendarEntry=null;
                                taskPublicEvents.setVisibility(View.GONE);
                                ref.removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch (NullPointerException e){
                    progressBar.setVisibility(View.GONE);
                    negativeIcon.setVisibility(View.VISIBLE);
                    positiveIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                negativeIcon.setVisibility(View.VISIBLE);
                positiveIcon.setVisibility(View.GONE);
            }
        });

    };


}