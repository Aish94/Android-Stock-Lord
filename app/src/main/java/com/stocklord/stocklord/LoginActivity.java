package com.stocklord.stocklord;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{

    EditText email;     //Declare Views
    EditText pwd;
    Button submit;
    TextView register;

    Context context;
    GetData gd;  //used to connect to server and extract data
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
        {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

        gd = new GetData(this);


        //initialize all views
        email = (EditText)findViewById(R.id.editText_email);
        pwd = (EditText)findViewById(R.id.editText_password);
        submit = (Button)findViewById(R.id.button_submit);
        register = (TextView)findViewById(R.id.textView_gotoRegister);

        submit.setOnClickListener(this);    //Set onClick Listener
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
      //Button click

        if (v.getId() == R.id.button_submit)
        {
            //When user clicks submit
            if(gd.haveNetworkConnection()) {
                signInUser();
            }
            else {
                Toast toast = Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if (v.getId() == R.id.textView_gotoRegister)
        {
            //When user clicks register (First time user)
            Intent i = new Intent(this,RegisterActivity.class);
            startActivity(i);
        }

    }

    public void signInUser()
    {
        final ProgressDialog dialog= new ProgressDialog(this);
        String uname = email.getText().toString().trim();  //get parameters entered
        String passwd = pwd.getText().toString().trim();
        auth.signInWithEmailAndPassword(uname,passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(dialog.isShowing())
                            dialog.dismiss();   //Dismiss loading
                        if(task.isSuccessful())
                        {
                            Intent i = new Intent(context,MainActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast toast = Toast.makeText(context, "Invalid email/password", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
