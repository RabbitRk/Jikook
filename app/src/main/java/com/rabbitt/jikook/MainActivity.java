package com.rabbitt.jikook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    Button maleb, femaleb, resultb;
    String gender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        init();
    }

    private void init() {
        maleb = findViewById(R.id.malebtn);
        femaleb = findViewById(R.id.femalebtn);
        resultb = findViewById(R.id.result);

        maleb.setOnClickListener(this);
        femaleb.setOnClickListener(this);
        resultb.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.malebtn:

                maleb.setBackgroundResource(R.drawable.btn_bg_selected);
                femaleb.setBackgroundResource(R.drawable.btn_bg);
                gender = "Male";
                break;

            case R.id.femalebtn:

                femaleb.setBackgroundResource(R.drawable.btn_bg_selected);
                maleb.setBackgroundResource(R.drawable.btn_bg);
                gender = "Female";

                break;

            case R.id.result:
                register();
                Toast.makeText(this, "Gender: "+gender, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserActivity.class));
                break;

        }
    }

    private void register() {

        String url = "https://jikook-0215.firebaseio.com//users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Log.i(TAG, "onResponse: Main"+s);
                Firebase reference = new Firebase("https://jikook-0215.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child("user_id").setValue("pass");
                    Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.i(TAG, "onResponse: ");
//                    try {
//                        JSONObject obj = new JSONObject(s);
//
//                        if (!obj.has(user)) {
//                            reference.child(user).child("password").setValue(pass);
//                            Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }

            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }
}
