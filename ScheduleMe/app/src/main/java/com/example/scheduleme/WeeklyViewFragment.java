package com.example.scheduleme;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
import com.example.scheduleme.DataClasses.CalendarEntry;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WeeklyViewFragment extends Fragment {

    CalendarEntitiesAdapter adapter;
    public List<CalendarEntry> calendarEntryList;
    //add to resources
    private String daysOfTheWeek[]= {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

    List<RecyclerView> weekdaysRecyclerView;
    List<TextView> weekdaysTextView;
    List<TextView> weekdaysDateTextView;
    MainPage parent;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        parent= ((MainPage)getActivity());

        weekdaysRecyclerView = new ArrayList<RecyclerView>();

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.monRecyclerView));
        weekdaysRecyclerView.get(0).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.tueRecyclerView));
        weekdaysRecyclerView.get(1).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.wedRecyclerView));
        weekdaysRecyclerView.get(2).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.thuRecyclerView));
        weekdaysRecyclerView.get(3).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.friRecyclerView));
        weekdaysRecyclerView.get(4).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.satRecyclerView));
        weekdaysRecyclerView.get(5).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysRecyclerView.add((RecyclerView) view.findViewById(R.id.sunRecyclerView));
        weekdaysRecyclerView.get(6).setLayoutManager(new LinearLayoutManager(view.getContext()));

        weekdaysTextView = new ArrayList<TextView>();
        weekdaysTextView.add(view.findViewById(R.id.Monday));
        weekdaysTextView.add(view.findViewById(R.id.Tuesday));
        weekdaysTextView.add(view.findViewById(R.id.Wednesday));
        weekdaysTextView.add(view.findViewById(R.id.Thursday));
        weekdaysTextView.add(view.findViewById(R.id.Friday));
        weekdaysTextView.add(view.findViewById(R.id.Saturday));
        weekdaysTextView.add(view.findViewById(R.id.Sunday));

        weekdaysDateTextView=new ArrayList<TextView>();
        weekdaysDateTextView.add(view.findViewById(R.id.dateMonday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateTuesday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateWednesday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateThursday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateFriday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateSaturday));
        weekdaysDateTextView.add(view.findViewById(R.id.dateSunday));

        setupSimpleItemTouchCallback();
        mListener.onComplete();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weeky_view, container, false);
    }
    private void setupSimpleItemTouchCallback() {

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                parent.deleteFromDatabase(viewHolder.getAdapterPosition(),adapter.getDatabaseID(viewHolder.getAdapterPosition()),calendarEntryList.get(viewHolder.getAdapterPosition()));

            }
        };
    }

    public void passData(List<CalendarEntry> calendarEntryList) {
        this.calendarEntryList=calendarEntryList;
    }

    public void updateDate(Date date) {
        //set Day
        SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDateDay = dfday.format(date);

        //get day of the week
        SimpleDateFormat dfdayOfTheWeek = new SimpleDateFormat("E", Locale.getDefault());
        String formattedDateDayOfTheWeek = dfdayOfTheWeek.format(date);

        int dayOfTheWeek = 0;

        for (int i = 0; i < daysOfTheWeek.length; i++) {
           if(formattedDateDayOfTheWeek.equalsIgnoreCase(daysOfTheWeek[i])) {

               weekdaysDateTextView.get(i).setText(formattedDateDay);
               dayOfTheWeek=i;
               populateRecycler(date, formattedDateDayOfTheWeek,dayOfTheWeek);

               weekdaysDateTextView.get(i).setBackgroundColor(Color.parseColor("#4056a1"));
               weekdaysDateTextView.get(i).setTextColor(ContextCompat.getColor(parent, R.color.white));
           }
        }

        Date dateTemp = new Date();
        dateTemp.setTime(date.getTime());
        for(int i = dayOfTheWeek+1; i < daysOfTheWeek.length; i++) {
            dateTemp.setTime(dateTemp.getTime()+86400000);
            weekdaysDateTextView.get(i).setText(dfday.format(dateTemp));
            populateRecycler(dateTemp,daysOfTheWeek[i],i);
            weekdaysDateTextView.get(i).setBackgroundColor(ContextCompat.getColor(parent, R.color.background_color));
            weekdaysDateTextView.get(i).setTextColor(ContextCompat.getColor(parent,android.R.color.secondary_text_light));

        }

        dateTemp = new Date();
        dateTemp.setTime(date.getTime());
        for(int i = dayOfTheWeek-1; i >=0; i--) {
            dateTemp.setTime(dateTemp.getTime()-86400000);
            weekdaysDateTextView.get(i).setText(dfday.format(dateTemp));
            populateRecycler(dateTemp,daysOfTheWeek[i],i);
            weekdaysDateTextView.get(i).setBackgroundColor(ContextCompat.getColor(parent, R.color.background_color));
            weekdaysDateTextView.get(i).setTextColor(ContextCompat.getColor(parent,android.R.color.secondary_text_light));
        }

    }

    public void populateRecycler(Date date,String weekday,int recyclerIndex) {
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
                if(entry.getDayOfWeek().equals(weekday))
                {
                    calendarEntriesForAdapter.add(entry);
                }
            }
        }
        updateRecyclerView(recyclerIndex,calendarEntriesForAdapter);
    }

    public void updateRecyclerView(int weekday , List<CalendarEntry> calendarEntryListForAdapter){
        //create new adapter
        adapter = new CalendarEntitiesAdapter(calendarEntryListForAdapter, new MyOnClickListener(){
            @Override
            public void onItemClicked(int index) {
               parent.setupBottomView(calendarEntryListForAdapter.get(index));

            }
        });

        // Attach the adapter to the recyclerview to populate items
        weekdaysRecyclerView.get(weekday).setAdapter(adapter);
        // Set layout manager to position the items

        //attach itemTouch helper to adapter
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(weekdaysRecyclerView.get(weekday));
    }

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }

    private OnCompleteListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }
}