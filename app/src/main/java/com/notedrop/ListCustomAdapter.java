package com.notedrop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;


    public ListCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_view, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = position;
                String itemValue = (String) list.get(position);
                /*Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();*/
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.putExtra("key", itemValue);

                context.startActivity(myIntent);
            }
        });

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                //or some other task
                SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);;
                SharedPreferences.Editor mEdit1 = sp.edit();
                int totalLists = mSharedPreference1.getInt("total_list_size", 0);
                mEdit1.remove("total_list_size");
                mEdit1.putInt("total_list_size", (totalLists - 1));
                mEdit1.remove("title_" + position);
                int size = mSharedPreference1.getInt(list.get(position) + "_size", 0);
                mEdit1.remove(list.get(position) + "_size");
                for (int i = 0; i < size; i++) {
                    mEdit1.remove(list.get(position) + "_line_" + i);
                }
                mEdit1.commit();
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}