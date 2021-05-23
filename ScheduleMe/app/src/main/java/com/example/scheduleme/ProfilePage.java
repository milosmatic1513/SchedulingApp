package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.Language;
import com.example.scheduleme.DataClasses.Preferences;
import com.example.scheduleme.DataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    User originalUser;
    User alteredUser;
    TextView emailTextView,nameTextView;
    Switch switchAuth;
    Button saveChangesButton;

    Spinner language;
    String currentLocale;
    boolean spinnerInitialized=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //set Languages
        currentLocale = Preferences.getLanguage(this);
        // Spinner
        language = findViewById(R.id.spinnerProfile);

        List<Language> languages = getLanguages();
        ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);

        int index = findIndexOfLanguage(languages, Preferences.getLanguage(this));
        if (index != -1) language.setSelection(index);

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerInitialized){
                    Language language = (Language) parent.getSelectedItem();
                    showDialogLanguageChange(language);
                }else {
                    spinnerInitialized=true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Component Initialization
        language = findViewById(R.id.spinnerProfile);
        emailTextView = findViewById(R.id.emailTextView);
        nameTextView = findViewById(R.id.nameTextView);
        switchAuth = findViewById(R.id.switch_auth);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.getBackground().setTint(ContextCompat.getColor(ProfilePage.this, R.color.common_google_signin_btn_text_dark_disabled));
        saveChangesButton.setEnabled(false);
        switchAuth.setEnabled(false);
        //Firebase Initialization
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            emailTextView.setText(currentUser.getEmail());

            DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid()+"/UserInfo");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    originalUser = snapshot.getValue(User.class);
                    alteredUser = snapshot.getValue(User.class);
                    nameTextView.setText(originalUser.getName());
                    switchAuth.setEnabled(true);
                    switchAuth.setChecked(originalUser.isLoginAuth());

                    switchAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            alteredUser.setLoginAuth(isChecked);
                            userChanged();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void saveChanges(View view) {
        saveChanges();
    }

    private void saveChanges(){
        DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid()+"/UserInfo");
        myRef.setValue(alteredUser);
        finish();
    }

    public void exit(View view) {
        exit();
    }

    private void exit() {
        if(alteredUser.equals(originalUser)){
            finish();
        }
        else{
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            saveChanges();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePage.this);
            builder.setTitle("Unsaved Changes");
            builder.setMessage("Do you want to save the changes?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public void onBackPressed(){
        exit();

    }

    public void editName(View view) {
        createViewPopUp();
    }

    private void createViewPopUp() {
        //create and set up alert Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilePage.this);
        alertDialog.setTitle(R.string.dialog_change_name_title);

        //create a layout
        LinearLayout layout = new LinearLayout(ProfilePage.this);
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 10, 20, 10);

        //create title Rating
        TextView nameText = new TextView(ProfilePage.this);
        nameText.setText(R.string.dialog_change_name_message);
        nameText.setLayoutParams(lp);
        layout.addView(nameText);
        //create first edit text (for Rating)
        EditText nameEditText = new EditText(ProfilePage.this);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT); //for decimal numbers
        nameEditText.setLayoutParams(lp);
        layout.addView(nameEditText);

        alertDialog.setView(layout);
        alertDialog.setPositiveButton(R.string.dialog_change_name_post, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nameEditText.getText().toString().trim().length() == 0 )
                {
                    Toast.makeText(ProfilePage.this, R.string.dialog_change_name_missing_info, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    alteredUser.setName(nameEditText.getText().toString());
                    nameTextView.setText(alteredUser.getName());
                    userChanged();
                }
            }
        });
        alertDialog.show();
    }

    private void showDialogLanguageChange(Language selectedLanguage){
        Locale locale = new Locale(selectedLanguage.getLanguageLocale());
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.getLocales().get(0);
        conf.setLocale(locale);
        res.updateConfiguration(conf, null); // second arg null means don't change

        //create and set up alert Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilePage.this);
        alertDialog.setTitle(R.string.dialog_change_language_title);

        //create a layout
        LinearLayout layout = new LinearLayout(ProfilePage.this);
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 10, 20, 10);

        TextView nameText = new TextView(ProfilePage.this);
        nameText.setText(R.string.dialog_change_language_message);
        nameText.setLayoutParams(lp);
        layout.addView(nameText);

        alertDialog.setView(layout);
        alertDialog.setPositiveButton(R.string.dialog_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLanguage(selectedLanguage);
                Intent intent=new Intent();
                intent.putExtra("restart",true);
                setResult(3,intent);
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.dialog_delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

        // restore original locale
        conf.setLocale(savedLocale);
        res.updateConfiguration(conf, null);
        List<Language> languages = getLanguages();
        spinnerInitialized=false;
        int index = findIndexOfLanguage(languages, Preferences.getLanguage(ProfilePage.this));
        if (index != -1) language.setSelection(index);

    }

    private void userChanged(){
        if(alteredUser.equals(originalUser)){
            saveChangesButton.setEnabled(false);
            saveChangesButton.getBackground().setTint(ContextCompat.getColor(ProfilePage.this, R.color.common_google_signin_btn_text_dark_disabled));
        }
        else{
            saveChangesButton.setEnabled(true);
            saveChangesButton.getBackground().setTint(Color.parseColor("#2980b9"));

        }
    }

    // Gets called when a new language is selected
    private void changeLanguage(Language language) {
        if (!language.getLanguageLocale().equals(Preferences.getLanguage(this))) {
            // Save language change
            Preferences.setLanguage(getApplicationContext(), language.getLanguageLocale());
            // Set locale
            Preferences.setLocale(this, currentLocale);
        }
    }

    // Gets the languages and their locales from the xml arrays and returns them as Language objects in a List
    private List<Language> getLanguages() {
        List<Language> languageList = new ArrayList<>();
        String[] langs = getResources().getStringArray(R.array.language_select_options);
        String[] langsLoc = getResources().getStringArray(R.array.language_locales_select_options);
        for (int i=0; i<langs.length; i++){
            languageList.add(new Language(langs[i], langsLoc[i]));
        }
        return languageList;
    }

    // Finds the index of the given languageLocale in a list of objects of type Language
    private int findIndexOfLanguage(List<Language> languages, String languageLocale) {
        for (int i=0; i<languages.size(); i++){
            if (languages.get(i).getLanguageLocale().toLowerCase().equals(languageLocale.toLowerCase()))
                return i;
        }
        return -1;
    }

}