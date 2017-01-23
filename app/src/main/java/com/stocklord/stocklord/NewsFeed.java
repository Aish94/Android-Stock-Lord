package com.stocklord.stocklord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsFeed extends Fragment {

    ListView articles;
    Context context;
    GetData gd;
    public NewsFeed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        context = getActivity();
        gd = new GetData(context);
        articles = (ListView)view.findViewById(R.id.listView_newsfeed);
        String p[] = new String[1];
        String url = getResources().getString(R.string.newsfeedURL);
        if(gd.haveNetworkConnection()) {
            p[0] = url;
            new DownloadTask().execute(p);
        }
        else
        {
            Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
        }

        return view;
    }

    class DownloadTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>>
    {
        ArrayList<HashMap<String,String>> res;
        private ProgressDialog dialog;  //Loading dialog
        public DownloadTask()
        {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Loading");   //Loading data dialog
            dialog.show();
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... params)
        {
            res = Parse(params[0]);
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> arr) {
            res = arr;
            if (dialog.isShowing())
                dialog.dismiss();   //Dismiss loading
            if (res == null) {
                Toast toast = Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                articles.setAdapter(new NewsFeedAdapter(context, res));
                articles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(res.get(position).get("link")));
                        startActivity(browserIntent);
                    }
                });
            }
        }
    }

    public ArrayList<HashMap<String,String>> Parse(String url)
    {
        try {
            ArrayList<HashMap<String, String>> arr = new ArrayList<>();
            RSSReader reader = new RSSReader();
            RSSFeed feed = reader.load(url);
            List<RSSItem> list = feed.getItems();
            for (RSSItem i : list) {
                HashMap<String, String> item = new HashMap<>();
                item.put("title", i.getTitle());//title content
                item.put("desc", i.getDescription());//description content
                item.put("date",i.getPubDate().toString());
                item.put("link", i.getLink().toString());//link
                arr.add(item);
            }
            return arr;
        }
        catch(Exception e)
        {
            Log.d("ServerError",""+e);
            return null;
        }
    }


}
