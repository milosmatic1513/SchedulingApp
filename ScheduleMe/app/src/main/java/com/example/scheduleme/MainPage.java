package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainPage extends AppCompatActivity {


    //Database
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    //Private variables
    private boolean authenticated;
    private static int VIEW_ACTIVITY_REQUEST=1;
    private Date currentDate;
    //View Components
    TextView authenticatedTag;
    TextView textViewDateDay;
    TextView textViewDateMonth;
    TextView textViewDateYear;
    ImageView imageViewCalendar;
    RecyclerView calendarEntryRecyclerView;
    //Dialogs
    DatePickerDialog pickerDate;
    //Calendar View Components
    List<CalendarEntry> calendarEntries;
    CalendarEntitiesAdapter adapter;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //components Initialization
        authenticatedTag = findViewById(R.id.authenticatedTag);
        textViewDateDay  = findViewById(R.id.textViewDateDay);
        textViewDateMonth  = findViewById(R.id.textViewDateMonth);
        textViewDateYear  = findViewById(R.id.textViewDateYear);
        imageViewCalendar=findViewById(R.id.imageViewCalendar);
        calendarEntryRecyclerView = (RecyclerView) findViewById(R.id.calendarEntryRecyclerView);

        //Check if the user Is authenticated;
        Intent intent = getIntent();
        authenticated=intent.getBooleanExtra("Authenticated",false);
        if(!authenticated)
        {
            authenticatedTag.setVisibility(View.GONE);
        }


        //Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        database =FirebaseDatabase.getInstance();

        //set Current selected Date
        currentDate = new Date(System.currentTimeMillis());

        //set Listeners
        imageViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                cldr.setTime(currentDate);
                int year = cldr.get(Calendar.YEAR);
                int month = cldr.get(Calendar.MONTH);
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                // Date picker dialog
                pickerDate = new DatePickerDialog(MainPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = dayOfMonth+"/"+(month+1)+"/"+year +" 00:00";

                        try {
                            SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            Date dateParsed = sdf.parse(date);
                            currentDate = dateParsed;
                            updateDate(currentDate);



                        } catch (ParseException e) {
                            Log.e("tag",e.getLocalizedMessage().toString());
                        }


                    }
                },year,month,day);
                pickerDate.show();
            }
        });

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            //Redirect to main
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else{
            //Get database ref
            DatabaseReference myRef = database.getReference("Users/"+currentUser.getUid()+"/Tasks/");
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    calendarEntries = new ArrayList<>();

                    for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren())
                    {
                        CalendarEntry databaseCalendarEntry = dataSnapshotChild.getValue(CalendarEntry.class);
                        databaseCalendarEntry.setDatabaseID(dataSnapshotChild.getRef().getKey());
                        calendarEntries.add(databaseCalendarEntry);

                    }
                    //update view according to results
                    updateRecyclerView(calendarEntries);
                    //Update date With current date
                    SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        Date dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"/" +Calendar.getInstance().get(Calendar.YEAR)+" 00:00");
                        updateDate(dateParsed);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(),"Database Error", Toast.LENGTH_SHORT).show();
                }
            });

        }
        //configure SimpleItemTouchCallback
        setupSimpleItemTouchCallback();
    }

    public void logout(View view) {
        mAuth.signOut();
        finish();

    }

    public void createEvent(View view) {
        Intent intent = new Intent(getApplicationContext(),EventCreatePage.class);
        startActivity(intent);
    }

    public void idProcessor(View view) {
        //Redirect to home page
        Intent intent = new Intent(getApplicationContext(),FacetecAuthentication.class);
        intent.putExtra("mode",3);
        startActivity(intent);


    }

    public void updateDate(Date date) {
        //set Day
        SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDateDay = dfday.format(date);
        textViewDateDay.setText(formattedDateDay);

        //set Month
        SimpleDateFormat dfmonth = new SimpleDateFormat("MMM", Locale.getDefault());
        String formattedDateMonth = dfmonth.format(date);
        textViewDateMonth.setText(formattedDateMonth);

        //set Year
        SimpleDateFormat dfyear = new SimpleDateFormat("yyyy", Locale.getDefault());
        String formattedDateYear = dfyear.format(date);
        textViewDateYear.setText(formattedDateYear);

        //get day of the week
        SimpleDateFormat dfdayOfTheWeek = new SimpleDateFormat("E", Locale.getDefault());
        String formattedDateDayOfTheWeek = dfdayOfTheWeek .format(date);

        //update recycler view
        List<CalendarEntry> calendarEntriesForAdapter=new ArrayList<>();

        //Get non repeating Tasks
        calendarEntriesForAdapter.addAll(calendarEntries.stream()
                .filter(calendarEntry -> (calendarEntry.getDate()==date.getTime() || calendarEntry.getRepeating()==1)  && (calendarEntry.getRepeating()!=2 && calendarEntry.getRepeating()!=3) )
                .collect(Collectors.toList())
        );
        //Get repeating tasks
        List<CalendarEntry> calendarEntriesRepeating = new ArrayList<>();
        calendarEntriesRepeating.addAll(calendarEntries.stream()
                .filter(calendarEntry -> calendarEntry.getRepeating()==2 || calendarEntry.getRepeating()==3  )
                .collect(Collectors.toList())
        );
        //Set repeating tasks to calendar view if appropriate
        for(CalendarEntry entry:calendarEntriesRepeating )
        {
            //find weekly repeating tasks
           if(entry.getRepeating()==2)
           {


               if(entry.getDayOfWeek().equals(formattedDateDayOfTheWeek))
               {
                   calendarEntriesForAdapter.add(entry);
               }

           }
        }
        updateRecyclerView(calendarEntriesForAdapter);
    }

    public void updateRecyclerView(List<CalendarEntry> calendarEntries){
        //create new adapter
        adapter = new CalendarEntitiesAdapter(  calendarEntries, new MyOnClickListener(){
            @Override
            public void onItemClicked(int index) {
                if(!authenticated && calendarEntries.get(index).isImportant()) {
                    Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                    intent.putExtra("mode",1);
                    startActivityForResult(intent,1);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(),EventDisplayPage.class);
                    intent.putExtra("CalendarEntry",calendarEntries.get(index));
                    startActivityForResult(intent,VIEW_ACTIVITY_REQUEST);

                }

            }
        });


        // Attach the adapter to the recyclerview to populate items
        calendarEntryRecyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        calendarEntryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //attach itemTouch helper to adapter
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(calendarEntryRecyclerView);
    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void setupSimpleItemTouchCallback() {
        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();

                Log.e( "tag","Item " + adapter.getDatabaseID(position) +" Deleted ");
                DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/"+adapter.getDatabaseID(position)
                );
                myRef.removeValue();
                calendarEntries.remove(position);
                updateRecyclerView(calendarEntries);

            }
        };
    }


} 