package com.snail.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.snail.databases.DBEvents;
import com.snail.databases.DBMyPlants;
import com.snail.databases.DBMyPlantsUpdates;
import com.snail.databases.DBPlants;
import com.snail.databases.DBSensors;
import com.snail.objects.MyPlant;
import com.snail.objects.Plant;
import com.snail.dialogs.ChoosePlantDialog;
import com.snail.plants.R;
import com.snail.views.MyPlantView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.snail.fragments.MainActivity.DEFAULT_LANGUAGE;
import static com.snail.fragments.MainActivity.LANGUAGE;
import static java.lang.Math.min;

public class MyPlantsFragment extends Fragment {

    private MyPlantsFragment.myListAdapter myAdapter;
    private DBPlants mDBPlants;
    private DBMyPlants mDBMyPlants;
    private DBSensors mDBSensors;
    private long lastId;
    private MyBroadcastReceiver br;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_myplants, container,false);

        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
        LANGUAGE = sPref.getString(DEFAULT_LANGUAGE, "ru");
        MainActivity.setLanguage(getContext(), LANGUAGE);

        ListView mPlantsListView = mainView.findViewById(R.id.myplants_list);
        mDBPlants = new DBPlants(getContext());

        mDBMyPlants = new DBMyPlants((getContext()));
        mDBSensors = new DBSensors((getContext()));
        myAdapter = new MyPlantsFragment.myListAdapter(getContext(), mDBMyPlants.selectAll());

        mPlantsListView.setAdapter(myAdapter);
        registerForContextMenu(mPlantsListView);

        mPlantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyPlant mPlant = myAdapter.getPlantByPos(position);

                Intent intent = new Intent(getContext(), MyPlantView.class);
                intent.putExtra("id", mPlant.getId());
                startActivity(intent);
            }
        });

        mPlantsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                lastId = myAdapter.getRealId(pos);
                onCreateDeleteDialog().show();
                return true;
            }
        });

        FloatingActionButton addPlant = mainView.findViewById(R.id.add_plant);
        addPlant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogFragment choosePlant = new ChoosePlantDialog();
                choosePlant.show(getFragmentManager(), "choosePlant");
            }
        });

        br = new MyBroadcastReceiver();

        IntentFilter intFilt = new IntentFilter("plantAdded");
        getActivity().registerReceiver(br, intFilt);

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
                    MyPlant idConnection = mDBMyPlants.select(lastId);
                    Plant tempPlant = mDBPlants.select(idConnection.getIdAll());
                    tempPlant.removeMine();
                    mDBPlants.update(tempPlant);

                    mDBMyPlants.delete((int)lastId);
                    DBMyPlantsUpdates mDBPlantsUpdates = new DBMyPlantsUpdates(getContext());
                    mDBPlantsUpdates.delete((int)lastId);
                    DBEvents mDBEvents = new DBEvents(getContext());
                    mDBEvents.deleteByPlant(lastId, getContext());

                    myAdapter.change(mDBMyPlants.selectAll());
                    myAdapter.notifyDataSetChanged();
                    updateNotifications();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void updateNotifications() {
        DBEvents mDBEvents = new DBEvents(getContext());
        mDBEvents.deleteByPlant(lastId, getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(br);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                myAdapter.change(mDBMyPlants.selectAll());
                myAdapter.notifyDataSetChanged();
            }
            catch (Exception RuntimeException) {}
        }
    }


    class myListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MyPlant> arrayMyPlants;

        public myListAdapter (Context ctx, ArrayList<MyPlant> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public void change(ArrayList<MyPlant> newArr) {
            arrayMyPlants = newArr;
        }

        public long getRealId(int position) { return arrayMyPlants.get(position).getId();}

        public MyPlant getPlantByPos(int position) {
            return arrayMyPlants.get(position);
        }

        public void setArrayMyData(ArrayList<MyPlant> arrayMyData) {
            this.arrayMyPlants = arrayMyData;
        }

        public int getCount() {
            return arrayMyPlants.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId (int position) {
            MyPlant md = arrayMyPlants.get(position);
            if (md != null) {
                return md.getId();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.my_plant, null);

            TextView mName = convertView.findViewById(R.id.plant_name);
            ImageView mPicture = convertView.findViewById(R.id.plant_photo);
            TextView mFeels = convertView.findViewById(R.id.plant_feels);


            Plant md = mDBPlants.select(arrayMyPlants.get(position).getIdAll());
            mName.setText(md.getName());

            byte[] data = Base64.decode(md.getPicture(), Base64.DEFAULT);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap plantPic = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            int size = min(plantPic.getWidth(), plantPic.getHeight());
            Bitmap resizedPlantPic = Bitmap.createBitmap(plantPic, 0,0, size, size);
            mPicture.setImageBitmap(resizedPlantPic);

            if (arrayMyPlants.get(position).getFeels() == 1) {
                mFeels.setText(getString(R.string.check_plant));
                mFeels.setTextColor(getResources().getColor(R.color.pay_attention));
            }
            else
                if (mDBSensors.selectByPlant(arrayMyPlants.get(position).getId()).size() > 0) {
                    mFeels.setText(getString(R.string.plant_is_ok));
                    mFeels.setTextColor(getResources().getColor(R.color.feels_ok));
                }
            return convertView;
        }
    }
}