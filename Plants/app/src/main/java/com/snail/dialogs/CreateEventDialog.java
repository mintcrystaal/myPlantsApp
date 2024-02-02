package com.snail.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.snail.databases.DBEvents;
import com.snail.plants.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.snail.fragments.MainActivity.DEFAULT_TIME_HOUR;
import static com.snail.fragments.MainActivity.DEFAULT_TIME_MINUTE;

public class CreateEventDialog extends DialogFragment {

    private Calendar calendar = Calendar.getInstance();
    private Button chosenTime;
    private Button chosenDate;

    private int myYear = 2019;
    private int myMonth = 1;
    private int myDay = 1;
    private int myHourOfDay = DEFAULT_TIME_HOUR;
    private int myMinute = DEFAULT_TIME_MINUTE;

    private String eventName = "";

    private EditText writeEventName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_event_dialog, null);

        chosenTime = dialogView.findViewById(R.id.timeView);
        chosenDate = dialogView.findViewById(R.id.dateView);
        writeEventName = dialogView.findViewById(R.id.event_name);

        chosenTime.setText(String.format("%02d:%02d", myHourOfDay, myMinute));
        chosenDate.setText(String.format("%02d.%02d.%02d", myDay, myMonth, myYear));

        chosenTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener () {

                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myHourOfDay = hourOfDay;
                        myMinute = minute;
                        chosenTime.setText(String.format("%02d:%02d", myHourOfDay, myMinute));
                    }
                };

                new TimePickerDialog(getActivity(), myCallBack,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true)
                        .show();
            }
        });

        chosenDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myYear = year;
                        myMonth = monthOfYear + 1;
                        myDay = dayOfMonth;
                        chosenDate.setText(String.format("%02d.%02d.%02d", myDay, myMonth, myYear));
                    }
                };

                new DatePickerDialog(getActivity(), myCallBack,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        writeEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                eventName = writeEventName.getText().toString();
            }
        });


        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DBEvents mDBEvents = new DBEvents(getContext());
                        if (eventName.length() == 0)
                            eventName = getString(R.string.event) + " " + (mDBEvents.getLastId() + 1);

                        Intent intent = new Intent("datePicked");
                        int color = getActivity().getColor(R.color.other_day);
                        intent.putExtra("color", color);

                        long timeInMillis = Date.parse(myYear + "/" + myMonth + "/" + myDay + " " + myHourOfDay + ":" + myMinute);
                        intent.putExtra("time", timeInMillis);

                        SimpleDateFormat dateFormatEventName = new SimpleDateFormat("(HH:mm)", Locale.getDefault());
                        eventName += " " + dateFormatEventName.format(timeInMillis);
                        intent.putExtra("name", eventName);

                        intent.putExtra("id", mDBEvents.getLastId() + 1);

                        mDBEvents.insert(mDBEvents.getLastId() + 1, eventName, color, timeInMillis, -1, getContext());
                        getContext().sendBroadcast(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateEventDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }
}
