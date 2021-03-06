package com.example.scheduleme;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.Preferences;
import com.example.scheduleme.FragmentControllers.DailyViewFragment;
import com.example.scheduleme.FragmentControllers.DailyViewFragmentAlternative;
import com.example.scheduleme.FragmentControllers.WeeklyViewFragment;
import com.example.scheduleme.Utilities.ImageUtilities;
import com.example.scheduleme.Utilities.NetworkUtilities;
import com.example.scheduleme.Utilities.RandomStringGenerator;
import com.example.scheduleme.Utilities.ReminderUtilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
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
import java.util.stream.Stream;

public class MainPage extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener , WeeklyViewFragment.OnCompleteListener, DailyViewFragment.OnCompleteListener {
    //Request codes
    private static int VIEW_ACTIVITY_REQUEST=1;
    static int EDIT_ACTIVITY_REQUEST=2;
    static int PROFILE_ACTIVITY_REQUEST=3;
    static int FACETEC_ACTIVITY_REQUEST=4;
    static int PUBLIC_EVENT_ACTIVITY_REQUEST=5;
    //Database
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    //Private variables
    private boolean authenticated;
    int selectedViewMode;
    public Date currentDate;
    private CalendarEntry lastDeletedItem;
    //View Components
    ImageView authenticatedTag;
    TextView textViewDateDay;
    TextView textViewDateMonth;
    TextView textViewDateYear;
    TextView message;
    ImageView imageViewCalendar;
    //Buttons
    Button currentDateButton;
    //Layouts
    RelativeLayout loadingScreen;
    //Dialogs
    DatePickerDialog pickerDate;
    //Calendar View Components
    List<CalendarEntry> calendarEntries;
    List<CalendarEntry> calendarEntriesPublic;
    //Drawer layout
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    //Fragments
    DailyViewFragment dailyViewFragment;
    WeeklyViewFragment weeklyViewFragment;
    DailyViewFragmentAlternative dailyAltViewFragment;
    //reminders
    ReminderUtilities reminderUtilities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get and set Language
        String currentLocale = Preferences.getLanguage(this);
        Preferences.setLocale(this, currentLocale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //components Initialization
        authenticatedTag = findViewById(R.id.authenticatedTag);
        textViewDateDay  = findViewById(R.id.textViewDateDay);
        textViewDateMonth  = findViewById(R.id.textViewDateMonth);
        textViewDateYear  = findViewById(R.id.textViewDateYear);
        loadingScreen = findViewById(R.id.loadingPanel);
        currentDateButton=findViewById(R.id.currentDateButton);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        imageViewCalendar=findViewById(R.id.imageViewCalendar);
        message=findViewById(R.id.message);

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

        //set Listeners
        imageViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                if(currentDate==null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        Date dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                        cldr.setTime(dateParsed);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    cldr.setTime(currentDate);
                }
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
                            updateDate(dateParsed);

                        } catch (ParseException e) {
                            Log.e("tag",e.getLocalizedMessage().toString());
                        }


                    }
                },year,month,day);
                pickerDate.show();
            }
        });

        //Nav bar initialization
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        menu=navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null) {
            //Redirect to main
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else{
            //Get database ref
            DatabaseReference myRef = database.getReference("Users/"+currentUser.getUid()+"/Tasks/");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    //clear loading screen
                    loadingScreen.setVisibility(View.GONE);

                    calendarEntries = new ArrayList<>();

                    int index=0;
                    reminderUtilities.clearNotifications();
                    reminderUtilities.clearAllAlarms();
                    for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        CalendarEntry databaseCalendarEntry = dataSnapshotChild.getValue(CalendarEntry.class);
                        databaseCalendarEntry.setDatabaseID(dataSnapshotChild.getRef().getKey());
                        if(databaseCalendarEntry.getReminder() && databaseCalendarEntry.getDate()+databaseCalendarEntry.getTimeStart()>System.currentTimeMillis()){
                            if(index>=10){
                                Toast.makeText(getApplicationContext(),"ScheduleME only Supports 10 reminder notification please delete some",Toast.LENGTH_SHORT).show();
                            }else{
                                String calendarEntryTime = databaseCalendarEntry.calculateHourFrom()+":"+databaseCalendarEntry.calculateMinuteFrom()+" - "+databaseCalendarEntry.calculateHourTo()+":"+databaseCalendarEntry.calculateMinuteTo();

                                if(databaseCalendarEntry.getRepeating()==databaseCalendarEntry.REPEATING_NONE){
                                    reminderUtilities.startAlarm(databaseCalendarEntry.getTitle(),calendarEntryTime,index,databaseCalendarEntry.getDate()+databaseCalendarEntry.getTimeStart(),databaseCalendarEntry.getReminderTime());
                                    index++;
                                }
                                else if(databaseCalendarEntry.getRepeating()==databaseCalendarEntry.REPEATING_DAILY){
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    Date dateParsed;
                                    try {
                                        dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                                        reminderUtilities.startAlarm(databaseCalendarEntry.getTitle(),calendarEntryTime,index,dateParsed.getTime()+databaseCalendarEntry.getTimeStart(),databaseCalendarEntry.getReminderTime());
                                        index++;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                                else if(databaseCalendarEntry.getRepeating()==databaseCalendarEntry.REPEATING_WEEKLY){
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        Date dateParsed;
                                        try {
                                            dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                                            SimpleDateFormat dfdayOfTheWeek = new SimpleDateFormat("E", Locale.getDefault());
                                            String formattedDateDayOfTheWeek = dfdayOfTheWeek .format(dateParsed);
                                            if(formattedDateDayOfTheWeek.equalsIgnoreCase(databaseCalendarEntry.getDayOfWeek())){
                                                reminderUtilities.startAlarm(databaseCalendarEntry.getTitle(),calendarEntryTime,index,dateParsed.getTime()+databaseCalendarEntry.getTimeStart(),databaseCalendarEntry.getReminderTime());
                                                index++;
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                }

                            }
                        }
                        calendarEntries.add(databaseCalendarEntry);

                    }

                    //Update date With current date
                    if(currentDate==null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {
                            Date dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                            updateDate(dateParsed);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        updateDate(currentDate);
                    }

                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(),"Database Error", Toast.LENGTH_SHORT).show();
                }
            });

            //Get Public Events
            DatabaseReference myRefPublicEvents = database.getReference("Users/"+currentUser.getUid()+"/TasksReferences");
            myRefPublicEvents.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    calendarEntriesPublic = new ArrayList<>();
                    for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren())
                    {
                        DatabaseReference childReference = database.getReference(dataSnapshotChild.getValue(String.class));

                        childReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CalendarEntry calendarEntry =snapshot.getValue(CalendarEntry.class);
                                if(calendarEntry==null) {
                                    deleteReference(dataSnapshotChild.getKey());
                                }
                                else{
                                    calendarEntry.setDatabaseID("Users/"+currentUser.getUid()+"/TasksReferences/"+dataSnapshotChild.getKey());
                                    if(calendarEntriesPublic.stream().filter(o -> o.getDatabaseID().equalsIgnoreCase(calendarEntry.getDatabaseID())).findFirst().isPresent());
                                    {
                                        calendarEntriesPublic.removeIf(o -> o.getDatabaseID().equalsIgnoreCase(calendarEntry.getDatabaseID()));
                                        calendarEntry.setReminder(false);
                                        calendarEntriesPublic.add(calendarEntry);
                                    }
                                    //update if a child changes
                                    updateDate(currentDate);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }

                        });

                    }
                    //update if a reference is deleted
                    updateDate(currentDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        //set up calendar lists
        calendarEntries = new ArrayList<>();
        calendarEntriesPublic= new ArrayList<>();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        selectedViewMode=R.id.daily;
        dailyViewFragment = new DailyViewFragment();
        weeklyViewFragment = new WeeklyViewFragment();
        dailyAltViewFragment = new DailyViewFragmentAlternative();
        ft.replace(R.id.frameLayout, dailyViewFragment);
        ft.commit();

        //initialize reminder utilities
        reminderUtilities = new ReminderUtilities(this);
    }

    public void logout(View view) {
        logout();
    }

    public void logout() {
        //Log user out
        mAuth.signOut();
        finish();

    }

    public void createEvent(View view) {
        //Check if app is online
        if(NetworkUtilities.isNetworkAvailable(this)){
            //start EventCreatePage activity
            Intent intent = new Intent(getApplicationContext(),EventCreatePage.class);
            intent.putExtra("Date",getDate());
            startActivity(intent);
        }
        else{
            Toast.makeText(this,getString(R.string.internet_disabled),Toast.LENGTH_SHORT).show();
        }
    }

    public void idProcessor(View view) {
        //start FacetecAuthentication activity
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

        currentDate = date;
        //get correct entries
        List<CalendarEntry> calendarEntriesFinal = Stream.concat(calendarEntries.stream(), calendarEntriesPublic.stream())
                .collect(Collectors.toList());

        //pass data to correct fragment
        if(selectedViewMode==R.id.daily) {
            dailyViewFragment.passData(calendarEntriesFinal);
            dailyViewFragment.updateDate(date,formattedDateDayOfTheWeek);
        }
        else if (selectedViewMode==R.id.weekly){
            weeklyViewFragment.passData(calendarEntriesFinal);
            weeklyViewFragment.updateDate(date);
            textViewDateDay.setText("");
        }
        else{
            dailyAltViewFragment.passData(calendarEntriesFinal);
            dailyAltViewFragment.updateDate(date,formattedDateDayOfTheWeek);
        }


    }
    @Override
    public void onBackPressed(){
        //overide back button not to return to homescreen
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            this.moveTaskToBack(true);
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Initialize coresponding fragment or activity based on selection
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.weekly:
                selectedViewMode=R.id.weekly;
                ft.replace(R.id.frameLayout, weeklyViewFragment);
                ft.commit();
                break;
            case R.id.daily:
                selectedViewMode=R.id.daily;
                ft.replace(R.id.frameLayout, dailyViewFragment);
                ft.commit();
                break;
            case R.id.dailyAlt:
                selectedViewMode=R.id.dailyAlt;
                ft.replace(R.id.frameLayout, dailyAltViewFragment);
                ft.commit();
                break;
            case R.id.nav_public_events:
                startActivityForResult(new Intent(getApplicationContext(),PublicEventsPage.class),PUBLIC_EVENT_ACTIVITY_REQUEST);
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_profile:
                startActivityForResult(new Intent(getApplicationContext(),ProfilePage.class),PROFILE_ACTIVITY_REQUEST);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    public void setupBottomView(CalendarEntry calendarEntry) {
        setupBottomView(calendarEntry,false );
    }

    public void setupBottomView(CalendarEntry calendarEntry,boolean photoIdAuth) {
        //create bottom view
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainPage.this);
        bottomSheetDialog.setContentView(R.layout.bottom_view_layout);
        //components initialization
        TextView textViewTitle = bottomSheetDialog.findViewById(R.id.titleBottomView);
        TextView textViewDate = bottomSheetDialog.findViewById(R.id.dateViewPublicEvents);
        TextView textViewTime = bottomSheetDialog.findViewById(R.id.timePublicEvents);
        TextView textViewRepeating = bottomSheetDialog.findViewById(R.id.repeatingPublicEvents);
        TextView descriptionEditText = bottomSheetDialog.findViewById(R.id.descriptionEditText);
        TextView publicCodeBottomView=bottomSheetDialog.findViewById(R.id.publicCodeBottomView);
        TextView reminderTimeTag = bottomSheetDialog.findViewById(R.id.reminderTimeTag);
        LinearLayout descriptionBox=bottomSheetDialog.findViewById(R.id.descriptionBoxBottomView);
        LinearLayout imageBox=bottomSheetDialog.findViewById(R.id.imageBoxBottomView);
        LinearLayout authenticateLayout=bottomSheetDialog.findViewById(R.id.authenticateLayout);
        LinearLayout locationBoxBottomView=bottomSheetDialog.findViewById(R.id.locationBoxBottomView);

        ConstraintLayout publicCodeLayout=bottomSheetDialog.findViewById(R.id.publicCodeLayout);
        ConstraintLayout timeConstrainViewBottomView=bottomSheetDialog.findViewById(R.id.timeConstrainViewBottomView);

        NestedScrollView nestedScrollView  = bottomSheetDialog.findViewById(R.id.nestedScrollView);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imageBottomView);
        ImageButton buttonEdit = bottomSheetDialog.findViewById(R.id.editButtonBottomView);
        ImageButton buttonDelete= bottomSheetDialog.findViewById(R.id.deleteButtonBottomView);
        Button authenticateButton = bottomSheetDialog.findViewById(R.id.authenticateButton);
        Button showLocationButton = bottomSheetDialog.findViewById(R.id.showLocationButton);

        ImageButton copyButton = bottomSheetDialog.findViewById(R.id.copyButtonBottomView);

        bottomSheetDialog.show();
        textViewTitle.setText(calendarEntry.getTitle());
        //check if the user is authenticated and block out sensitive info
        if((!calendarEntry.isImportant() || authenticated) && (!calendarEntry.isRequireIdScan() || photoIdAuth)) {

            authenticateLayout.setVisibility(View.GONE);
            authenticateButton.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);

            textViewDate.setText(getString(R.string.event_date) + calendarEntry.getDayOfMonth() + "/" + calendarEntry.getMonth() + "/" + calendarEntry.getYear());
            textViewTime.setText(calendarEntry.calculateHourFrom()+":"+calendarEntry.calculateMinuteFrom()+"-"+calendarEntry.calculateHourTo()+":"+calendarEntry.calculateMinuteTo());
            String[] items = getResources().getStringArray(R.array.spinnerItems);
            textViewRepeating.setText(getString(R.string.event_repeating) + items[calendarEntry.getRepeating()]);

            //Display data
            if (calendarEntry.getDescription().length() == 0) {
                descriptionBox.setVisibility(View.GONE);
            } else {
                descriptionEditText.setText(calendarEntry.getDescription());
            }
            if (calendarEntry.getBase64Image().length() == 0) {
                imageBox.setVisibility(View.GONE);
            } else {
                Bitmap bitmap = ImageUtilities.base64ToBitmap(calendarEntry.getBase64Image());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    //set error image
                    imageBox.setVisibility(View.GONE);
                }
            }
            if(calendarEntry.getDatabaseID().contains("TasksReferences")){
                buttonEdit.setVisibility(View.GONE);
            }
            if(calendarEntry.getPublicCode().length()!=0) {
                publicCodeLayout.setVisibility(View.VISIBLE);
                publicCodeBottomView.setText(getString(R.string.event_public_code)+" "+calendarEntry.getPublicCode());
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Public Code", calendarEntry.getPublicCode());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else{
                publicCodeLayout.setVisibility(View.GONE);
            }
            if(calendarEntry.getType()==CalendarEntry.TYPE_REMINDER){timeConstrainViewBottomView.setVisibility(View.GONE);}
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(NetworkUtilities.isNetworkAvailable(getApplicationContext())) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(MainPage.this, EventCreatePage.class);
                        intent.putExtra("task", calendarEntry);
                        startActivityForResult(intent, EDIT_ACTIVITY_REQUEST);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),getString(R.string.internet_disabled),Toast.LENGTH_SHORT).show();
                    }

                }
            });
            if(calendarEntry.getLocationLong()!=0 && calendarEntry.getLocationLong()!=0){
                locationBoxBottomView.setVisibility(View.VISIBLE);
                showLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        String geoUri = "http://maps.google.com/maps?q=loc:" + calendarEntry.getLocationLat() + "," + calendarEntry.getLocationLong()  + " (" + calendarEntry.getTitle() + ")";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        startActivity(intent);
                    }
                });
            }
            else{
                locationBoxBottomView.setVisibility(View.GONE);

            }
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    deleteFromDatabase(calendarEntry.getDatabaseID(), calendarEntry);
                }
            });
            if(calendarEntry.getReminder()){
                reminderTimeTag.setVisibility(View.VISIBLE);
                String[] reminderTimes = getResources().getStringArray(R.array.reminderTimes);
                reminderTimeTag.setText(getString(R.string.reminder_time)+" "+reminderTimes[calendarEntry.getReminderTime()]);
            }
            else{
                reminderTimeTag.setVisibility(View.GONE);

            }
        }
        else{
            authenticateLayout.setVisibility(View.VISIBLE);
            authenticateButton.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            if(calendarEntry.isRequireIdScan()) authenticateButton.setText(getString(R.string.authenticate_id));
            authenticateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    if(!calendarEntry.isRequireIdScan()) {
                        Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                        intent.putExtra("mode", 1);
                        intent.putExtra("CalendarEntry",calendarEntry);
                        startActivityForResult(intent, FACETEC_ACTIVITY_REQUEST);

                    }else {
                        Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                        intent.putExtra("mode", 3);
                        intent.putExtra("CalendarEntry",calendarEntry);
                        startActivityForResult(intent, FACETEC_ACTIVITY_REQUEST);
                    }

                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==EDIT_ACTIVITY_REQUEST) {
            //Display edited calendar entry
            CalendarEntry calendarEntry=null;
            try {
                calendarEntry=(CalendarEntry) data.getSerializableExtra("calendarEntry");
            }catch (java.lang.NullPointerException e ){
                Log.e("Debug",e.getMessage());
            }

            if (calendarEntry==null)
            {
                Toast.makeText(getApplicationContext(),"There was an error loading the task", Toast.LENGTH_SHORT);
            }
            else
            {
                setupBottomView(calendarEntry);
            }
        }
        else if(requestCode==PROFILE_ACTIVITY_REQUEST)
        {
            //Check if restart is needed
            boolean restart =false;
            try {
                restart = data.getBooleanExtra("restart",false);
            }catch (java.lang.NullPointerException e ){
                Log.e("error",e.getMessage());
            }
            if (restart){
                String currentLocale = Preferences.getLanguage(this);
                Locale locale = new Locale(currentLocale);
                Locale.setDefault(locale);
                Resources resources = MainPage.this.getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        else if(requestCode==FACETEC_ACTIVITY_REQUEST) {
            //Check if user athenticated properly and show calendar entry
            try {
                authenticated=data.getBooleanExtra("Authenticated",false);
                CalendarEntry calendarEntry=(CalendarEntry) data.getSerializableExtra("CalendarEntry");
                int mode = data.getIntExtra("Mode",0);
                if(authenticated) {
                    authenticatedTag.setVisibility(View.VISIBLE);
                }
                else{
                    authenticatedTag.setVisibility(View.GONE);
                }

                if(calendarEntry!=null)
                {
                    if(mode==3)
                    {
                        setupBottomView(calendarEntry,true);
                    }
                    else{
                    setupBottomView(calendarEntry);}
                }
            }
            catch (java.lang.NullPointerException e ){
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }

        }



    }
    //fragment functions
    public void deleteFromDatabase(String databaseId,CalendarEntry calendarEntry){
        //Remove swiped item from list and notify the RecyclerView
        if(NetworkUtilities.isNetworkAvailable(getApplicationContext())) {
            if (calendarEntry.getDatabaseID().contains("TasksReferences")) {
                DatabaseReference myRef = database.getReference(calendarEntry.getDatabaseID());
                myRef.removeValue();
            } else {
                lastDeletedItem = calendarEntry;
                DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/" + databaseId);

                if (calendarEntry.getPublicCode().length() != 0) {
                    DatabaseReference myRefShared = database.getReference("PublicEvents/" + calendarEntry.getPublicCode());
                    myRefShared.removeValue();
                }


                Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "Item " + calendarEntry.getTitle() + " Deleted ", Snackbar.LENGTH_SHORT);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.setValue(lastDeletedItem);
                        calendarEntries.add(lastDeletedItem);
                        lastDeletedItem = null;

                    }
                });
                myRef.removeValue();
                snackbar.show();
            }
        }
        else{
            Toast.makeText(this,getString(R.string.internet_disabled),Toast.LENGTH_SHORT).show();
            updateDate(currentDate);
        }
    }
    @Override
    public void onComplete() {
        //Display data when fragment is loaded
        if(currentDate==null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                updateDate(dateParsed);
            } catch (ParseException e) {
                Toast.makeText(MainPage.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            updateDate(currentDate);
        }
    }

    public void showDateButton(Date date) {
        //update date to current date
        SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDateDay = dfday.format(date);
        currentDateButton.setText(formattedDateDay);
        currentDateButton.setVisibility(View.VISIBLE);
        currentDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate(date);
            }
        });
    }

    public void hideDateButton() {
        currentDateButton.setVisibility(View.GONE);
    }

    public Date getDate() {
        //return current date
        if(currentDate==null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date dateParsed = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
                return dateParsed;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return currentDate;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void nextDate(View view) {
        updateDate(new Date(currentDate.getTime()+86400000));
    }

    public void previousDate(View view) {
        updateDate(new Date(currentDate.getTime()-86400000));
    }

    private void deleteReference(String key) {
        //delete reference from database
        DatabaseReference databaseReference = database.getReference("Users/"+currentUser.getUid()+"/TasksReferences/"+key);
        databaseReference.removeValue();
    }


}
