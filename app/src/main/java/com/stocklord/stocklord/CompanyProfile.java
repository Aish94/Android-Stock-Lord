package com.stocklord.stocklord;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CompanyProfile extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    Button buy;
    Button sell;

    ImageButton back;    //Back Button
    ImageView graph;              //Graph
    Bitmap bmp;
    Spinner spin;                 //Choice of Timeline
    TextView c1;                //Table cells
    TextView c2;
    TextView c3;
    TextView c4;
    TextView c5;
    TextView c6;
    TextView c7;
    TextView c8;
    TextView c9;
    TextView c10;
    TextView c11;
    TextView title;
    Dialog pick;
    String url;
    String url_graph;
    String list[] = {"1 day", "5 days", "3 months", "6 months", "1 year", "2 year", "5 year", "Maximum"};      //Timeline
    String time[] = {"1d", "5d", "3m", "6m", "1y", "2y", "5y", "my"};
    String det[] = new String[12];
    String p[]=new String[4];
    Context context;
    boolean flag;
    int position;
    GetData gd;
    static int num;
    static float cash;
    float price;
    String res;
    String email;
    User user;
    String company_abbr;
    FirebaseAuth auth;
    DatabaseReference db;

    public CompanyProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid());

        Bundle b = getArguments();
        position = b.getInt("company_id");
        company_abbr = getResources().getStringArray(R.array.symbols)[position];
        url = getResources().getString(R.string.apiquery1)+company_abbr+getResources().getString(R.string.apiquery2);
        url_graph = getResources().getString(R.string.graphapiquery1)+getResources().getStringArray(R.array.symbols)[position]+getResources().getString(R.string.graphapiquery2)+time[0]+getResources().getString(R.string.graphapiquery3);
        context = getActivity();
        gd = new GetData(context);

        flag = true;
        //Initialize all view objects
        title = (TextView) view.findViewById(R.id.textView_companyName);
        buy = (Button) view.findViewById(R.id.button_buy);
        sell = (Button) view.findViewById(R.id.button_sell);
        graph = (ImageView) view.findViewById(R.id.imageView_graph);
        pick = new Dialog(context);
        spin = (Spinner) view.findViewById(R.id.spinner_timeline);
        back = (ImageButton) view.findViewById(R.id.BackButton);
        buy.setOnClickListener(this);
        sell.setOnClickListener(this);
        back.setOnClickListener(this);

        c1 = (TextView) view.findViewById(R.id.cell1);
        c2 = (TextView) view.findViewById(R.id.cell2);
        c3 = (TextView) view.findViewById(R.id.cell3);
        c4 = (TextView) view.findViewById(R.id.cell4);
        c5 = (TextView) view.findViewById(R.id.cell5);
        c6 = (TextView) view.findViewById(R.id.cell6);
        c7 = (TextView) view.findViewById(R.id.cell7);
        c8 = (TextView) view.findViewById(R.id.cell8);
        c9 = (TextView) view.findViewById(R.id.cell9);
        c10 = (TextView) view.findViewById(R.id.cell10);
        c11 = (TextView) view.findViewById(R.id.cell11);

        // Create an ArrayAdapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
        if(gd.haveNetworkConnection())
        {
            GetUserData();
            p[0] = url;
            p[1] = url_graph;
            new CompanyData().execute(p);
        }
        else
        {
            Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
        }

        pick.setTitle("NumberPicker");
        pick.setContentView(R.layout.buy_dialog);
        // Inflate the layout for this fragment
        return view;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //An item was selected from the Spinner
        url_graph = getResources().getString(R.string.graphapiquery1)+getResources().getStringArray(R.array.symbols)[position]+getResources().getString(R.string.graphapiquery2)+time[pos]+getResources().getString(R.string.graphapiquery3);
         p[1] = url_graph;//Extract the selected item
        new GraphData().execute(p);// Reload the graph
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //@Override
    public void onClick(View v) {


        if (v.getId() == R.id.BackButton) {
            //Back to list of companies fragment
            Fragment newFragment = new CompanyList();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.container, newFragment);
            // Commit the transaction
            transaction.commit();
        }
        else
        {
            Button cancel = (Button) pick.findViewById(R.id.button_cancel);
            Button buysell = (Button) pick.findViewById(R.id.button_buysell);
            final NumberPicker np = (NumberPicker) pick.findViewById(R.id.numberPicker1);
            np.setMinValue(1);
            np.setWrapSelectorWheel(false);

            if (v.getId() == R.id.button_buy)
            {
                //Buy
                buysell.setText("Buy");

                int max = (int)Math.floor(cash/price);
                np.setMaxValue(max);
                buysell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        num += np.getValue();
                        cash -= np.getValue() * price;
                        buy(num, cash);
                        pick.dismiss();
                        if(cash<price)
                            buy.setEnabled(false);
                        sell.setEnabled(true);
                    }
                });

            }
            else if (v.getId() == R.id.button_sell)
            {
                //Sell
                buysell.setText("Sell");
                np.setMaxValue(num);
                buysell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        num -= np.getValue();
                        cash += np.getValue() * price;
                        sell(num,cash);
                        pick.dismiss();
                        if(cash>price)
                            buy.setEnabled(true);
                        if(num == 0)
                            sell.setEnabled(false);
                    }
                });

            }
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pick.dismiss();
                }
            });

            pick.show();
        }
    }

    public void buy(int num_of_stocks,float cash)
    {
        db.child("company_stocks").child(company_abbr).setValue(num_of_stocks);
        db.child("cash").setValue(cash);
        Toast toast = Toast.makeText(context, "Bought!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sell(int num_of_stocks,float cash)
    {
        db.child("company_stocks").child(company_abbr).setValue(num_of_stocks);
        db.child("cash").setValue(cash);
        Toast toast = Toast.makeText(context, "Sold!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setUIElements()
    {
        title.setText(det[11]); //Heading - company name

        c1.setText(det[0]); //Table data
        c2.setText(det[1]);
        c3.setText(det[2]);
        c4.setText(det[3]);
        c5.setText(det[4]);
        c6.setText(det[5]);
        c7.setText(det[6]);
        c8.setText(det[7]);
        c9.setText(det[8]);
        c10.setText(det[9]);
        c11.setText(det[10]);

        price = Float.parseFloat(det[8]);
        Log.d("Cash",""+cash);
        Log.d("Num",""+num);
        if(cash<price)
            buy.setEnabled(false);
        if(num == 0)
            sell.setEnabled(false);
    }

    public void GetUserData()
    {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object>  user = (Map<String,Object>)dataSnapshot.getValue();
                cash = Float.parseFloat(user.get("cash").toString());
                num =  Integer.parseInt(((HashMap)user.get("company_stocks")).get(company_abbr).toString());
                Log.d("CashatUser",""+cash);
                Log.d("NumatUser",""+num);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database error",databaseError.toException());
                Toast toast = Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    class CompanyData extends AsyncTask<String,Void,Void> {
        //Background Process
        private ProgressDialog dialog;  //Loading dialog
        public CompanyData()
        {
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute()
        {
            dialog.setMessage("Loading");   //Loading data dialog
            dialog.show();
        }


        @Override
        protected Void doInBackground(String... params)
        {
            //Retrieve data to display
            InputStream in = gd.connect(params[0]);
            parseData(in);
            return null;
        }

        protected void onPostExecute(Void params)
        {   //Display results
            if(dialog.isShowing())
                dialog.dismiss();   //Dismiss loading

            setUIElements();
            new GraphData().execute(p);//Graph

        }
    }
    class GraphData extends AsyncTask<String,Void,Void> {

        //Background Process
        private ProgressDialog dialog;  //Loading dialog
        public GraphData()
        {
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute()
        {
            dialog.setMessage("Loading");   //Loading data dialog
            dialog.show();
        }


        @Override
        protected Void doInBackground(String... params)
        {

            //Retrieve data to display
            InputStream in = gd.connect(params[1]);
            parseGraphData(in);
            return null;
        }

        protected void onPostExecute(Void params)
        {   //Display results
            if(dialog.isShowing())
                dialog.dismiss();   //Dismiss loading

            graph.setImageBitmap(bmp); //Graph

        }
    }

    public void parseData(InputStream in)
    {
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
            o = obj.getJSONObject("quote");

            det[11] = o.getString("Name");
            det[0] = o.getString("symbol");
            det[1] = o.getString("AverageDailyVolume");
            det[2] = o.getString("Change");
            det[3] = o.getString("DaysLow");
            det[4] = o.getString("DaysHigh");
            det[5] = o.getString("YearLow");
            det[6] = o.getString("YearHigh");
            det[7] = o.getString("MarketCapitalization");
            det[8] = o.getString("LastTradePriceOnly");
            det[9] = o.getString("DaysRange");
            det[10] = o.getString("Volume");

            Log.d("TestingDet",det[8]);
        }
        catch(Exception e)
        {
            Log.d("CompanyDataError",e.toString());
        }

    }
    public void parseGraphData(InputStream in)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;

        try
        {
            while ((len = in.read(buffer)) != -1)
            {
                baos.write(buffer, 0, len);
                baos.close();
            }

        }
        catch (Exception e)
        {
            Log.d("ImageError",e.toString());
        }
        byte[] b = baos.toByteArray();
        String str = new String(b);
        if(!str.equals("404"))
            bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        else
            flag = false;
    }

   }

