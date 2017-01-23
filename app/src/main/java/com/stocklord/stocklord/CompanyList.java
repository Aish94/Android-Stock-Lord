package com.stocklord.stocklord;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CompanyList extends Fragment implements AdapterView.OnItemClickListener{

    ListView comp;

    public CompanyList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_list, container, false);
        Context context = getActivity();
        comp = (ListView) view.findViewById(R.id.listView_companies);
        comp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.companies)));
        comp.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //Go to selected company profile page
        Bundle args = new Bundle();
        args.putInt("company_id", position);
        Fragment newFragment = new CompanyProfile();
        newFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        transaction.replace(R.id.container, newFragment);
        // Commit the transaction
        transaction.commit();
    }
}
