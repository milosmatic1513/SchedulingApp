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

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.Preferences;
import com.example.scheduleme.DataClasses.User;
import com.example.scheduleme.Utilities.NetworkUtilities;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    private static int RC_SIGN_IN =532;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    EditText passwordEditText , emailEditText;
    CheckBox rememberMeCheckbox;

    ProgressBar progressBar;
    static String HIDDEN_TAG_STRING = "hidden";
    SharedPreferences sharedPreferences;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get and set Language
        String currentLocale = Preferences.getLanguage(this);
        Preferences.setLocale(this, currentLocale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        if(NetworkUtilities.isNetworkAvailable(this)) {
            //Attempts to log the user into the app
            if (emailEditText.getText().toString().trim().length() != 0 && passwordEditText.getText().toString().trim().length() != 0) {
                mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    // Sign in success, update UI with the signed-in user's information
                                    logInAuthenticated();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), getString(R.string.authentication_failed),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
        else{
            Toast.makeText(this,getString(R.string.internet_disabled),Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterPage.class));
    }

    public void onStart() {
        super.onStart();
        logInAuthenticated();
    }

    private void  logInAuthenticated(){
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
        }
    }

    public void logInWithGoogle(View view){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("262272836886-dckvp9s7cpolj8qtuod0i8384d3fvgoc.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}