
package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.CalendarEntryBuilder;
import com.example.scheduleme.DataClasses.Preferences;
import com.example.scheduleme.Utilities.ImageUtilities;
import com.example.scheduleme.Utilities.RandomStringGenerator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventCreatePage extends AppCompatActivity implements LocationListener {
    //firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    TimePickerDialog pickerTime;
    DatePickerDialog pickerDate;

    TextView publicCodeTextView;
    TextView createPageTitle;

    EditText editTextTitle;
    EditText editTextDescription;
    EditText editTextFrom;
    EditText editTextTo;
    EditText editTextDate;

    Spinner spinnerRepeat;
    Spinner spinnerType;
    Spinner spinnerReminderTime;

    Switch switchImportant;
    Switch switchPhotoId;
    Switch descriptionSwitch;
    Switch imageSwitch;
    Switch locationSwitch;
    Switch publicSwitch;
    Switch reminderSwitch;

    CalendarEntry calendarEntry;
    Date date;

    Button createButton;
    Button takeImageButton;
    Button loadImageButton;
    ImageView imageView;
    ImageView imageViewCopy;
    Button locationButton;
    ProgressBar progressBar;
    private GoogleMap mMap;
    private Marker current_location_marker;

    LinearLayout mapLayout;
    ConstraintLayout timeConstraintLayout;
    ConstraintLayout constraintLayoutPublic;
    ConstraintLayout constraintLayoutReminderTime;
    private final int RESULT_LOAD_IMG = 155;
    private final int RESULT_TAKE_PHOTO = 156;
    private final int RESULT_TURN_ON_LOCATION= 157;
    Bitmap imageBitmap = null;

    LocationManager locationManager;

    //timeValues
    int hourFrom = 0;
    int minuteFrom = 0;
    int hourTo= 0;
    int minuteTo = 0;
    //marker
    LatLng selectedMarkerLatLong;
    //string
    String publicCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get and set Language
        String currentLocale = Preferences.getLanguage(this);
        Preferences.setLocale(this, currentLocale);

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
        switchPhotoId=(Switch)findViewById(R.id.switchPhotoId);
        createButton = (Button)findViewById(R.id.createButton);
        spinnerRepeat = (Spinner)findViewById(R.id.spinner);
        spinnerType = (Spinner)findViewById(R.id.spinner2);
        descriptionSwitch = findViewById(R.id.publicSwitch);
        imageSwitch = findViewById(R.id.imageSwitch);
        imageView=findViewById(R.id.imageViewTaskPage);
        takeImageButton=findViewById(R.id.takeImageButton);
        loadImageButton = findViewById(R.id.loadImageButton);
        calendarEntry = (CalendarEntry) getIntent().getSerializableExtra("task");
        date = (Date) getIntent().getSerializableExtra("Date");
        locationSwitch = findViewById(R.id.locationSwitch);
        mapLayout = findViewById(R.id.mapLayout);
        timeConstraintLayout = findViewById(R.id.timeConstraintLayout);
        constraintLayoutPublic=findViewById(R.id.constraintLayoutPublic);
        constraintLayoutPublic.setVisibility(View.GONE);
        publicSwitch=findViewById(R.id.publicSwitchCreateView);
        publicCodeTextView=findViewById(R.id.publicCodeTextView);
        imageViewCopy=findViewById(R.id.imageViewCopy);
        locationButton=findViewById(R.id.locationButton);
        locationButton.setEnabled(false);
        progressBar = findViewById(R.id.progressBarEventCreate);
        reminderSwitch=findViewById(R.id.reminderSwitch);
        constraintLayoutReminderTime=findViewById(R.id.constraintLayoutReminderTime);
        spinnerReminderTime=findViewById(R.id.spinnerReminderTime);
        createPageTitle=findViewById(R.id.createPageTitle);
        //listeners
        switchImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchPhotoId.setEnabled(true);
                }
                else{
                    switchPhotoId.setEnabled(false);
                    switchPhotoId.setChecked(false);
                }
            }
        });

        descriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    editTextDescription.setVisibility(View.VISIBLE);
                }
                else
                {
                    editTextDescription.setVisibility(View.GONE);
                }
            }
        });

        imageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadImageButton.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    takeImageButton.setVisibility(View.VISIBLE);
                }
                else {
                    loadImageButton.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    takeImageButton.setVisibility(View.GONE);

                }
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mapLayout.setVisibility(View.VISIBLE);
                    gps();
                    if(mMap!=null){
                        mMap.clear();

                    }
                }
                else {
                    mapLayout.setVisibility(View.GONE);
                    selectedMarkerLatLong=null;
                }
            }
        });

        editTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(calendarEntry!=null) {
                    try {
                        hour = Integer.parseInt(calendarEntry.calculateHourFrom());
                        hourFrom = hour;
                        minutes = Integer.parseInt(calendarEntry.calculateMinuteFrom());
                        minuteFrom=minutes;
                    }catch (Exception e) {
                        Toast.makeText(EventCreatePage.this,"Error While Parsing Time",Toast.LENGTH_SHORT);
                    }
                }
                // time picker dialog
                pickerTime = new TimePickerDialog(EventCreatePage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                int timeInMinutesFrom = (sHour*60)+sMinute;
                                int timeInMinutesTo= (hourTo*60)+minuteTo;
                                Log.e("Time","Time from : "+timeInMinutesFrom +" Time to:"+timeInMinutesTo);

                                if((timeInMinutesFrom < timeInMinutesTo) ||editTextTo.getText().length()==0) {
                                    hourFrom=sHour;
                                    minuteFrom=sMinute;
                                    if (sHour < 10 && sMinute < 10) {
                                        editTextFrom.setText("0" + sHour + ":0" + sMinute);
                                    } else if (sMinute < 10) {
                                        editTextFrom.setText(sHour + ":0" + sMinute);
                                    } else if (sHour < 10) {
                                        editTextFrom.setText("0" + sHour + ":" + sMinute);
                                    } else {
                                        editTextFrom.setText(sHour + ":" + sMinute);
                                    }
                                }
                                else{
                                    Toast.makeText(EventCreatePage.this,getString(R.string.time_warning),Toast.LENGTH_SHORT).show();
                                }
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
                        hour = Integer.parseInt(calendarEntry.calculateHourTo());
                        hourTo = hour;
                        minutes = Integer.parseInt(calendarEntry.calculateMinuteTo());
                        minuteTo=minutes;
                    }catch (Exception e) {
                        Toast.makeText(EventCreatePage.this,"Error While Parsing Time",Toast.LENGTH_SHORT);
                    }
                }
                // time picker dialog
                pickerTime = new TimePickerDialog(EventCreatePage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                int timeInMinutesFrom = (hourFrom*60)+minuteFrom;
                                int timeInMinutesTo= (sHour*60)+sMinute;
                                Log.e("Time","Time from : "+timeInMinutesFrom +" Time to:"+timeInMinutesTo);
                                if((timeInMinutesFrom < timeInMinutesTo) ||editTextFrom.getText().length()==0) {
                                    hourTo=sHour;
                                    minuteTo=sMinute;
                                    if (sHour < 10 && sMinute < 10) {
                                        editTextTo.setText("0" + sHour + ":0" + sMinute);
                                    } else if (sMinute < 10) {
                                        editTextTo.setText(sHour + ":0" + sMinute);
                                    } else if (sHour < 10) {
                                        editTextTo.setText("0" + sHour + ":" + sMinute);
                                    } else {
                                        editTextTo.setText(sHour + ":" + sMinute);
                                    }
                                }
                                else{
                                    Toast.makeText(EventCreatePage.this,getString(R.string.time_warning),Toast.LENGTH_SHORT).show();
                                }
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


                        } catch (ParseException e) {
                            Log.e("tag",e.getLocalizedMessage ().toString());
                        }

                    }
                },year,month,day);
                pickerDate.show();
            }

        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==CalendarEntry.TYPE_REMINDER) {
                    timeConstraintLayout.setVisibility(View.GONE);
                    reminderSwitch.setChecked(false);
                }
                else{
                    timeConstraintLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        publicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(publicCode.length()==0) publicCode = RandomStringGenerator.generateString(10);
                    constraintLayoutPublic.setVisibility(View.VISIBLE);
                    publicCodeTextView.setText(getString(R.string.event_public_code)+" "+publicCode);
                    imageViewCopy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Public Code", publicCode);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getApplicationContext(), getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else{
                    constraintLayoutPublic.setVisibility(View.GONE);

                }
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(spinnerType.getSelectedItemPosition()==CalendarEntry.TYPE_REMINDER){
                        reminderSwitch.setChecked(false);
                        Toast.makeText(getApplication(),getString(R.string.reminder_warning),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        constraintLayoutReminderTime.setVisibility(View.VISIBLE);
                    }
                }else{
                    constraintLayoutReminderTime.setVisibility(View.GONE);

                }
            }
        });

        //location manager initialization
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //setup google maps
        setupMap();

        if(calendarEntry != null){
            createPageTitle.setText(getString(R.string.edit_task));
            editTextTitle.setText(calendarEntry.getTitle());
            if(calendarEntry.getDescription().length()!=0){
                editTextDescription.setText(calendarEntry.getDescription());
                editTextDescription.setVisibility(View.VISIBLE);
                descriptionSwitch.setChecked(true);
            }
            if(calendarEntry.getBase64Image().length()!=0){

                Bitmap bitmap = ImageUtilities.base64ToBitmap(calendarEntry.getBase64Image());
                if(bitmap!=null) {
                    imageView.setImageBitmap(bitmap);
                    imageBitmap=bitmap;
                    loadImageButton.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    takeImageButton.setVisibility(View.VISIBLE);
                    imageSwitch.setChecked(true);
                }
                else {
                    //set error image
                    imageSwitch.setChecked(false);
                    loadImageButton.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    takeImageButton.setVisibility(View.GONE);
                }
            }
            else {
                imageSwitch.setChecked(false);
                loadImageButton.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                takeImageButton.setVisibility(View.GONE);
            }
            if(calendarEntry.getPublicCode().length()!=0){
                publicCode=calendarEntry.getPublicCode();
                publicSwitch.setChecked(true);
            }
            if(calendarEntry.getPublicCode().length()!=0){
                publicCode=calendarEntry.getPublicCode();
                publicSwitch.setChecked(true);
            }
            if(calendarEntry.getLocationLat()!=0 && calendarEntry.getLocationLong()!=0){
                locationSwitch.setChecked(true);
                selectedMarkerLatLong=new LatLng(calendarEntry.getLocationLat(),calendarEntry.getLocationLong());
            }


            switchImportant.setChecked(calendarEntry.isImportant());
            switchPhotoId.setChecked(calendarEntry.isRequireIdScan());
            reminderSwitch.setChecked(calendarEntry.getReminder());
            spinnerReminderTime.setSelection(calendarEntry.getReminderTime());
            spinnerRepeat.setSelection(calendarEntry.getRepeating());
            spinnerType.setSelection(calendarEntry.getType());
            editTextDate.setText(calendarEntry.getDayOfMonth()+"/"+calendarEntry.getMonthNumeric()+"/"+calendarEntry.getYear());
            editTextDate.setTag(Long.toString(calendarEntry.getDate()));
            editTextFrom.setText(calendarEntry.calculateHourFrom()+":"+calendarEntry.calculateMinuteFrom());
            hourFrom=Integer.parseInt(calendarEntry.calculateHourFrom());
            minuteFrom=Integer.parseInt(calendarEntry.calculateMinuteFrom());
            editTextTo.setText(calendarEntry.calculateHourTo()+":"+calendarEntry.calculateMinuteTo());
            hourTo=Integer.parseInt(calendarEntry.calculateHourTo());
            minuteTo=Integer.parseInt(calendarEntry.calculateMinuteTo());

        }
        else{
            if (date!=null) {

                //set Day
                SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
                String formattedDateDay = dfday.format(date);


                //set Month
                SimpleDateFormat dfmonth = new SimpleDateFormat("MM", Locale.getDefault());
                String formattedDateMonth = dfmonth.format(date);


                //set Year
                SimpleDateFormat dfyear = new SimpleDateFormat("yyyy", Locale.getDefault());
                String formattedDateYear = dfyear.format(date);

                editTextDate.setTag(Long.toString(date.getTime()));

                editTextDate.setText(formattedDateDay + "/" +formattedDateMonth + "/" + formattedDateYear);
            }
        }




    }

    public void checkCreate(View view) {
        //check if any of the fields are empty
        if ((editTextTitle.getText().toString().trim().length()==0 || editTextDate.getText().toString().trim().length()==0)  || ((editTextTo.getText().toString().trim().length()==0 || editTextFrom.getText().toString().trim().length()==0) && spinnerType.getSelectedItemPosition()==calendarEntry.TYPE_EVENT))
        {
            //inform the user that all the fields are required
            Toast.makeText(this,getString(R.string.toast_fill_warning),Toast.LENGTH_LONG).show();
        }
        else if(locationSwitch.isChecked() && selectedMarkerLatLong == null)
        {
            //inform the user that all the fields are required
            Toast.makeText(this,getString(R.string.location_warning),Toast.LENGTH_LONG).show();
        }
        else
          {
            Long dateMillis = Long.parseLong(editTextDate.getTag().toString());
            Long timeFromMillis = (hourFrom*60+minuteFrom)*60000l;
            Long timeToMillis = (hourTo*60+minuteTo)*60000l;

            CalendarEntry newCalendarEntry = new CalendarEntryBuilder()
                                            .setTitle(editTextTitle.getText().toString())
                                            .setImportant(switchImportant.isChecked())
                                            .setRequireIdScan(switchPhotoId.isChecked())
                                            .setDate(dateMillis)
                                            .setRepeating(spinnerRepeat.getSelectedItemPosition())
                                            .setType(spinnerType.getSelectedItemPosition())
                                            .setReminder(reminderSwitch.isChecked())
                                            .build();
            //time set
            if(spinnerType.getSelectedItemPosition() == CalendarEntry.TYPE_EVENT ){
                 newCalendarEntry.setTimeStart(timeFromMillis);
                 newCalendarEntry.setTimeEnd(timeToMillis);
            }
            //description Checkbox
            if(editTextDescription.getText().toString().trim().length()!=0 && descriptionSwitch.isChecked())
            {
                newCalendarEntry.setDescription(editTextDescription.getText().toString());
            }
            //image Checkbox
            if(imageBitmap!=null && imageSwitch.isChecked()) {
                newCalendarEntry.setBase64Image(ImageUtilities.bitmapToBase64(imageBitmap));
            }
            //location Checkbox
            if(selectedMarkerLatLong!=null && locationSwitch.isChecked()){
                newCalendarEntry.setLocationLat(selectedMarkerLatLong.latitude);
                newCalendarEntry.setLocationLong(selectedMarkerLatLong.longitude);
            }
            if(publicSwitch.isChecked()){
                newCalendarEntry.setPublicCode(publicCode);
            }
            if(reminderSwitch.isChecked()){
                newCalendarEntry.setReminderTime(spinnerReminderTime.getSelectedItemPosition());

            }
            if(calendarEntry==null) {
                DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/").push();
                myRef.setValue(newCalendarEntry);
                if(newCalendarEntry.getPublicCode().length()!=0){
                    DatabaseReference myRefShared = database.getReference("PublicEvents/"+newCalendarEntry.getPublicCode());
                    myRefShared.setValue("Users/" + currentUser.getUid() + "/Tasks/"+myRef.getKey());
                }
                finish();
            }
            else
            {
                if(calendarEntry.getDatabaseID().trim().length()!=0){
                    DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/Tasks/"+calendarEntry.getDatabaseID());
                    myRef.setValue(newCalendarEntry);
                    Intent intent=new Intent();
                    newCalendarEntry.setDatabaseID(calendarEntry.getDatabaseID());
                    if(publicCode.length()!=0 && newCalendarEntry.getPublicCode().length()==0) {
                        DatabaseReference myRefShared = database.getReference("PublicEvents/" +publicCode);
                        myRefShared.removeValue();
                    }
                    if(newCalendarEntry.getPublicCode().length()!=0){
                        DatabaseReference myRefShared = database.getReference("PublicEvents/"+newCalendarEntry.getPublicCode());
                        myRefShared.setValue("Users/" + currentUser.getUid() + "/Tasks/"+myRef.getKey());
                    }

                    finish();
                    intent.putExtra("calendarEntry",newCalendarEntry);
                    setResult(2,intent);

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

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 237);
                return;
            }
            startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.error_opening_camera), Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Camera opening", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RESULT_LOAD_IMG ) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(imageBitmap);
                } catch (FileNotFoundException e) {

                    Toast.makeText(EventCreatePage.this, R.string.failure_image_loading, Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (reqCode == RESULT_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
            }
            else {
                Toast.makeText(EventCreatePage.this, R.string.failure_taking_picture,Toast.LENGTH_LONG).show();
            }
        }
        else if (reqCode==RESULT_TURN_ON_LOCATION){
            gps();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent();
        intent.putExtra("calendarEntry",calendarEntry);
        setResult(2,intent);
        finish();
    }

    private void gps() {
        //Gps Function
        //Handles location tracking start = true : location is updated , start = false location is not updated
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        }
        else{
            buildAlertMessageNoGps();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationButton.setEnabled(true);
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        progressBar.setVisibility(View.GONE);

        if (current_location_marker != null)
            current_location_marker.remove();

        current_location_marker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title(getString(R.string.event_current_location))
                .zIndex(1000f) // Always on top of other markers
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );

        zoomOnCurrentMarker();
        locationManager.removeUpdates(this);
    }

    private void zoomOnCurrentMarker() {
        if (mMap != null && current_location_marker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location_marker.getPosition(), 15f), 1000, null);
        }
    }

    public void zoomOnCurrentMarker(View view) {
        zoomOnCurrentMarker();
    }

    private void setupMap(){
        if (mMap == null) {
            SupportMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    ScrollView mScrollView = findViewById(R.id.scrollMap); //parent scrollview in xml, give your scrollview id value
                    ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .setListener(new WorkaroundMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch()
                                {
                                    mScrollView.requestDisallowInterceptTouchEvent(true);
                                }
                            });

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            if(current_location_marker!=null) {
                                selectedMarkerLatLong = null;
                                mMap.clear();
                                if(editTextTitle.length()==0){
                                    mMap.addMarker(new MarkerOptions().position(latLng).
                                            title("Location Marker")
                                    );
                                }
                                else{
                                    mMap.addMarker(new MarkerOptions().position(latLng).
                                            title(editTextTitle.getText().toString())
                                    );
                                }
                                selectedMarkerLatLong = latLng;
                                mMap.addMarker(new MarkerOptions().position(current_location_marker.getPosition())
                                        .title(getString(R.string.event_current_location))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                );
                            }
                        }
                    });
                    if (selectedMarkerLatLong!=null){
                        if(editTextTitle.length()==0){
                            mMap.addMarker(new MarkerOptions().position(selectedMarkerLatLong).
                                    title("Location Marker")
                            );
                        }
                        else{
                            mMap.addMarker(new MarkerOptions().position(selectedMarkerLatLong).
                                    title(editTextTitle.getText().toString())
                            );
                        }
                    }
                }
            });
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_location))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_delete_yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),RESULT_TURN_ON_LOCATION);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_delete_no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}