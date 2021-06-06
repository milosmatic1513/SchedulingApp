package com.example.scheduleme.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CalendarEntriesAdapterAlternative extends RecyclerView.Adapter<CalendarEntriesAdapterAlternative.ViewHolder> {


    private List<CalendarEntry> calendarEntries;
    private MyOnClickListener listener;

    int currentTime = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView taskTextView;
        public Button moreInfoButton;
        public TextView importantTag;
        public ConstraintLayout constraintLayout;
        private WeakReference<MyOnClickListener> listenerRef;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, MyOnClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.constaintLayoutAlternative);
            taskTextView = (TextView) itemView.findViewById(R.id.eventNameTextAlternative);
            moreInfoButton = (Button) itemView.findViewById(R.id.moreInfoButton);
            importantTag = (TextView) itemView.findViewById(R.id.importantMarkerAlternative);
            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            moreInfoButton.setOnClickListener((view)->{
                listenerRef.get().onItemClicked(getAdapterPosition());
            });
        }


    }
    @NonNull
    @Override
    public CalendarEntriesAdapterAlternative.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.calendar_entry_row_alternative, parent, false);

        // Return a new holder instance
        CalendarEntriesAdapterAlternative.ViewHolder viewHolder = new CalendarEntriesAdapterAlternative.ViewHolder(contactView,listener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEntriesAdapterAlternative.ViewHolder holder, int position) {
        // Get the data model based on position
        CalendarEntry calendarEntry = calendarEntries.get(position);

        Button moreInfoButton = holder.moreInfoButton;

        // Set item views based on your views and data model
        TextView titleTextView = holder.taskTextView;
        titleTextView.setText(calendarEntry.getTitle());

        if(!calendarEntry.isImportant()) {
            holder.importantTag.setVisibility(View.INVISIBLE);
        }

        //calculate hours
        int size =(int)Math.ceil((calendarEntry.getTimeEnd()-calendarEntry.getTimeStart())/1800000);
        final float scale = holder.itemView.getResources().getDisplayMetrics().density;;
        int pixels = (int) (60 * scale + 0.5f);
        if(size<1)
        {
            size=1;
            holder.constraintLayout.getLayoutParams().height=pixels;
        }else{
            holder.constraintLayout.getLayoutParams().height=pixels*size;

        }
        int timeStart=(int) calendarEntry.getTimeStart()/1800000;
        int timeEnd=(int) calendarEntry.getTimeEnd()/1800000;

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.constraintLayout.getLayoutParams();
        params.setMargins((int)(10 * scale + 0.5f), (int)(5 * scale + 0.5f)+(int)((timeStart-currentTime)*60*scale+0.5f), (int)(10 * scale + 0.5f), 0); //substitute parameters for left, top, right, bottom
        holder.constraintLayout.setLayoutParams(params);

        currentTime=timeEnd;
    }
    @Override
    public int getItemCount() {
        return calendarEntries.size();
    }

    public CalendarEntriesAdapterAlternative(List<CalendarEntry> calendarEntries, MyOnClickListener listener) {
        currentTime = 0;
        this.calendarEntries = calendarEntries;
        this.listener = listener;

    }

    public String getDatabaseID(int position){
        return calendarEntries.get(position).getDatabaseID();
    }

}