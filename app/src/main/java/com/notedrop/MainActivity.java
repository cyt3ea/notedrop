package com.notedrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.notedrop.R;

public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayList<String> notes = new ArrayList<String>();
    private LinearLayout mLayout;
    private ScrollView scroll;
    private TextView notesLabel;
    private ImageButton send, list, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (LinearLayout) findViewById(R.id.notes);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        scroll = (ScrollView) findViewById(R.id.scroller);
        notesLabel = (TextView) findViewById(R.id.noteslabel);

        boolean x = isSpeechRecognitionActivityPresented(this);
        Log.v("speech recognizer: ", x + "");

        loadArray(MainActivity.this);

        save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = saveArray();
                Log.v("Saved?: ", saved + "");
            }
        });
        list = (ImageButton) findViewById(R.id.list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "List!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this, NotesList.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Send!", Toast.LENGTH_SHORT).show();
            }
        });

        // hide the action bar
        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        //TEST TO ADD TEXT ON BUTTON CLICK
        mLayout.addView(createNewTextView("HI!"));
        notes.add("HI!");
        scroll.fullScroll(ScrollView.FOCUS_DOWN);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    Log.d("GOT RESULT:", "");
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("Result: ", result.get(0));
                    mLayout.addView(createNewTextView(result.get(0)));
                    scroll.fullScroll(ScrollView.FOCUS_DOWN);
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean saveArray()
    {
        Toast.makeText(MainActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);;
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("notes_size", notes.size()); /* sKey is an array */

        for(int i=0;i<notes.size();i++)
        {
            mEdit1.remove("line_" + i);
            mEdit1.putString("line_" + i, notes.get(i));
        }

        return mEdit1.commit();
    }

    public void loadArray(Context mContext)
    {
        Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();

        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        notes.clear();
        int size = mSharedPreference1.getInt("notes_size", 0);

        for(int i=0;i<size;i++)
        {
            notes.add(mSharedPreference1.getString("line_" + i, null));
            mLayout.addView(createNewTextView(notes.get(i)));
            scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private TextView createNewTextView(String text) {
        if (notesLabel.getVisibility() == View.VISIBLE)
            notesLabel.setVisibility(View.GONE);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lparams.setMargins(30, 15, 0, 0);
        TextView textView = new TextView(this);
        textView.setTextSize(26);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        return textView;
    }

    public static boolean isSpeechRecognitionActivityPresented(Activity callerActivity) {
       try {
           PackageManager pm = callerActivity.getPackageManager();
           List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
           if (activities.size() != 0) {
               return true;
           }

       }       catch (Exception e) {


       }
        return false;
    }

}