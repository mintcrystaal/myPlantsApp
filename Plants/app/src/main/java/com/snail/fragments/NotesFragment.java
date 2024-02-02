package com.snail.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.snail.databases.DBNotes;
import com.snail.objects.Note;
import com.snail.plants.R;
import com.snail.views.NoteView;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private myListAdapter myAdapter;
    private int lastId;

    DBNotes mDBNotes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View mainView = inflater.inflate(R.layout.fragment_notes, container, false);

            FloatingActionButton add = mainView.findViewById(R.id.add_note_button);
            ListView mNotesListView = mainView.findViewById(R.id.notes_list);
            mDBNotes = new DBNotes(getContext());

            myAdapter = new myListAdapter(getContext(), mDBNotes.selectAll());
            mNotesListView.setAdapter(myAdapter);
            registerForContextMenu(mNotesListView);

            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), NoteView.class);
                    intent.putExtra("text", "");
                    intent.putExtra("name", "");
                    startActivityForResult(intent, 1);

                }
            });

        mNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), NoteView.class);
                intent.putExtra("text", mDBNotes.select(id).getText());
                intent.putExtra("name", mDBNotes.select(id).getName());
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);
            }
        });

        mNotesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                lastId = (int) id;
                onCreateDialog().show();
                return true;
            }
        });

        return mainView;
    }

    private Dialog onCreateDialog() {
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
                    mDBNotes.delete(lastId);
                    myAdapter.change(mDBNotes.selectAll());
                    myAdapter.notifyDataSetChanged();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data.getBooleanExtra("upd", false)) {
                myAdapter.change(mDBNotes.selectAll());
                myAdapter.notifyDataSetChanged();
            }
        } catch (Exception RuntimeException) {}
    }


    class myListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Note> arrayMyNotes;

        public myListAdapter (Context ctx, ArrayList<Note> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public void change(ArrayList<Note> newArr) {
            arrayMyNotes = newArr;
        }

        public ArrayList<Note> getArrayMyData() {
            return arrayMyNotes;
        }

        public void setArrayMyData(ArrayList<Note> arrayMyData) {
            this.arrayMyNotes = arrayMyData;
        }

        public int getCount() {
            return arrayMyNotes.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId (int position) {
            Note md = arrayMyNotes.get(position);
            if (md != null) {
                return md.getId();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.note, null);

            TextView mName = convertView.findViewById(R.id.note_name);

            Note md = arrayMyNotes.get(position);
            mName.setText(md.getName());

            return convertView;
        }
    }
}