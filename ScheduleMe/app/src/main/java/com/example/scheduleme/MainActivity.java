package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SQLiteDatabase database;

    EditText passwordEditText , emailEditText;
    CheckBox rememberMeCheckbox;
    static String HIDDEN_TAG_STRING = "hidden";
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Shared Preferences Instantiation
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //components Initialization
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckbox=findViewById(R.id.rememberMeCheckBox);

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
        mAuth = FirebaseAuth.getInstance();

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
                                // Sign in success, update UI with the signed-in user's information
                                //Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                //startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

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
    public void register(View view)
    {
        startActivity(new Intent(getApplicationContext(), RegisterPage.class));
    }

}