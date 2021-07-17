package com.example.scheduleme.FragmentControllers;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
import com.example.scheduleme.Adapters.CalendarEntriesReminderAdapter;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.FragmentControllers.WeeklyViewFragment;
import com.example.scheduleme.MainPage;
import com.example.scheduleme.MyOnClickListener;
import com.example.scheduleme.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DailyViewFragment extends Fragment {

    public List<CalendarEntry> calendarEntryList;
    List<CalendarEntry> calendarEntriesEvents;
    List<CalendarEntry> calendarEntriesReminder;
    RecyclerView calendarEntryRecyclerView;
    RecyclerView reminderEntryRecyclerView;
    LinearLayout linearLayoutReminder;
    TextView message;
    MainPage parent;

    ItemTouchHelper.SimpleCallback simpleItemTouchCallbackEvents;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallbackReminders;
    Date currentDate;
    Date selectedDate;
    Switch switchReminder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            currentDate = sdf.parse(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_view, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        parent= ((MainPage)getActivity());

        message = view.findViewById(R.id.message);

        calendarEntryRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        calendarEntryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        reminderEntryRecyclerView = (RecyclerView) view.findViewById(R.id.reminderRecyclerDailyView);
        reminderEntryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        linearLayoutReminder = (LinearLayout)view.findViewById(R.id.linearLayoutReminderDailyView);
        switchReminder = (Switch)view.findViewById(R.id.switchReminderDailyView) ;
        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    reminderEntryRecyclerView.setVisibility(View.GONE);
                }
                else{
                    reminderEntryRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        switchReminder.setChecked(true);
        setupSimpleItemTouchCallbackEvents();
        setupSimpleItemTouchCallbackReminders();
         mListener.onComplete();
    }

    public void passData(List<CalendarEntry> calendarEntryList) {
        this.calendarEntryList = calendarEntryList;
    }

    public void updateRecyclerView(List<CalendarEntry> calendarEntriesForAdapter){
        //create new adapter
        CalendarEntitiesAdapter adapter = new CalendarEntitiesAdapter(calendarEntriesForAdapter, new MyOnClickListener(){
            @Override
            public void onItemClicked(int index) {
                parent.setupBottomView(calendarEntriesForAdapter.get(index));
            }
        });
        if(calendarEntriesForAdapter.size()==0) {
            message.setVisibility(View.VISIBLE);
        }
        else{
            message.setVisibility(View.GONE);
        }


        // Attach the adapter to the recyclerview to populate items
        calendarEntryRecyclerView.setAdapter(adapter);
        // Set layout manager to position the items

        //attach itemTouch helper to adapter
        ItemTouchHelper itemTouchHelperEvents = new ItemTouchHelper(simpleItemTouchCallbackEvents);
        itemTouchHelperEvents.attachToRecyclerView(calendarEntryRecyclerView);

    }

    public void updateRecyclerViewEvents(List<CalendarEntry> calendarEntriesForAdapter){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parent.getDisplay().getRealMetrics(displayMetrics);
        if(calendarEntriesForAdapter.size() == 0 ) linearLayoutReminder.setVisibility(View.GONE);
        else linearLayoutReminder.setVisibility(View.VISIBLE);
        CalendarEntriesReminderAdapter adapter = new CalendarEntriesReminderAdapter(calendarEntriesForAdapter, new MyOnClickListener(){
            @Override
            public void onItemClicked(int index) {
                parent.setupBottomView(calendarEntriesForAdapter.get(index));
            }
        },displayMetrics.widthPixels);

        reminderEntryRecyclerView.setAdapter(adapter);
        //attach itemTouch helper to adapter
        ItemTouchHelper itemTouchHelperReminders = new ItemTouchHelper(simpleItemTouchCallbackReminders);
        itemTouchHelperReminders.attachToRecyclerView(reminderEntryRecyclerView);

    }

    private void setupSimpleItemTouchCallbackEvents() {
        simpleItemTouchCallbackEvents = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(calendarEntriesEvents.get(viewHolder.getAdapterPosition()).isImportant() && !parent.isAuthenticated())
                {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.drawer_layout),getString(R.string.snackbar_important_warning) ,Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    parent.updateDate(selectedDate);
                }
                else{
                    parent.deleteFromDatabase(calendarEntriesEvents.get(viewHolder.getAdapterPosition()).getDatabaseID(),calendarEntriesEvents.get(viewHolder.getAdapterPosition()));
                }

            }
    };
    }

    private void setupSimpleItemTouchCallbackReminders() {
        simpleItemTouchCallbackReminders = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(calendarEntriesReminder.get(viewHolder.getAdapterPosition()).isImportant() && !parent.isAuthenticated())
                {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.drawer_layout),getString(R.string.snackbar_important_warning) ,Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    parent.updateDate(selectedDate);
                }
                else{
                    parent.deleteFromDatabase(calendarEntriesReminder.get(viewHolder.getAdapterPosition()).getDatabaseID(),calendarEntriesReminder.get(viewHolder.getAdapterPosition()));
                }

            }
        };
    }

    public void updateDate(Date date,String formattedDateDayOfTheWeek) {
        selectedDate=date;
        //update recycler view
        List<CalendarEntry> calendarEntriesForAdapter=new ArrayList<>();
        //Get non repeating Tasks
        calendarEntriesForAdapter.addAll(calendarEntryList.stream()
                .filter(calendarEntry -> (calendarEntry.getDate()==date.getTime() || calendarEntry.getRepeating()==1)  && (calendarEntry.getRepeating()!=2 && calendarEntry.getRepeating()!=3) )
                .collect(Collectors.toList())
        );
        //Get repeating tasks
        List<CalendarEntry> calendarEntriesRepeating = new ArrayList<>();
        calendarEntriesRepeating.addAll(calendarEntryList.stream()
                .filter(calendarEntry -> calendarEntry.getRepeating()==2 || calendarEntry.getRepeating()==3  )
                .collect(Collectors.toList())
        );
        //Set repeating tasks to calendar view if appropriate
        for(CalendarEntry entry:calendarEntriesRepeating )
        {
            //find weekly repeating tasks
            if(entry.getRepeating()==2)
            {
                if(entry.getDayOfWeek().equals(formattedDateDayOfTheWeek))
                {
                    calendarEntriesForAdapter.add(entry);
                }
            }
        }
        calendarEntriesReminder=new ArrayList<>();
        calendarEntriesReminder.addAll(calendarEntriesForAdapter.stream()
                .filter(calendarEntry -> (calendarEntry.getType()==CalendarEntry.TYPE_REMINDER))
                .collect(Collectors.toList())
        );
        calendarEntriesEvents = new ArrayList<>();
        calendarEntriesEvents.addAll(calendarEntriesForAdapter.stream()
                .filter(calendarEntry -> (calendarEntry.getType()==CalendarEntry.TYPE_EVENT))
                .collect(Collectors.toList())
        );
        //sort before updating
        Collections.sort(calendarEntriesEvents);
        updateRecyclerView(calendarEntriesEvents);
        updateRecyclerViewEvents(calendarEntriesReminder);
        if(date.getTime()!=currentDate.getTime()) {
            parent.showDateButton(currentDate);
        }
        else{
            parent.hideDateButton();

        }
    }

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }

    private WeeklyViewFragment.OnCompleteListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (WeeklyViewFragment.OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

}