package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scheduleme.DataClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    //Components
    EditText emailText,password1Text,password2Text,nameText;
    static String HIDDEN_TAG_STRING="hidden";

    //Firebase
    FirebaseDatabase database;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //Component Initialisation
        emailText = findViewById(R.id.email);
        password1Text = findViewById(R.id.password1);
        password2Text = findViewById(R.id.password2);
        nameText = findViewById(R.id.name);

        //password1 hidden
        password1Text.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD );
        password1Text.setTag(HIDDEN_TAG_STRING);

        //password2 hidden
        password2Text.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD );
        password2Text.setTag(HIDDEN_TAG_STRING);

        //Firebase Instantiation
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void checkRegister(View view) {

        //check if any of the fields are empty
        if (
                emailText.getText().toString().trim().length()==0 ||
                        password1Text.getText().toString().trim().length()==0  ||
                        password2Text.getText().toString().trim().length()==0  ||
                        nameText.getText().toString().trim().length()==0
        )
        {
            //inform the user that all the fields are required
            Toast.makeText(this,getString(R.string.toast_fill_warning),Toast.LENGTH_LONG).show();
        }
        else
        {
            //check if passwords match
            if(password1Text.getText().toString().equals(password2Text.getText().toString())) {
                //create new User
                createUser(emailText.getText().toString(), password1Text.getText().toString());
            }
            else
            {
                //inform the user that the passwords don't match
                Toast.makeText(this,getString(R.string.password_match_error),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createUser( String email, String password) {
        //attempt to create user with the provided email and password
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Add other user data to database
                            DatabaseReference myRef = database.getReference("Users/" + user.getUid());
                            myRef.setValue(
                                    new User(nameText.getText().toString())
                            );

                            //Redirect to home page
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void toggleVisibility1(View view) {
        //Toggles the visibility of the password field
        if (password1Text.getTag().toString().equals(HIDDEN_TAG_STRING)) {
            password1Text.setInputType(InputType.TYPE_CLASS_TEXT);
            password1Text.setTag("");
        } else {
            password1Text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password1Text.setTag(HIDDEN_TAG_STRING);
        }
    }

    public void toggleVisibility2(View view) {
        //Toggles the visibility of the password field
        if (password2Text.getTag().toString().equals(HIDDEN_TAG_STRING)) {
            password2Text.setInputType(InputType.TYPE_CLASS_TEXT);
            password2Text.setTag("");
        } else {
            password2Text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password2Text.setTag(HIDDEN_TAG_STRING);
        }
    }
}