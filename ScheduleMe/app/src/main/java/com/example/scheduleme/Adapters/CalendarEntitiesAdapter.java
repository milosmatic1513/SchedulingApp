package com.example.scheduleme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.MyOnClickListener;
import com.example.scheduleme.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class CalendarEntitiesAdapter extends RecyclerView.Adapter<CalendarEntitiesAdapter.ViewHolder> {


    private List<CalendarEntry> calendarEntries;
    private MyOnClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView taskTextView;
        public Button   moreInfoButton;
        public TextView timeTextView;
        public TextView importantTag;
        private WeakReference<MyOnClickListener> listenerRef;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, MyOnClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            taskTextView = (TextView) itemView.findViewById(R.id.eventNameText);
            moreInfoButton = (Button) itemView.findViewById(R.id.moreInfoButton);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            importantTag = (TextView) itemView.findViewById(R.id.importantMarker);

            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            moreInfoButton.setOnClickListener((view)->{
                listenerRef.get().onItemClicked(getAdapterPosition());
            });
        }


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.calendar_entry_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView,listener);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        CalendarEntry calendarEntry = calendarEntries.get(position);

        Button moreInfoButton = holder.moreInfoButton;

        // Set item views based on your views and data model
        TextView titleTextView = holder.taskTextView;
        titleTextView.setText(calendarEntry.getTitle());
        TextView timeTextView =holder.timeTextView;
        timeTextView.setText(calendarEntry.getTimeStart().substring(0,2)+":"+calendarEntry.getTimeStart().substring(2,4)+"-"+calendarEntry.getTimeEnd().substring(0,2)+":"+calendarEntry.getTimeEnd().substring(2,4));

        if(!calendarEntry.isImportant())
        {
            holder.importantTag.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return calendarEntries.size();
    }
    public CalendarEntitiesAdapter(List<CalendarEntry> calendarEntries, MyOnClickListener listener) {
        this.calendarEntries = calendarEntries;
        this.listener = listener;

    }

}
