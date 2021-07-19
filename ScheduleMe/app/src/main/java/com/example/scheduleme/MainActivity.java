package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.Button;
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
    //static variables
    private static int RC_SIGN_IN =532;
    static int FACETEC_ACTIVITY_REQUEST=4;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;

    //components
    EditText passwordEditText , emailEditText;
    CheckBox rememberMeCheckbox;

    ProgressBar progressBar;
    ProgressBar progressBarGoogle;
    ProgressBar progressBarForgotPassword;
    static String HIDDEN_TAG_STRING = "hidden";
    SharedPreferences sharedPreferences;

    Button googleButton;
    Button loginButton;
    Button facebookButton;


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
        progressBarForgotPassword=findViewById(R.id.progressBarForgotPassword);
        googleButton=findViewById(R.id.googleButton);
        loginButton=findViewById(R.id.loginButton);
        facebookButton=findViewById(R.id.facebookButton);

        progressBar = findViewById(R.id.progressBarMainPage);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarGoogle = findViewById(R.id.progressBarMainPageGoogle);
        progressBarGoogle.setVisibility(View.INVISIBLE);
        progressBarForgotPassword.setVisibility(View.INVISIBLE);
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
        //log user in with local account
        if(NetworkUtilities.isNetworkAvailable(this)) {

            toogleButtons(false);

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
                                    toogleButtons(true);
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

    private void  logInAuthenticated(){
        //once user is logged in get private data
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid() + "/UserInfo");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                    toogleButtons(true);
                    User user = snapshot.getValue(User.class);
                    if(user!=null)
                    {
                        //check if user wants authentication on login
                        if (user.isLoginAuth())
                        {
                            Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                            intent.putExtra("mode",1);
                            startActivityForResult(intent,MainActivity.FACETEC_ACTIVITY_REQUEST);
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
                    toogleButtons(true);
                }
            });
        }
    }

    public void logInWithGoogle(View view){
        //log user in with google account
        toogleButtons(false);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("230370227975-bl9tmu15rl9ral6at66fd9afhhjssdpg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Handle google login results
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(),account.getDisplayName());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                toogleButtons(true);
            }
        }
        else if(requestCode==4) {
            //Handle authenticate results
            if(data.getBooleanExtra("Authenticated",false)){
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
            else{
                Snackbar snackbar = Snackbar.make(findViewById(R.id.ConstraintLayoutMainActivity),"Could not Authenticate ", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken,String displayName) {
        //when user logs in with google, create a firebase login instance
        progressBarGoogle.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseRef = database.getReference("Users/");
                            databaseRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean userInDatabase=false;
                                    for(DataSnapshot child : snapshot.getChildren()){
                                        if(child.getKey().equalsIgnoreCase(user.getUid())){
                                            userInDatabase=true;
                                        }
                                    }
                                    progressBarGoogle.setVisibility(View.INVISIBLE);
                                    if(userInDatabase){
                                        logInAuthenticated();
                                    }
                                    else{
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // Add other user data to database
                                                        DatabaseReference myRef = database.getReference("Users/" + user.getUid()+"/UserInfo");
                                                        myRef.setValue(
                                                                new User(displayName)
                                                        );

                                                        Intent intent = new Intent(getApplicationContext(),FacetecAuthentication.class);
                                                        intent.putExtra("mode",2);
                                                        startActivityForResult(intent,FACETEC_ACTIVITY_REQUEST);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:

                                                        break;
                                                }
                                            }
                                        };
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle(getString(R.string.dialog_first_time_title));
                                        builder.setMessage(getString(R.string.dialog_first_time_body)).setPositiveButton(getString(R.string.dialog_delete_yes), dialogClickListener)
                                                .setNegativeButton(getString(R.string.dialog_delete_no), dialogClickListener).show();
                                    }
                                    databaseRef.removeEventListener(this);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBarGoogle.setVisibility(View.INVISIBLE);
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void resetPassword(View view) {
        //send user reset email
        if (emailEditText.getText().length()!=0) {
            view.setEnabled(false);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = emailEditText.getText().toString();
            progressBarForgotPassword.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBarForgotPassword.setVisibility(View.INVISIBLE);
                            view.setEnabled(true);
                            if (task.isSuccessful()) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(getString(R.string.email_sent));
                                builder.setMessage(getString(R.string.email_sent_text)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                            else{
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.ConstraintLayoutMainActivity), getString(R.string.email_validation), Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    });
        }
        else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.ConstraintLayoutMainActivity), getString(R.string.email_fill_warning), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void toogleButtons(boolean mode){
        //toggle login buttons
        loginButton.setEnabled(mode);
        facebookButton.setEnabled(mode);
        googleButton.setEnabled(mode);
    }

    public void logInWithFacebook(View view) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.ConstraintLayoutMainActivity),"Authentication with Facebook is not yet supported", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}