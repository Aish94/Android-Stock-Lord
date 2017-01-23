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
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class HighScores extends Fragment {
    TextView email1;
    TextView email2;
    TextView email3;
    TextView email4;
    TextView email5;
    TextView email6;
    TextView email7;
    TextView email8;
    TextView email9;
    TextView email10;

    TextView score1;
    TextView score2;
    TextView score3;
    TextView score4;
    TextView score5;
    TextView score6;
    TextView score7;
    TextView score8;
    TextView score9;
    TextView score10;
    Context context;
    GetData gd;
    String users[] = {"", "", "", "", "", "", "", "", "", ""};
    String networth[] = {"", "", "", "", "", "", "", "", "", ""};
    static Map<String,Float> price = new HashMap<String, Float>();
    String[] p = new String[1];
    int no = 0;

    public HighScores() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_high_scores, container, false);
        context = getActivity();
        gd = new GetData(context);

        email1 = (TextView) view.findViewById(R.id.email1);
        email2 = (TextView) view.findViewById(R.id.email2);
        email3 = (TextView) view.findViewById(R.id.email3);
        email4 = (TextView) view.findViewById(R.id.email4);
        email5 = (TextView) view.findViewById(R.id.email5);
        email6 = (TextView) view.findViewById(R.id.email6);
        email7 = (TextView) view.findViewById(R.id.email7);
        email8 = (TextView) view.findViewById(R.id.email8);
        email9 = (TextView) view.findViewById(R.id.email9);
        email10 = (TextView) view.findViewById(R.id.email10);

        score1 = (TextView) view.findViewById(R.id.score1);
        score2 = (TextView) view.findViewById(R.id.score2);
        score3 = (TextView) view.findViewById(R.id.score3);
        score4 = (TextView) view.findViewById(R.id.score4);
        score5 = (TextView) view.findViewById(R.id.score5);
        score6 = (TextView) view.findViewById(R.id.score6);
        score7 = (TextView) view.findViewById(R.id.score7);
        score8 = (TextView) view.findViewById(R.id.score8);
        score9 = (TextView) view.findViewById(R.id.score9);
        score10 = (TextView) view.findViewById(R.id.score10);

        if (gd.haveNetworkConnection()) {
            new Load().execute();
        } else {
            Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        return view;
    }

    public void getAllUsers()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TreeSet<User> topTenScores = new TreeSet<User>(new UserCompare());
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                {
                    Map<String,Object> userMap = (Map<String,Object>)userSnapshot.getValue();

                    float netWorth = calcNetWorth(Float.parseFloat(userMap.get("cash").toString()),(HashMap<String, Object>) userMap.get("company_stocks"));
                    User user = new User(userMap.get("name").toString(),netWorth);
                    if(topTenScores.size() < 10)
                        topTenScores.add(user);
                    else
                    {
                        User smallest_element = (User) topTenScores.first();
                        if(netWorth > smallest_element.score)
                        {
                            topTenScores.remove(topTenScores.first());
                            topTenScores.add(user);
                        }
                    }
                }

                int i = 0;
                for(User u: topTenScores)
                {
                    users[i] = u.name;
                    networth[i] = ""+u.score;
                    i++;
                }

                setUIElements();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class User
    {
        public String name;
        public double score;

        public User(String name,double score)
        {
            this.name = name;
            this.score = score;
        }
    }

    public class UserCompare implements Comparator<User>
    {
        public int compare(User user1,User user2)
        {
            if(user1.score > user2.score)
                return 1;
            return -1;
        }
    }

    public float calcNetWorth(float cash,HashMap<String,Object> stocks)
    {
        float netWorth = cash;

        for (Map.Entry<String, Object> entry : stocks.entrySet())
        {
            String company_abbr = entry.getKey();
            int num_of_stocks = Integer.parseInt(entry.getValue().toString());
            if(num_of_stocks > 0)
            {
                Float stock_price = price.get(company_abbr);
                netWorth += stock_price*num_of_stocks;
            }
        }

        return netWorth;
    }

    class Load extends AsyncTask<Void, Void, String> {
        //Background Process
        private ProgressDialog dialog;  //Loading dialog

        public Load() {
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            dialog.setMessage("Loading");   //Loading data dialog
            dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {

            String companies = "yhoo,goog,amzn,msft,aapl,fb,twtr,orcl,sndk,csco";
            String url = getResources().getString(R.string.apiquery1) + companies + getResources().getString(R.string.apiquery2);
            InputStream in = gd.connect(url);

            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                String b = "";
                String line;

                while ((line = bf.readLine()) != null) {
                    b += line;
                    //Read response
                }

                JSONObject obj = new JSONObject(b);
                JSONObject o = obj.getJSONObject("query");
                obj = o.getJSONObject("results");
                JSONArray arr = obj.getJSONArray("quote");
                for (int i = 0; i < 10; i++) {
                    o = arr.getJSONObject(i);
                    price.put(o.getString("symbol").toUpperCase(),Float.parseFloat(o.getString("LastTradePriceOnly")));
                    //price[i] = Float.parseFloat(o.getString("LastTradePriceOnly"));
                }

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String res) {   //Display results

            if (price.isEmpty())
            {
                Toast toast = Toast.makeText(context, "Price Server Error", Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                getAllUsers();

                if (dialog.isShowing())
                    dialog.dismiss();   //Dismiss loading


                }
            }
        }

        public void setUIElements()
        {
            email1.setText(users[0]);
            email2.setText(users[1]);
            email3.setText(users[2]);
            email4.setText(users[3]);
            email5.setText(users[4]);
            email6.setText(users[5]);
            email7.setText(users[6]);
            email8.setText(users[7]);
            email9.setText(users[8]);
            email10.setText(users[9]);
            score1.setText(networth[0]);
            score2.setText(networth[1]);
            score3.setText(networth[2]);
            score4.setText(networth[3]);
            score5.setText(networth[4]);
            score6.setText(networth[5]);
            score7.setText(networth[6]);
            score8.setText(networth[7]);
            score9.setText(networth[8]);
            score10.setText(networth[9]);
        }
        /*class NetWorth extends AsyncTask<String, Void, String> {

            //Background Process
            String email;
            int j;
            private ProgressDialog dialog;  //Loading dialog

            public NetWorth(String e, int index) {
                dialog = new ProgressDialog(context);
                email = e;
                j = index;
            }

            protected void onPreExecute() {
                dialog.setMessage("Loading");   //Loading data dialog
                dialog.show();
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("email", email);
                p[0] = gd.conv(params);
            }

            @Override
            protected String doInBackground(String... params) {
                String s = getResources().getString(R.string.UserDataURL);
                Log.d("checkParam", params[0]);
                String res = gd.post(s, params[0]);
                Log.d("checkResult", res);
                if (res.equals("failure")) {
                    Toast toast = Toast.makeText(context, "NetWorth Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    StringTokenizer st = new StringTokenizer(res, "|");
                    st.nextToken();
                    float cash = Float.parseFloat(st.nextToken());
                    float net = cash;
                    for (int i = 0; i < 10; i++) {
                        String num = st.nextToken();
                        if (!num.equals("0")) {
                            net += Integer.parseInt(num) * price[i];
                        }

                    }
                    networth[j] = "" + net;
                    Log.d("netindex", "" + j);
                }
                return null;
            }

            protected void onPostExecute(String params) {   //Display results
                if (dialog.isShowing())
                    dialog.dismiss();   //Dismiss loading
            }

        }*/

}