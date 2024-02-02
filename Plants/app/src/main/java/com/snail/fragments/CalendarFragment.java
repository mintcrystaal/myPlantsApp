package com.snail.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.snail.databases.DBEvents;
import com.snail.objects.EventInfo;
import com.snail.dialogs.CreateEventDialog;
import com.snail.plants.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    public Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private TextView monthName;
    private DBEvents mDBEvents;
    private long lastId;
    private Date lastClickedDate;

    private ArrayAdapter myAdapter;
    private List<String> changingEvents = new ArrayList<>();
    private List<EventInfo> changingEventsInfo = new ArrayList<>();

    private MyBroadcastReceiverDate brDate;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_calendar, container,false);

        final ListView eventsListView = mainView.findViewById(R.id.bookings_listview);
        myAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, changingEvents);

        eventsListView.setAdapter(myAdapter);
        compactCalendarView = mainView.findViewById(R.id.compact_calendar_view);
        monthName = mainView.findViewById(R.id.month_name);
        monthName.setText(dateFormatMonth.format(System.currentTimeMillis()));

        mDBEvents = new DBEvents(getContext());

        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.displayOtherMonthDays(false);
        compactCalendarView.invalidate();
        compactCalendarView.addEvents(mDBEvents.selectAll());

        Date date = new Date();
        long time = System.currentTimeMillis() - System.currentTimeMillis() % AlarmManager.INTERVAL_DAY;
        date.setTime(time);
        dateChanged(date);
        lastClickedDate = date;

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                lastClickedDate = dateClicked;
                currentCalendar.setTime(dateClicked);
                currentCalendar.set(Calendar.MILLISECOND, 0);
                currentCalendar.set(Calendar.SECOND, 0);
                currentCalendar.set(Calendar.MINUTE, 0);
                currentCalendar.set(Calendar.HOUR, 0);
                dateChanged(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthName.setText(dateFormatMonth.format(firstDayOfNewMonth));
                dateChanged(firstDayOfNewMonth);
            }
        });


        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Calendar calendar = Calendar.getInstance();
                final int myId = (int)id;
                final TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener () {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String name = changingEvents.get(myId);
                        String newName = name.substring(0, name.lastIndexOf("(")) + String.format("(%02d:%02d)", hourOfDay, minute);

                        EventInfo eventInfo = changingEventsInfo.get(myId);

                        long id = eventInfo.getId();
                        Event tempEvent = mDBEvents.select(id);
                        Date date = new Date();
                        date.setTime(lastClickedDate.getTime());
                        date.setHours(hourOfDay);
                        date.setMinutes(minute);
                        long newTime = date.getTime();
                        int color = tempEvent.getColor();

                        compactCalendarView.removeEvents(tempEvent.getTimeInMillis());
                        mDBEvents.delete(id, getContext());

                        mDBEvents.insert(mDBEvents.getLastId() + 1, newName, color, newTime, eventInfo.getConnectedWithPlant(), getContext());
                        addEvent(newName, color, newTime, id, eventInfo.getConnectedWithPlant());
                        myAdapter.notifyDataSetChanged();
                        dateChanged(lastClickedDate);
                    }
                };

                new TimePickerDialog(getActivity(), myCallBack,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
            }
        });


        eventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                lastId = changingEventsInfo.get((int)id).getId();
                onCreateDeleteDialog().show();
                return true;
            }
        });


        FloatingActionButton add = mainView.findViewById(R.id.add_note_button);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogFragment makeEvent = new CreateEventDialog();
                makeEvent.show(getFragmentManager(), "createEvent");
            }
        });

        brDate = new MyBroadcastReceiverDate();

        IntentFilter intFilt = new IntentFilter("datePicked");
        getActivity().registerReceiver(brDate, intFilt);

        return mainView;
    }

    private Dialog onCreateDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.if_delete);
        builder.setPositiveButton(R.string.ok, dialogClickListener);
        builder.setNegativeButton(R.string.cancel, dialogClickListener);
        return builder.create();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    Event tempEvent = mDBEvents.select(lastId);
                    compactCalendarView.removeEvents(tempEvent.getTimeInMillis());
                    mDBEvents.delete(lastId, getContext());
                    dateChanged(lastClickedDate);
                    myAdapter.notifyDataSetChanged();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(brDate);
    }

    private void dateChanged(Date newDate) {
        List<Event> newDayEvents = mDBEvents.selectByDate(newDate.getTime());
        compactCalendarView.removeEvents(newDate);
        compactCalendarView.addEvents(newDayEvents);
        if (newDayEvents != null) {
            changingEvents.clear();
            changingEventsInfo.clear();
            for (Event event : newDayEvents) {
                EventInfo eventInfo = (EventInfo) event.getData();
                changingEvents.add(eventInfo.getName());
                changingEventsInfo.add(eventInfo);
            }
        }
        myAdapter.notifyDataSetChanged();
    }


    public class MyBroadcastReceiverDate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String eventName = intent.getStringExtra("name");
                long timeInMillis = intent.getLongExtra("time", -1);
                int color = intent.getIntExtra("color", Color.argb(255, 23, 188, 230));
                long id = intent.getLongExtra("id", -1);
                addEvent(eventName, color, timeInMillis, id, -1);
                dateChanged(lastClickedDate);
            }
            catch (Exception RuntimeException) {}
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        monthName.setText(dateFormatMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
    }

    public void addEvent(String name, int color, long timeInMillis, long id, long plant) {
        compactCalendarView.addEvents(Arrays.asList(new Event(color, timeInMillis, new EventInfo(name, id, plant))));
    }
}