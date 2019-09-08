package com.rabbitt.jikook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rabbitt.jikook.Preferences.PrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.rabbitt.jikook.Preferences.PrefsManager.USER_NAME;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_PREFS;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    SharedPreferences userpref;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);

        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userName = userpref.getString(USER_NAME,"");


        pd = new ProgressDialog(UserActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://jikook-k2b15.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
            Log.i(TAG, "onResponse: "+s);
            doOnSuccess(s);
        }, volleyError -> Log.i(TAG, "onErrorResponse: "+volleyError.toString()));

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);


        usersList.setOnItemClickListener((parent, view, position, id) -> {
//                UserDetails.chatWith = al.get(position);
                PrefsManager pm = new PrefsManager(this);
                pm.chatwith(al.get(position));
                startActivity(new Intent(this, ChatRoom.class));
        });
    }

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(userName)) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();
    }
}