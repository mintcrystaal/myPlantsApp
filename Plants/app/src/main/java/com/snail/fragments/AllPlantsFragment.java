package com.snail.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.snail.databases.DBPlants;
import com.snail.objects.Plant;
import com.snail.dialogs.FilterDialog;
import com.snail.plants.R;
import com.snail.views.PlantView;

import java.util.ArrayList;

import static java.lang.Math.min;

public class AllPlantsFragment extends Fragment implements SearchView.OnQueryTextListener {

    ListView mAllPlantsListView;
    myListAdapter myAdapter;
    SearchView mSearchView;
    DBPlants mDBPlants;
    MyBroadcastReceiverFilter br;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_allplants, container, false);

        mAllPlantsListView = mainView.findViewById(R.id.allplants_list);
        ImageButton filter = mainView.findViewById(R.id.filter);

        br = new MyBroadcastReceiverFilter();

        IntentFilter intFilt = new IntentFilter("filter");
        getActivity().registerReceiver(br, intFilt);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment filter = new FilterDialog();
                filter.show(getFragmentManager(), "filter");
            }
        });

        mSearchView = mainView.findViewById(R.id.search_plant);
        mSearchView.setOnQueryTextListener(this);

        mDBPlants = new DBPlants(getContext());

        myAdapter = new myListAdapter(getContext(), mDBPlants.selectAll());
        mAllPlantsListView.setAdapter(myAdapter);
        registerForContextMenu(mAllPlantsListView);

        mAllPlantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plant mPlant = myAdapter.getPlantByPos(position);

                Intent intent = new Intent(getContext(), PlantView.class);
                intent.putExtra("id", mPlant.getId());
                startActivity(intent);
            }
        });

        return mainView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myAdapter.filterName(newText);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(br);
    }

    public class MyBroadcastReceiverFilter extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String order = intent.getStringExtra("order");
            String selection = intent.getStringExtra("selection");
            String[] selectionArgs = intent.getStringArrayExtra("selectionArgs");

            ArrayList<Plant> filteredPl;
            filteredPl = mDBPlants.filter(selection, selectionArgs, order);

            myAdapter.setArrayMyData(filteredPl);
            myAdapter.notifyDataSetChanged();
        }
    }

    class myListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Plant> arrayAllPlants;
        private ArrayList<Plant> arrayAllPlantsShowing;

        public myListAdapter (Context ctx, ArrayList<Plant> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<Plant> getArrayMyData() {
            return arrayAllPlants;
        }

        public Plant getPlantByPos(int position) {
            return arrayAllPlantsShowing.get(position);
        }

        public void setArrayMyData(ArrayList<Plant> arrayMyData) {
            this.arrayAllPlants = arrayMyData;
            this.arrayAllPlantsShowing = arrayMyData;
        }

        public int getCount() {
            return arrayAllPlantsShowing.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId (int position) {
            Plant md = arrayAllPlantsShowing.get(position);
            if (md != null) {
                return md.getId();
            }
            return 0;
        }

        public void filterName(String beginning) {
            ArrayList<Plant> filteredArrayPlants = new ArrayList<Plant>();
            int size = arrayAllPlants.size();
            for (int i = 0; i < size; i++) {
                String name = (arrayAllPlants.get(i)).getName();
                if (name.toLowerCase().startsWith(beginning))  {
                    filteredArrayPlants.add(arrayAllPlants.get(i));
                }
            }
            arrayAllPlantsShowing = filteredArrayPlants;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.plant, null);

            TextView mName = convertView.findViewById(R.id.plant_name);
            ImageView mPicture = convertView.findViewById(R.id.plant_photo);

            Plant md = arrayAllPlantsShowing.get(position);
            mName.setText(md.getName());

            byte[] data = Base64.decode(md.getPicture(), Base64.DEFAULT);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap plantPic = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            int size = min(plantPic.getWidth(), plantPic.getHeight());
            Bitmap resizedPlantPic = Bitmap.createBitmap(plantPic, 0,0, size, size);
            mPicture.setImageBitmap(resizedPlantPic);

            return convertView;
        }
    }
}