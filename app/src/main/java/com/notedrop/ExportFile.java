package com.notedrop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.notedrop.R;

import java.util.ArrayList;

public class ExportFile extends Activity {

    Button buttonSend;
    EditText textTo;
    EditText textSubject;
    EditText textMessage;
    String eMessage = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_file);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textTo = (EditText) findViewById(R.id.editTextTo);
        textSubject = (EditText) findViewById(R.id.editTextSubject);
        textMessage = (EditText) findViewById(R.id.editTextMessage);

        Intent in = getIntent();
        String title = in.getStringExtra("key");
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();
        int size = mSharedPreference1.getInt(title + "_size", 0);

        for(int i=0;i<size;i++)
        {
            String temp = mSharedPreference1.getString(title + "_line_" + i, null);
                eMessage +=  temp + "\n";
            //Log.v("Loading Notes " + i + ": ", notes.get(i));
            //mLayout.addView(createNewTextView(notes.get(i).toLowerCase(), i));
            //scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
        textMessage.setText(eMessage);
        Log.v("messaqe: ", textMessage.getText().toString());

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String to = textTo.getText().toString();
                String subject = textSubject.getText().toString();
                String message = textMessage.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
                //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
    }
}