package com.example.scheduleme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.scheduleme.DataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    TextView emailTextView, nameTextView;
    Switch switchAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //Component Initialization
        emailTextView = findViewById(R.id.emailTextView);
        nameTextView = findViewById(R.id.nameTextView);
        switchAuth = findViewById(R.id.switch_auth);
        switchAuth.setEnabled(false);
        //Firebase Initialization
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
        {
            emailTextView.setText(currentUser.getEmail());

            DatabaseReference myRef = database.getReference("Users/" + currentUser.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    nameTextView.setText(user.getName());
                    switchAuth.setChecked(user.isLoginAuth());
                    switchAuth.setEnabled(true);
                    switchAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            DatabaseReference authRef = database.getReference("Users/" + currentUser.getUid()+"/"+"loginAuth");
                            authRef.setValue(isChecked);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public void onBackPressed(){
        finish();
    }
}