package com.snail.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.snail.databases.DBNotes;
import com.snail.plants.R;

public class NoteView extends AppCompatActivity {

    private EditText mNameView;
    private EditText mTextView;
    private long id;
    private String mName;
    private String mText;
    private Button okNote;
    private Button cancelNote;
    private boolean newNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);

        Intent intent = getIntent();
        mName = intent.getStringExtra("name");
        if (mName.length() == 0)
            newNote = true;
        id = intent.getLongExtra("id", new DBNotes(getApplicationContext()).getLastId() + 1);
        mText = intent.getStringExtra("text");


        mNameView = findViewById(R.id.note_name);
        mTextView = findViewById(R.id.note_text);
        okNote = findViewById(R.id.ok_note);
        cancelNote = findViewById(R.id.cancel_note);

        mNameView.setText(mName);
        mTextView.setText(mText);

        mNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mName = mNameView.getText().toString();
            }
        });

        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mText = mTextView.getText().toString();
            }
        });


        okNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mName.length() == 0)
                    mName = "Заметка " + id;
                DBNotes mDBNotes = new DBNotes(getApplicationContext());

                if (newNote)
                    mDBNotes.insert(id, mName, mText);
                else {
                    mDBNotes.update(id, mName, mText);
                }
                Intent intent = new Intent();
                intent.putExtra("upd", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("add", -1);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

}
