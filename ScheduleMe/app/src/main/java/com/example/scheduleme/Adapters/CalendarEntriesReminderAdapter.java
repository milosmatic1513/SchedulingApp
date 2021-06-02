package com.example.scheduleme.Adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scheduleme.DataClasses.CalendarEntry;
import com.example.scheduleme.MyOnClickListener;
import com.example.scheduleme.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class CalendarEntriesReminderAdapter extends RecyclerView.Adapter<CalendarEntriesReminderAdapter.ViewHolder> {


    private List<CalendarEntry> calendarEntries;
    private MyOnClickListener listener;
    private int parentWidth;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView taskTextView;
        public Button moreInfoButton;
        public TextView importantTag;

        public CardView cardView;
        public ConstraintLayout constraintLayout;
        private WeakReference<MyOnClickListener> listenerRef;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, MyOnClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.constaintLayoutReminder);
            cardView = (CardView)itemView.findViewById(R.id.cardViewReminder);
            taskTextView = (TextView) itemView.findViewById(R.id.eventNameTextReminder);
            moreInfoButton = (Button) itemView.findViewById(R.id.moreInfoButtonReminder);
            importantTag = (TextView) itemView.findViewById(R.id.importantMarkerReminder);
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
        View contactView = inflater.inflate(R.layout.calendar_entry_row_reminder, parent, false);

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

        if (!calendarEntry.isImportant()) {
            holder.importantTag.setVisibility(View.INVISIBLE);
        }
        if (!calendarEntry.isRequireIdScan()) {
            //holder.idImageView.setVisibility(View.INVISIBLE);
        }


        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.constraintLayout.getLayoutParams();
        if(calendarEntries.size()<=3) {
            params.width = parentWidth/ calendarEntries.size();
        }
        else{
            params.width = parentWidth / 3;

        }
    }
    @Override
    public int getItemCount() {
        return calendarEntries.size();
    }

    public CalendarEntriesReminderAdapter(List<CalendarEntry> calendarEntries, MyOnClickListener listener,int parentWidth) {
        this.calendarEntries = calendarEntries;
        this.listener = listener;
        this.parentWidth=parentWidth;
    }


    public String getDatabaseID(int position){
        return calendarEntries.get(position).getDatabaseID();
    }

}

