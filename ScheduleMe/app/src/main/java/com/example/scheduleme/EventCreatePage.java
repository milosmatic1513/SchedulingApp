
package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.CalendarEntryBuilder;
import com.example.scheduleme.Utilities.ImageUtilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class EventCreatePage extends AppCompatActivity implements OnMapReadyCallback  {
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
    Switch switchPhotoId;
    Switch descriptionSwitch;
    Switch imageSwitch;
    Switch locationSwitch;

    CalendarEntry calendarEntry;
    Date date;

    Button createButton;
    Button takeImageButton;
    Button loadImageButton;
    ImageView imageView;

    private GoogleMap mMap;
    private Marker current_location_marker;
    LinearLayout mapLayout;
    private final int RESULT_LOAD_IMG = 155;
    private final int RESULT_TAKE_PHOTO = 156;
    Bitmap imageBitmap = null;

    LocationManager locationManager;

    //timeValues
    int hourFrom = 0;
    int minuteFrom = 0;
    int hourTo= 0;
    int minuteTo = 0;
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
        switchPhotoId=(Switch)findViewById(R.id.switchPhotoId);
        createButton = (Button)findViewById(R.id.createButton);
        spinnerRepeat = (Spinner)findViewById(R.id.spinner);
        descriptionSwitch = findViewById(R.id.descriptionSwitch);
        imageSwitch = findViewById(R.id.imageSwitch);
        imageView=findViewById(R.id.imageViewTaskPage);
        takeImageButton=findViewById(R.id.takeImageButton);
        loadImageButton = findViewById(R.id.loadImageButton);
        calendarEntry = (CalendarEntry) getIntent().getSerializableExtra("task");
        date = (Date) getIntent().getSerializableExtra("Date");
        locationSwitch = findViewById(R.id.locationSwitch);
        mapLayout = findViewById(R.id.mapLayout);


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

                }
                else {
                    mapLayout.setVisibility(View.GONE);


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
                                hourFrom=sHour;
                                minuteFrom=sMinute;
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
                                hourTo=sHour;
                                minuteTo=sMinute;

                                if(sHour<10 && sMinute<10) {
                                    editTextTo.setText("0"+sHour + ":0" + sMinute);
                                }
                                else if(sMinute<10) {
                                    editTextTo.setText(sHour + ":0" + sMinute);
                                }
                                else if(sHour<10) {
                                    editTextTo.setText("0"+sHour + ":" + sMinute);
                                }
                                else {
                                    editTextTo.setText(sHour + ":" + sMinute);
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

        if(calendarEntry != null){
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
            else
            {
                imageSwitch.setChecked(false);
                loadImageButton.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                takeImageButton.setVisibility(View.GONE);
            }
            switchImportant.setChecked(calendarEntry.isImportant());
            switchPhotoId.setChecked(calendarEntry.isRequireIdScan());
            spinnerRepeat.setSelection(calendarEntry.getRepeating());
            editTextDate.setText(calendarEntry.getDayOfMonth()+"/"+calendarEntry.getMonthNumeric()+"/"+calendarEntry.getYear());
            editTextDate.setTag(Long.toString(calendarEntry.getDate()));
            editTextFrom.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4));
            editTextTo.setText(calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //location manager initialization
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    public void checkCreate(View view) {
        //check if any of the fields are empty
        if (
                editTextTitle.getText().toString().trim().length()==0 ||
                        editTextFrom.getText().toString().trim().length()==0  ||
                        editTextTo.getText().toString().trim().length()==0    ||
                        editTextDate.getText().toString().trim().length()==0
        )
        {
            //inform the user that all the fields are required
            Toast.makeText(this,getString(R.string.toast_fill_warning),Toast.LENGTH_LONG).show();
        }
        else
        {
            Long dateMillis = Long.parseLong(editTextDate.getTag().toString());


            //Long timeFromMillis = (hourFrom*60+minuteFrom)*60000l;
            //Long timeToMillis = (hourTo*60+minuteTo)*60000l;

            CalendarEntry newCalendarEntry= new CalendarEntryBuilder()
                    .setTitle(editTextTitle.getText().toString())
                    .setTimeStart(editTextFrom.getText().toString().replace(":",""))
                    .setTimeEnd(editTextTo.getText().toString().replace(":",""))
                    .setImportant(switchImportant.isChecked())
                    .setRequireIdScan(switchPhotoId.isChecked())
                    .setDate(dateMillis)
                    .setRepeating(spinnerRepeat.getSelectedItemPosition())
                    .build();
            //description Checkbox
            if(editTextDescription.getText().toString().trim().length()!=0 && descriptionSwitch.isChecked())
            {
                newCalendarEntry.setDescription(editTextDescription.getText().toString());
            }
            //image Checkbox
            if(imageBitmap!=null && imageSwitch.isChecked())
            {
                Log.e("tag",ImageUtilities.bitmapToBase64(imageBitmap));
                newCalendarEntry.setBase64Image(ImageUtilities.bitmapToBase64(imageBitmap));
            }

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("New Custom Marker"));
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent();
        intent.putExtra("calendarEntry",calendarEntry);
        setResult(2,intent);
        finish();
    }
}