package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.Preferences;
import com.example.scheduleme.DataClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    EditText passwordEditText , emailEditText;
    CheckBox rememberMeCheckbox;

    ProgressBar progressBar;
    static String HIDDEN_TAG_STRING = "hidden";
    SharedPreferences sharedPreferences;

    String currentLocale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get and set Language
        currentLocale = Preferences.getLanguage(this);
        Preferences.setLocale(this, currentLocale);

        //Shared Preferences Instantiation
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //components Initialization
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckbox=findViewById(R.id.rememberMeCheckBox);
        progressBar = findViewById(R.id.progressBarMainPage);
        progressBar.setVisibility(View.INVISIBLE);

        //listeners
        rememberMeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Store email and password to sharedPreferences if the checkbox is checked
                //or remove otherwise

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(isChecked) {
                    //save values in sharedPreferences
                    editor.putString("email", emailEditText.getText().toString());
                    editor.putString("password", passwordEditText.getText().toString());
                    editor.putBoolean("checked", true);
                }
                else
                {
                    //remove values from sharedPreferences
                    editor.remove("email");
                    editor.remove("password");
                    editor.remove("checked");
                }
                editor.apply();
            }
        });

        //set to password to hidden
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD );
        passwordEditText.setTag(HIDDEN_TAG_STRING);

        //Firebase Initialization
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        updateUI();



    }

    public void updateUI() {
        //update email and password based on previous logins stored in
        //shared preferences

        emailEditText.setText(sharedPreferences.getString("email",""));
        passwordEditText.setText(sharedPreferences.getString("password",""));
        rememberMeCheckbox.setChecked(sharedPreferences.getBoolean("checked",false));

    }

    public void toggleVisibility(View view) {
        //Toggles the visibility of the password field
        if(passwordEditText.getTag().toString().equals(HIDDEN_TAG_STRING))
        {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordEditText.setTag("");
        }
        else
        {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD );
            passwordEditText.setTag(HIDDEN_TAG_STRING);
        }

    }

    public void logIn(View view) {
        //Attempts to log the user into the app
        if(emailEditText.getText().toString().trim().length() != 0 && passwordEditText.getText().toString().trim().length() != 0) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser currentUser;
                                currentUser = mAuth.getCurrentUser();
                                if(currentUser!=null) {
                                    DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/UserInfo");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            User user = snapshot.getValue(User.class);
                                            if(user!=null)
                                            {
                                                if (user.isLoginAuth())
                                                {
                                                    Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                                                    intent.putExtra("mode",1);
                                                    startActivity(intent);
                                                }
                                                else {
                                                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), R.string.authentication_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterPage.class));
    }


}