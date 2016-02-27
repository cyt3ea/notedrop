package com.notedrop;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class NotesList extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        listView = (ListView) findViewById(R.id.list);
        Intent i = getIntent();
        ArrayList<String> titles = i.getStringArrayListExtra("key");
        String[] titlesArr = new String[titles.size()];
        titlesArr = titles.toArray(titlesArr);
        /*String[] values = new String[]{
          "Chicken Fried Rice",
          "Lo mein with beef",
          "Pad Thai",
          "Candace",
          "Sharon",
          "General Tsou",
          "Priya",
          "Cup Noodles"
        };*/

        ListCustomAdapter adapter = new ListCustomAdapter(titles, this);
        listView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
