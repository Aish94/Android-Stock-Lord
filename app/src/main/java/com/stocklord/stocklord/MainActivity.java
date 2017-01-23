package com.stocklord.stocklord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

//AppCompatActivity required for fragments
public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerListener;
    ListView drawerItems;
    String title;
    String id;

    public MainActivity()
    {}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
        {
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        //fetch user data

        title = "User Profile";         //Title of current Fragment
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);  //drawer layout
        drawerItems = (ListView)findViewById(R.id.left_drawer);

        drawerItems.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.fragmentTitles)));
        drawerItems.setOnItemClickListener(this);

        addFragment(new UserProfile());

        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {//Adds and changes icon according to open & close drawer
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        android.app.Fragment fragment;
        fragment = new android.app.Fragment();
        if(position == 0)
            fragment = new About();
        else if(position == 1)
            fragment = new UserProfile();
        else if(position == 2)
            fragment = new CompanyList();
        else if(position == 3)
            fragment = new NewsFeed();
        else if(position == 4)
            fragment = new HighScores();
        else
        {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }

        // Insert the fragment by replacing any existing fragment

        addFragment(fragment);

        // Highlight the selected item, update the title, and close the drawer
        drawerItems.setItemChecked(position, true);
        title = getResources().getStringArray(R.array.fragmentTitles)[position];
        getSupportActionBar().setTitle(title);
        drawerLayout.closeDrawer(drawerItems);
    }

    public void addFragment(android.app.Fragment fragment)
    {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //To open & close navigation bar using the icon in the action bar
        if(drawerListener.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        //Change config of drawer when landscape mode
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

}
