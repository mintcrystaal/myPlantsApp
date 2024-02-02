package com.snail.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.snail.plants.R;

import java.util.ArrayList;

public class FilterDialog extends DialogFragment {

    int temperature = 0;
    int watering = 0;
    int light = 0;
    int order = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        Spinner chooseTemperature = dialogView.findViewById(R.id.choose_temperature);
        final ArrayList<String> temperatures = new ArrayList<>();
        temperatures.add("-");
        for (int i = 5; i < 41; i += 6)
            temperatures.add(Integer.toString(i) + " - " + Integer.toString(i + 5) + "°C");

        final ArrayAdapter myAdapterTemperatures = new ArrayAdapter<>(getContext(),  R.layout.item_choose, R.id.this_item, temperatures);
        chooseTemperature.setAdapter(myAdapterTemperatures);

        chooseTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temperature = (int) id;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner chooseLight = dialogView.findViewById(R.id.choose_light);
        final ArrayList<String> lights = new ArrayList<>();
        lights.add("-");
        lights.add(getString(R.string.light1));
        lights.add(getString(R.string.light2));
        lights.add(getString(R.string.light3));

        final ArrayAdapter myAdapterLights = new ArrayAdapter<>(getContext(),  R.layout.item_choose, R.id.this_item, lights);
        chooseLight.setAdapter(myAdapterLights);

        chooseLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                light = (int) id - 1;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });


        Spinner chooseWatering = dialogView.findViewById(R.id.choose_watering);
        final ArrayList<String> waterings = new ArrayList<>();
        waterings.add("-");
        waterings.add(getString(R.string.not_needed));
        for (int i = 1; i < 22; i += 3)
            waterings.add(Integer.toString(i) + " - " + Integer.toString(i + 2));

        final ArrayAdapter myAdapterWaterings = new ArrayAdapter<>(getContext(),  R.layout.item_choose, R.id.this_item, waterings);
        chooseWatering.setAdapter(myAdapterWaterings);

        chooseWatering.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                watering = (int) id;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner orderBy = dialogView.findViewById(R.id.choose_order);
        final ArrayList<String> orders = new ArrayList<>();
        orders.add(getString(R.string.by_date));
        orders.add(getString(R.string.by_name));
        orders.add(getString(R.string.by_temperature));
        orders.add(getString(R.string.by_light));
        orders.add(getString(R.string.by_watering));
        orders.add(getString(R.string.by_humidity));
        orders.add(getString(R.string.by_amount_mine));

        final String ordersColumns[] = {"id", "Название", "Температура", "Освещение", "Полив", "Влажность", "Мои"};

        final ArrayAdapter myAdapterOrders = new ArrayAdapter<>(getContext(),  R.layout.item_choose, R.id.this_item, orders);
        orderBy.setAdapter(myAdapterOrders);

        orderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order = (int) id;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });


        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent("filter");

                        String selection = "";
                        ArrayList<String> selectionArgs = new ArrayList<>();

                        if (temperature != 0) {
                            selection += "Температура >= ? AND Температура < ? ";
                            selectionArgs.add(Integer.toString(5 + (temperature - 1) * 6));
                            selectionArgs.add(Integer.toString(5 + temperature * 6));
                        }

                        if (watering != 0 && watering != 1) {
                            if (selection.length() != 0)
                                selection += " AND ";
                            selection += "Полив >= ? AND Полив < ? ";
                            watering--;
                            selectionArgs.add(Integer.toString( 1 + (watering - 1) * 3));
                            selectionArgs.add(Integer.toString(1 + watering * 3));
                        }
                        else {
                            if (watering == 1) {
                                if (selection.length() != 0)
                                    selection += " AND ";
                                selection += "Полив = ? ";
                                selectionArgs.add("0");
                            }
                        }

                        if (light != -1) {
                            if (selection.length() != 0)
                                selection += " AND ";
                            selection += "Освещение = ?";
                            selectionArgs.add(Integer.toString(light));
                        }

                        if (selection.length() != 0) {
                            intent.putExtra("selection", selection);
                            String[] arr = new String[selectionArgs.size()];
                            intent.putExtra("selectionArgs", selectionArgs.toArray(arr));
                        }

                        intent.putExtra("order", ordersColumns[order]);
                        getContext().sendBroadcast(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FilterDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }
}
