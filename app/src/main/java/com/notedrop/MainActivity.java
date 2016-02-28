package com.notedrop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
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
    private ArrayList<String> titles = new ArrayList<String>();
    private LinearLayout mLayout;
    private ScrollView scroll;
    private TextView notesLabel;
    private ImageButton send, list, save;
    private EditText title;
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (LinearLayout) findViewById(R.id.notes);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        scroll = (ScrollView) findViewById(R.id.scroller);
        notesLabel = (TextView) findViewById(R.id.noteslabel);

        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = mSharedPreference1.getStringSet("titles_list", null);
        if(set != null)
            titles = new ArrayList<String>(set);

        boolean x = isSpeechRecognitionActivityPresented(this);
        Log.v("speech recognizer: ", x + "");

        title = (EditText) findViewById(R.id.title);

        save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = saveArray(title.getText().toString(), MainActivity.this);
                Log.v("Saved?: ", saved + "");
            }
        });
        list = (ImageButton) findViewById(R.id.list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(MainActivity.this,
                        "List!", Toast.LENGTH_SHORT).show();*/
                //loadTitleArray(MainActivity.this);
                Intent myIntent = new Intent(MainActivity.this, NotesList.class);
                myIntent.putStringArrayListExtra("key", titles);

                MainActivity.this.startActivity(myIntent);
            }
        });

        send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("M1: ", "Heyy");
                Toast.makeText(MainActivity.this,
                        "Send!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this, ExportFile.class);
                myIntent.putExtra("key", title.getText().toString());
                Log.v("M2: ", "Booo");
                MainActivity.this.startActivity(myIntent);
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

        Intent i = getIntent();
        String selectedNote = i.getStringExtra("key");
        ArrayList<String> titles_list = i.getStringArrayListExtra("titles_list");
        if(selectedNote != null) {
            loadArray(MainActivity.this, selectedNote);
        }
        if(titles_list != null) {
            titles = titles_list;
        }
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
                    notes.add(result.get(0));
                    mLayout.addView(createNewTextView(result.get(0).toLowerCase(), notes.size()-1));
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

    public boolean saveArray(String title, Context mContext)
    {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);;
        SharedPreferences.Editor mEdit1 = sp.edit();
        int totalLists = mSharedPreference1.getInt("total_list_size", 0);
        if(titles.contains(title)) {
            Toast.makeText(MainActivity.this, "Title already exists, choose another title!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(title.equals("")) {
            Toast.makeText(MainActivity.this, "Note needs a title!", Toast.LENGTH_SHORT).show();
            return false;
        }
        titles.add(title);

        Set<String> set = new HashSet<String>();
        set.addAll(titles);
        mEdit1.remove("titles_list");
        mEdit1.putStringSet("titles_list", set);

        Toast.makeText(MainActivity.this, "Saving " + title, Toast.LENGTH_SHORT).show();

        Log.v("Saving: ", "title_" + (totalLists + 1));
        mEdit1.remove("total_list_size");
        mEdit1.putInt("total_list_size", (totalLists + 1));
        //mEdit1.putString("title_" + (totalLists + 1), title);

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH); // starts at 0 for January
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        String x = "AM";
        if(hour > 12) {
            hour = hour - 12;
            x = "PM";
        }
        else if(hour == 0)
            hour = 12;
        x = year + " " + months[month] + " " + day + " " + hour + ":" + min + x;
        //Toast.makeText(MainActivity.this,x, Toast.LENGTH_SHORT).show();

        mEdit1.remove(title + "_date");
        mEdit1.putString(title+ "_date", x);

        mEdit1.remove(title + "_size");
        mEdit1.putInt(title + "_size", notes.size()); /* sKey is an array */
        Log.v("Saving Notes Title: ", title);
        for (int i = 0; i < notes.size(); i++) {
            mEdit1.remove(title + "_line_" + i);
            mEdit1.putString(title + "_line_" + i, notes.get(i));
            Log.v("Saving Notes " + i + ": ", notes.get(i));
        }
        return mEdit1.commit();
    }

    public void loadArray(Context mContext, String titleString)
    {
        //Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
        title.setText(titleString);
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        notes.clear();
        int size = mSharedPreference1.getInt(titleString + "_size", 0);

        for(int i=0;i<size;i++)
        {
            notes.add(mSharedPreference1.getString(titleString + "_line_" + i, null));
            Log.v("Loading Notes " + i + ": ", notes.get(i));
            mLayout.addView(createNewTextView(notes.get(i).toLowerCase(), i));
            scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    public void loadTitleArray(Context mContext)
    {
        //Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();

        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        int size = mSharedPreference1.getInt("total_list_size", 0);
        Log.v("Load title array: ", "" + size);

        for(int i=0;i<titles.size();i++)
        {
            titles.add(mSharedPreference1.getString("title_" + i, null));
            Log.v("Title " + i, titles.get(i-1));
        }
    }

    private EditText createNewTextView(String text, final int position) {
        if (notesLabel.getVisibility() == View.VISIBLE)
            notesLabel.setVisibility(View.GONE);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lparams.setMargins(30, 15, 0, 0);
        EditText textView = new EditText(this);
        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        textView.setSingleLine(true);
        textView.setHorizontallyScrolling(false);
        textView.setMaxLines(Integer.MAX_VALUE);
        if(text.contains("new section")) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            text = text.replace("new section", "");
        }
        else {
            if(!text.contains(" - "))
                text = " - " + text;
        }
        textView.setTextSize(26);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notes.set(position, s.toString());
            }
        });
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