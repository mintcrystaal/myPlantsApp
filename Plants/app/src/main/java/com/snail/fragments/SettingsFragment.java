package com.snail.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.snail.plants.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.snail.fragments.MainActivity.DEFAULT_DAYS;
import static com.snail.fragments.MainActivity.DEFAULT_HOUR;
import static com.snail.fragments.MainActivity.DEFAULT_LANGUAGE;
import static com.snail.fragments.MainActivity.DEFAULT_MINUTE;
import static com.snail.fragments.MainActivity.DEFAULT_TIME_HOUR;
import static com.snail.fragments.MainActivity.DEFAULT_TIME_MINUTE;
import static com.snail.fragments.MainActivity.HOW_MANY_DAYS;

public class SettingsFragment extends Fragment {

    private int chosenDays = HOW_MANY_DAYS;
    private String language = "ru";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_settings, container,false);

        final Button chooseTime = mainView.findViewById(R.id.time);
        chooseTime.setText(getString(R.string.choose_default_time) + String.format(" (%02d:%02d)", DEFAULT_TIME_HOUR, DEFAULT_TIME_MINUTE));
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener () {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        DEFAULT_TIME_HOUR = hourOfDay;
                        DEFAULT_TIME_MINUTE = minute;
                        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString(DEFAULT_HOUR, Integer.toString(hourOfDay));
                        ed.putString(DEFAULT_MINUTE, Integer.toString(minute));
                        ed.apply();
                        chooseTime.setText(getString(R.string.choose_default_time) + String.format(" (%02d:%02d)", DEFAULT_TIME_HOUR, DEFAULT_TIME_MINUTE));
                    }
                };

                new TimePickerDialog(getActivity(), myCallBack,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true)
                        .show();
            }
        });

        final Button chooseLanguage = mainView.findViewById(R.id.language);
        chooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLanguageDialog().show();
            }
        });

        final Button chooseTheme = mainView.findViewById(R.id.theme);
        chooseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.soon_will_be), Toast.LENGTH_LONG).show();
            }
        });

        final Button chooseDays = mainView.findViewById(R.id.days);
        chooseDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDaysDialog().show();
            }
        });
        return mainView;
    }

    private Dialog chooseDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.days_dialog, null);

        Button add = dialogView.findViewById(R.id.plus);
        Button remove = dialogView.findViewById(R.id.minus);
        TextView textView = dialogView.findViewById(R.id.days_text_view);

        textView.setText("" + chosenDays);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (chosenDays == 60) {
                    Toast.makeText(getContext(), getString(R.string.maximum_is_60), Toast.LENGTH_LONG).show();
                }
                else {
                    chosenDays++;
                    textView.setText("" + chosenDays);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (chosenDays == 7) {
                    Toast.makeText(getContext(), getString(R.string.minimum_is_7), Toast.LENGTH_LONG).show();
                }
                else {
                    chosenDays--;
                    textView.setText("" + chosenDays);
                }
            }
        });

        builder.setPositiveButton(R.string.ok, dialogClickListener);
        builder.setNegativeButton(R.string.cancel, dialogClickListener);
        builder.setView(dialogView);
        return builder.create();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    HOW_MANY_DAYS = chosenDays;
                    SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(DEFAULT_DAYS, Integer.toString(HOW_MANY_DAYS));
                    ed.apply();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    chosenDays = HOW_MANY_DAYS;
                    break;
            }
        }
    };

    private Dialog chooseLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.choose_language_dialog, null);

        Button russian = dialogView.findViewById(R.id.russian);
        Button english = dialogView.findViewById(R.id.english);

        russian.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                language = "ru";
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                language = "en";
            }
        });

        builder.setPositiveButton(R.string.ok, changeLanguageListener);
        builder.setNegativeButton(R.string.cancel, changeLanguageListener);
        builder.setView(dialogView);
        return builder.create();
    }

    DialogInterface.OnClickListener changeLanguageListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    MainActivity.setLanguage(getContext(), language);
                    SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(DEFAULT_LANGUAGE, language);
                    ed.apply();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}