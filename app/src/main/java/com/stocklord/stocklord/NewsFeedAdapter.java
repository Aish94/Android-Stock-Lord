package com.stocklord.stocklord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aishwarya on 11/19/2015.
 */
public class NewsFeedAdapter extends BaseAdapter
{
    LayoutInflater inflater;
    Context context;
    ArrayList<HashMap<String,String>> data;
    NewsFeedAdapter(Context context, ArrayList<HashMap<String,String>> data)
    {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView; //recycle object
        if(row == null)
        {
            //first time
            row = inflater.inflate(R.layout.news_feed_item, parent, false);
        }
        TextView title = (TextView)row.findViewById(R.id.textView7);
        TextView date = (TextView)row.findViewById(R.id.textView5);
        TextView desc = (TextView)row.findViewById(R.id.textView6);

        HashMap<String,String> currMap = data.get(position);
        title.setText(currMap.get("title"));
        date.setText(currMap.get("date"));
        desc.setText(currMap.get("desc"));

        return row;
    }

}


