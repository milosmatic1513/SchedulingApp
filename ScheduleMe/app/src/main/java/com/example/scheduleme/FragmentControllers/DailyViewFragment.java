package com.example.scheduleme.FragmentControllers;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DailyViewFragment extends Fragment {
    CalendarEntitiesAdapter adapter;
    public List<CalendarEntry> calendarEntryList;
    List<CalendarEntry> calendarEntriesForAdapter;
    RecyclerView calendarEntryRecyclerView;
    TextView message;
    MainPage parent;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    Date currentDate;

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

        setupSimpleItemTouchCallback();
         mListener.onComplete();
    }

    public void passData(List<CalendarEntry> calendarEntryList)
    {
        Collections.sort(calendarEntryList);
        Collections.reverse(calendarEntryList);
        this.calendarEntryList = calendarEntryList;
    }

    public void updateRecyclerView(List<CalendarEntry> calendarEntriesForAdapter){
        //create new adapter
        adapter = new CalendarEntitiesAdapter(calendarEntriesForAdapter, new MyOnClickListener(){
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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(calendarEntryRecyclerView);
    }

    private void setupSimpleItemTouchCallback() {
        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(calendarEntriesForAdapter.get(viewHolder.getAdapterPosition()).isImportant() && !parent.isAuthenticated())
                {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.drawer_layout),getString(R.string.snackbar_important_warning) ,Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    parent.updateDate(currentDate);
                }
                else{
                    parent.deleteFromDatabase(viewHolder.getAdapterPosition(),adapter.getDatabaseID(viewHolder.getAdapterPosition()),calendarEntriesForAdapter.get(viewHolder.getAdapterPosition()));
                }

            }
    };
    }

    public void updateDate(Date date,String formattedDateDayOfTheWeek)
    {
        //update recycler view
        calendarEntriesForAdapter=new ArrayList<>();
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
        updateRecyclerView(calendarEntriesForAdapter);
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