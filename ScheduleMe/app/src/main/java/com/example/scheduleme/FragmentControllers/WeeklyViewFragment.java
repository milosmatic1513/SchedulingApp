package com.example.scheduleme.FragmentControllers;

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
import android.widget.Toast;

import com.example.scheduleme.Adapters.CalendarEntitiesAdapter;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.MainPage;
import com.example.scheduleme.MyOnClickListener;
import com.example.scheduleme.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WeeklyViewFragment extends Fragment {

    CalendarEntitiesAdapter adapter;
    public List<CalendarEntry> calendarEntryList;
    //add to resources
    private String daysOfTheWeek[]= {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    Date currentDate;
    List<Date> dates;
    List<RecyclerView> weekdaysRecyclerView;
    List<TextView> weekdaysTextView;
    List<TextView> weekdaysDateTextView;
    MainPage parent;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        parent= ((MainPage)getActivity());

        dates = new ArrayList<Date>() ;

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

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.dateMonday:
                        parent.updateDate(dates.get(0));
                        break;
                    case R.id.dateTuesday:
                        parent.updateDate(dates.get(1));
                        break;
                    case R.id.dateWednesday:
                        parent.updateDate(dates.get(2));
                        break;
                    case R.id.dateThursday:
                        parent.updateDate(dates.get(3));
                        break;
                    case R.id.dateFriday:
                        parent.updateDate(dates.get(4));
                        break;
                    case R.id.dateSaturday:
                        parent.updateDate(dates.get(5));
                        break;
                    case R.id.dateSunday:
                        parent.updateDate(dates.get(6));
                        break;
                }
            }
        };

        weekdaysDateTextView=new ArrayList<TextView>();
        weekdaysDateTextView.add(view.findViewById(R.id.dateMonday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateTuesday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateWednesday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateThursday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateFriday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateSaturday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);
        weekdaysDateTextView.add(view.findViewById(R.id.dateSunday));
        weekdaysDateTextView.get(weekdaysDateTextView.size() - 1).setOnClickListener(listener);

        mListener.onComplete();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weeky_view, container, false);
    }

    public void passData(List<CalendarEntry> calendarEntryList) {
        Collections.sort(calendarEntryList);
        Collections.reverse(calendarEntryList);
        this.calendarEntryList=calendarEntryList;
    }

    public void updateDate(Date date) {
        boolean currentDayInWeek=false;
        //set Day
        SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDateDay = dfday.format(date);

        //get day of the week
        SimpleDateFormat dfdayOfTheWeek = new SimpleDateFormat("E", Locale.getDefault());
        String formattedDateDayOfTheWeek = dfdayOfTheWeek.format(date);


        int index=java.util.Arrays.asList(daysOfTheWeek).indexOf(formattedDateDayOfTheWeek);
        dates = new ArrayList<Date>() ;
        for (int i = 0; i < daysOfTheWeek.length; i++) {
            Date dateTemp=new Date();
            dateTemp.setTime(date.getTime()-(index-i)*86400000);
            dates.add(dateTemp);
            populateRecycler(dateTemp, daysOfTheWeek[i],i);
            weekdaysDateTextView.get(i).setText(dfday.format(dateTemp));
            if(currentDate.getTime()==dateTemp.getTime()) {
                weekdaysTextView.get(i).getBackground().setTint(Color.parseColor("#4056a1"));
                weekdaysTextView.get(i).setTextColor(ContextCompat.getColor(parent, R.color.white));
                currentDayInWeek=true;
            }
            else{
                weekdaysTextView.get(i).getBackground().setTint(ContextCompat.getColor(parent, R.color.background_color));
                weekdaysTextView.get(i).setTextColor(ContextCompat.getColor(parent,android.R.color.secondary_text_light));

            }


            //Unselect all days
            weekdaysDateTextView.get(i).getBackground().setTint(ContextCompat.getColor(parent, R.color.background_color));
            weekdaysDateTextView.get(i).setTextColor(ContextCompat.getColor(parent,android.R.color.secondary_text_light));

        }
        //setSelectedDay
        weekdaysDateTextView.get(index).getBackground().setTint(Color.parseColor("#4056a1"));
        weekdaysDateTextView.get(index).setTextColor(ContextCompat.getColor(parent, R.color.white));


        if(!currentDayInWeek) {
            parent.showDateButton(currentDate);
        }
        else{
            parent.hideDateButton();

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