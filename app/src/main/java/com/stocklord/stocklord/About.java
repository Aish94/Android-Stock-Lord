package com.stocklord.stocklord;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


public class About extends Fragment implements AdapterView.OnClickListener{
    TextView txt,link;

    public About() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        txt = (TextView)view.findViewById(R.id.aboutcontent);
        link = (TextView)view.findViewById(R.id.link);
        txt.setText("Stocks represent ownership in companies, and stock markets are the places where stocks are bought and sold.\n" +
                "\n" +
                " Having a working knowledge of basic economics is crucial to your success as a stock investor. The stock market and the economy are joined at the hip.\n" +
                "\n" +
                "Understanding basic economics can help you filter financial news to separate relevant information from the irrelevant in order to make better investment decisions. Here are a few important economic concepts to be aware of:\n" +
                "\n" +
                "·         Supply and demand\n" +
                "\n" +
                "·         Cause and effect\n" +
                "\n" +
                "·         Economic effects from government actions\n" +
                "\n"
                );

        link.setText("Read more at: http://www.dummies.com/how-to/content/the-essentials-of-investing-in-stocks-and-bonds.html");
        link.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dummies.com/how-to/content/the-essentials-of-investing-in-stocks-and-bonds.html"));
        startActivity(browserIntent);
    }
}
