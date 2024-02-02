package com.snail.dialogs;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.snail.databases.DBEvents;
import com.snail.databases.DBMyPlants;
import com.snail.databases.DBMyPlantsUpdates;
import com.snail.databases.DBPlants;
import com.snail.objects.Plant;
import com.snail.plants.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.snail.fragments.MainActivity.DEFAULT_TIME_HOUR;
import static com.snail.fragments.MainActivity.DEFAULT_TIME_MINUTE;
import static com.snail.fragments.MainActivity.HOW_MANY_DAYS;


public class ChoosePlantDialog extends DialogFragment {

    DBPlants mDBPlants;
    ListView myPlantsList;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.choose_plant_dialog, null);

        mDBPlants = new DBPlants(getContext());

        myPlantsList = dialogView.findViewById(R.id.plants_list);
        myPlantsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final ArrayList<Plant> arr = mDBPlants.selectAll();

        arr.add(new Plant(-1, getString(R.string.create_your_own), "", -1, -1, -1, -1, -1, -1, ""));

        final ChoosePlantDialog.MyListAdapter myAdapter = new ChoosePlantDialog.MyListAdapter(getContext(), arr);
        myPlantsList.setAdapter(myAdapter);

        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int chosenId = myAdapter.selectedPosition;
                        if (chosenId == -1) {
                            ChoosePlantDialog.this.getDialog().cancel();
                        }
                        else {
                            if (chosenId == arr.size() - 1) {
                                Intent intent = new Intent(getContext(), CreatePlantActivity.class);
                                startActivity(intent);
                                ChoosePlantDialog.this.getDialog().cancel();
                            } else {
                                chosenId = (int) arr.get(chosenId).getId();
                                Plant mPlant = mDBPlants.select(chosenId);
                                mPlant.addMine();
                                mDBPlants.update(mPlant);

                                DBMyPlants mDBMyPlants = new DBMyPlants(getContext());
                                mDBMyPlants.insert(mDBMyPlants.getLastId() + 1, chosenId, 0);

                                DBEvents mDBEvents = new DBEvents(getContext());
                                Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
                                currentCalender.set(Calendar.HOUR_OF_DAY, DEFAULT_TIME_HOUR);
                                currentCalender.set(Calendar.MINUTE, DEFAULT_TIME_MINUTE);
                                currentCalender.set(Calendar.SECOND, 0);
                                long timeInMillis = currentCalender.getTimeInMillis();

                                DBMyPlantsUpdates mDBUpdates = new DBMyPlantsUpdates(getContext());

                                long maxtimeWater = timeInMillis, maxtimeFert = timeInMillis;
                                SimpleDateFormat dateFormatEventName = new SimpleDateFormat("(HH:mm)", Locale.getDefault());
                                String eventName = getString(R.string.of_plant) + " \"" + mPlant.getName() + "\" " + dateFormatEventName.format(timeInMillis);

                                if (mPlant.getFertilize() > 0)
                                    maxtimeWater = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_fertilize) + " " + eventName, getActivity().getColor(R.color.fertilize_day), timeInMillis, mDBMyPlants.getLastId(), mPlant.getFertilize(), timeInMillis + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getContext());

                                if (mPlant.getWatering() > 0)
                                    maxtimeFert = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_watering) + " " + eventName, getActivity().getColor(R.color.watering_day), timeInMillis, mDBMyPlants.getLastId(), mPlant.getWatering(), timeInMillis + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getContext());

                                mDBUpdates.insert(mDBMyPlants.getLastId(), maxtimeWater, maxtimeFert);

                                ChoosePlantDialog.this.getDialog().cancel();
                            }
                            Intent intent = new Intent("plantAdded");
                            getContext().sendBroadcast(intent);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChoosePlantDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }


    class MyListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Plant> arrayMyPlants;
        private int selectedPosition = -1;

        public MyListAdapter (Context ctx, ArrayList<Plant> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public void setArrayMyData(ArrayList<Plant> arrayMyData) {
            this.arrayMyPlants = arrayMyData;
        }

        public int getCount() {
            return arrayMyPlants.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId (int position) {
            Plant md = arrayMyPlants.get(position);
            if (md != null) {
                return md.getId();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.plant_choise, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder = new ViewHolder(convertView);

            holder.radioButton.setTag(position);

            holder.plantName.setText(arrayMyPlants.get(position).getName());

            if (position == selectedPosition) {
                holder.radioButton.setChecked(true);
            } else holder.radioButton.setChecked(false);

            holder.radioButton.setOnClickListener(onStateChangedListener(holder.radioButton, position));

            return convertView;
        }

        private View.OnClickListener onStateChangedListener(final RadioButton radioButton, final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (radioButton.isChecked()) {
                        selectedPosition = position;
                    } else {
                        selectedPosition = -1;
                    }
                    notifyDataSetChanged();
                }
            };
        }

        private class ViewHolder {
            private TextView plantName;
            private RadioButton radioButton;

            public ViewHolder(View view) {
                radioButton = view.findViewById(R.id.radioButton);
                plantName = view.findViewById(R.id.plant_name);
            }
        }
    }

}
