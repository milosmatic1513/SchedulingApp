package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.DataClasses.CalendarEntryBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    private boolean authenticated;
    TextView authenticatedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //components Initialization
        authenticatedTag = findViewById(R.id.authenticatedTag);

        //Check if the user Is authenticated;
        Intent intent = getIntent();
        authenticated=intent.getBooleanExtra("Authenticated",false);
        if(!authenticated)
        {
            authenticatedTag.setVisibility(View.GONE);
        }


        //Firebase Initialization
        mAuth = FirebaseAuth.getInstance();

        // Lookup the recyclerview in activity layout
        RecyclerView calendarEntryRecyclerView = (RecyclerView) findViewById(R.id.calendarEntryRecyclerView);
        CalendarEntry calendarEntry1 = new CalendarEntryBuilder().setTitle("Ραντεβου με γιατρό").setUserID("test").setImportant(true).setTimeStart("0830").setTimeEnd("0930").build();
        CalendarEntry calendarEntry2 = new CalendarEntryBuilder().setTitle("Καφέ με τα παιδιά").setTimeStart("1030").setTimeEnd("1130").setUserID("test").build();




        List<CalendarEntry> calendarEntries = new ArrayList<>();
        calendarEntries.add(calendarEntry1);
        calendarEntries.add(calendarEntry2);

        // Create adapter passing in the sample user data
        CalendarEntitiesAdapter adapter = new CalendarEntitiesAdapter(calendarEntries,new MyOnClickListener(){
            @Override
            public void onItemClicked(int index) {
                if(!authenticated && calendarEntries.get(index).isImportant()) {
                    Intent intent = new Intent(getApplicationContext(), FacetecAuthentication.class);
                    intent.putExtra("mode",1);
                    startActivity(intent);
                }
                else
                {
                    Log.e("tag",calendarEntries.get(index).getTitle());

                }

            }
        });
        // Attach the adapter to the recyclerview to populate items
        calendarEntryRecyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        calendarEntryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void logout(View view)
    {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
           startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        CalendarEntry calendarEntry1 = new CalendarEntryBuilder().setTitle("Ραντεβου με γιατρό").setUserID("test").setImportant(true).build();
        CalendarEntry calendarEntry2 = new CalendarEntryBuilder().setTitle("Καφέ με τα παιδιά").setUserID("test").build();





       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                CalendarEntry task = dataSnapshot.getValue(CalendarEntry.class);
                Log.d("test", "Value is: " + task.getTitle());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test", "Failed to read value.", error.toException());
            }
        });*/

    }
    public void setAuthenticated(boolean authenticated)
    {
        this.authenticated = authenticated;
    }
}