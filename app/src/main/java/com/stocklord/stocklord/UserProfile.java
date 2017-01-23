package com.stocklord.stocklord;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;


public class UserProfile extends Fragment {

    TextView welcomeMsg;
    TextView cash;
    TextView netWorth;
    TextView stocks;
    ListView stocks_list;

    Context context;
    GetData gd;

    public UserProfile()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        context = getActivity();
        gd = new GetData(context);

        welcomeMsg = (TextView) view.findViewById(R.id.textView_welcome);
        cash = (TextView) view.findViewById(R.id.textView_cash);
        netWorth = (TextView) view.findViewById(R.id.textView_networth);
        stocks = (TextView) view.findViewById(R.id.textView_stocksown);
        stocks_list = (ListView) view.findViewById(R.id.listView_stocksown);


        if(gd.haveNetworkConnection())
        {
            new GetProfileData().execute(); //get stock price data
        }
        else
        {
            Toast toast = Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        return view;
    }

    public void GetUserData(Float[] prices)
    {
        final Float[] stock_prices = prices;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userID = db.child(auth.getCurrentUser().getUid());

        userID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> user;
                user = (Map<String,Object>)dataSnapshot.getValue();
                setUIElements(user,stock_prices);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database error",databaseError.toException());
                Toast toast = Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void setUIElements(Map<String,Object> user,Float[] prices)
    {
        welcomeMsg.setText("Weclome "+user.get("name"));
        cash.setText("Cash Left: "+user.get("cash")+"$");
        HashMap company_stocks = (HashMap) user.get("company_stocks");
        ArrayList<String> ustocks_list = new ArrayList<>();
        float net = 0;
        int count = 0;
        net = Float.parseFloat(user.get("cash").toString());
        for(int i =0;i<10;i++)
        {
            String company_name = getResources().getStringArray(R.array.companies)[i];
            String company_abbr = getResources().getStringArray(R.array.symbols)[i];
            int num_of_stocks = Integer.valueOf(company_stocks.get(company_abbr).toString());
            if(num_of_stocks != 0)
            {
                ustocks_list.add(company_name + " : " + num_of_stocks);
                count++;
                net += num_of_stocks*prices[i];
            }

        }
        netWorth.setText("Net Worth: "+net+"$");
        stocks.setText("You have stocks in "+count+" companies");
        stocks_list.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, ustocks_list));
    }

    class GetProfileData extends AsyncTask<Void, Void, Float[]> {
        //Background Process
        private ProgressDialog dialog;  //Loading dialog

        public GetProfileData() {
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute()
        {
            dialog.setMessage("Loading");   //Loading data dialog
            dialog.show();
        }


        @Override
        protected Float[] doInBackground(Void... params)
        {
            Float price[] = new Float[getResources().getStringArray(R.array.companies).length];
            String companies = "yhoo,goog,amzn,msft,aapl,fb,twtr,orcl,sndk,csco";
            String url = getResources().getString(R.string.apiquery1)+companies+getResources().getString(R.string.apiquery2);
            InputStream in = gd.connect(url);
            try{
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
                JSONArray arr= obj.getJSONArray("quote");
                for(int i = 0;i<10;i++) {
                    o = arr.getJSONObject(i);
                    price[i] = Float.parseFloat(o.getString("LastTradePriceOnly"));
                }
                return price;

            }
            catch(Exception e)
            {
                return null;
            }


        }

        protected void onPostExecute(Float[] params)
        {   //Display results
            if (dialog.isShowing())
                dialog.dismiss();   //Dismiss loading
            if(params!=null)
            {
                GetUserData(params);
            }
            else
            {
                Toast toast = Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT);
                toast.show();
            }
        }


    }


}
