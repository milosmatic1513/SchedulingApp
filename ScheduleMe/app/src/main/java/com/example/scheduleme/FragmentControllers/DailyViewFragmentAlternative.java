package com.example.scheduleme.FragmentControllers;

import android.content.Context;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.scheduleme.Adapters.CalendarEntriesAdapterAlternative;
import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.FragmentControllers.WeeklyViewFragment;
import com.example.scheduleme.MainPage;
import com.example.scheduleme.MyOnClickListener;
import com.example.scheduleme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class DailyViewFragmentAlternative extends Fragment {
    public List<CalendarEntry> calendarEntryList;
    List<CalendarEntry> calendarEntriesForAdapter;
    //RecyclerView calendarEntryRecyclerView;
    LinearLayout linearLayoutAlternative;
    HorizontalScrollView horizontalScrollView;
    NestedScrollView nestedScrollView;
    MainPage parent;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    Date currentDate;
    View view;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_view_alternative, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        parent= ((MainPage)getActivity());

        linearLayoutAlternative=(LinearLayout)view.findViewById(R.id.linearLayoutAlternative);
        horizontalScrollView=(HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollViewAlternative);
        this.view = view;
        mListener.onComplete();
    }

    public void passData(List<CalendarEntry> calendarEntryList) {
        Collections.sort(calendarEntryList);
        Collections.reverse(calendarEntryList);
        this.calendarEntryList = calendarEntryList;
    }

    public void updateRecyclerView(List<CalendarEntry> calendarEntriesForAdapter){

        //get scale of view
        final float scale = view.getResources().getDisplayMetrics().density;

        //sort Entries in RecyclerViews

        //initialize list of recyclerViews to put tasks in
        List<List<CalendarEntry>> recyclerViewTasks = new ArrayList<List<CalendarEntry>>();
        //Add the first list
        recyclerViewTasks.add(new ArrayList<CalendarEntry>());

        for(CalendarEntry calendarEntry : calendarEntriesForAdapter){
            boolean hasBeenAssigned = false;
            for(List<CalendarEntry> list : recyclerViewTasks)
            {
                if(list.size()==0 || (list.get(list.size()-1).getTimeEnd()<calendarEntry.getTimeStart()))
                {
                    list.add(calendarEntry);
                    hasBeenAssigned = true;
                    break;
                }
            }
            if(!hasBeenAssigned) {
                recyclerViewTasks.add(new ArrayList<CalendarEntry>());
                recyclerViewTasks.get(recyclerViewTasks.size()-1).add(calendarEntry);

            }

        }

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parent.getDisplay().getRealMetrics(displayMetrics);
        if(recyclerViewTasks.size()<=3) {
            params.width = (displayMetrics.widthPixels-(int) (50 * scale + 0.5f)) / recyclerViewTasks.size();
        }
        else{
            params.width = (displayMetrics.widthPixels-(int) (50* scale + 0.5f))  / 3;
        }
        linearLayoutAlternative.removeAllViewsInLayout();

        for(int i=0;i<recyclerViewTasks.size();i++){
            RecyclerView recyclerView = new RecyclerView(parent);

            recyclerView.setLayoutParams(params);

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            //create new adapter
            int finalI = i;
            CalendarEntriesAdapterAlternative adapter = new CalendarEntriesAdapterAlternative(recyclerViewTasks.get(i), new MyOnClickListener(){
                @Override
                public void onItemClicked(int index) {
                    parent.setupBottomView(recyclerViewTasks.get(finalI).get(index));
                }
            });

            recyclerView.setAdapter(adapter);

            linearLayoutAlternative.addView(recyclerView);
        }



        //calculate pixels to get to first task if it exists

        int depthInPixels=0;
        if(recyclerViewTasks.get(0).size()!=0){
            int depthInDp =(int) recyclerViewTasks.get(0).get(0).getTimeStart()/1800000;
            depthInPixels = (int) (depthInDp * 60 * scale + 0.5f);
        }


        //create a handler and scroll to appropriate view
        Handler h = new Handler();
        //copy to an effectively final variable to be accesible from the handler
        int finalDepthInPixels = depthInPixels;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.smoothScrollTo(0, (int) finalDepthInPixels);
            }
        }, 250); // 250 ms delay


    }

    public void updateDate(Date date,String formattedDateDayOfTheWeek) {
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