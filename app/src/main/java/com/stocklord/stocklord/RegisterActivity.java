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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText name;  //Declare views
    EditText pwd;
    EditText confirm_pwd;
    EditText email;
    Button submit;
    TextView login;

    FirebaseAuth auth;
    DatabaseReference db;
    Context context;
    String p[];
    String parameters;
    GetData gd;
    boolean error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;
        error = false;
        p = new String[1];
        gd = new GetData(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        //initialize views
        name = (EditText)findViewById(R.id.editText_name);
        email = (EditText)findViewById(R.id.editText_register_email);
        pwd = (EditText)findViewById(R.id.editText_register_password);
        confirm_pwd = (EditText)findViewById(R.id.editText_register_confirmPassword);
        submit = (Button)findViewById(R.id.button_register_submit);
        login = (TextView)findViewById(R.id.textView_gotoLogin);

        submit.setOnClickListener(this);    //set on click listeners for clickables
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        //When user clicks a button
        if (v.getId() == R.id.button_register_submit)
        {
            //WHen user clicks submit
            //check for valid inputs
            if (ValidateInputs())
            {
                if(gd.haveNetworkConnection())
                    registerUser();
                else
                {
                        Toast toast = Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT);
                        toast.show();
                }
            }

        }

        else if (v.getId() == R.id.textView_gotoLogin)
        {
            //When user clicks register (Not a First time user)
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);

        }
    }


    public void registerUser()
    {
        final ProgressDialog dialog = new ProgressDialog(this);  //Loading dialog
        dialog.setMessage("Registering");   //Loading data dialog
        dialog.show();
        final String uname = name.getText().toString().trim();   //get parameters entered
        final String uemail = email.getText().toString().trim();
        String passwd = pwd.getText().toString().trim();

        //Check if user with email ID already exists.

        auth.createUserWithEmailAndPassword(uemail,passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(dialog.isShowing())
                            dialog.dismiss();   //Dismiss loading
                        if(task.isSuccessful())
                        {
                            User userData = new User(uname,uemail,getResources().getInteger(R.integer.cash_begin));
                            db.child(auth.getCurrentUser().getUid()).setValue(userData);
                            Intent i = new Intent(context,MainActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast toast = Toast.makeText(context, "Server error", Toast.LENGTH_SHORT);
                            Log.d("Error",task.getException().toString());
                            toast.show();
                        }
                    }
                });
    }

        public boolean ValidateInputs()
        {
            String n = name.getText().toString();
            String p = pwd.getText().toString();
            String cp = confirm_pwd.getText().toString();
            String em = email.getText().toString();
            //Check if any field is empty
            if(n.length()==0 || p.length()==0 || cp.length()==0 || em.length()==0) {
                Toast toast = Toast.makeText(context, "Don't leave any field blank", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            //Check if valid email id entered
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                Toast toast = Toast.makeText(context, "Provide a valid email id", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            //Check if password has right length
            if(p.length() < 6)
            {
                Toast toast = Toast.makeText(context, "Password should have atleast 6 characters", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            //Check if passwords match
            if(!cp.equals(p))
            {
                Toast toast = Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            return true;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
