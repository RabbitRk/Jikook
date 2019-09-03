package com.rabbitt.jikook;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rabbitt.jikook.Preferences.PrefsManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button maleb, femaleb, resultb;
    String gender = null;
    Boolean isSel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        PrefsManager prefsManager = new PrefsManager(getApplicationContext());

        if (!prefsManager.isFirstTimeLaunch()) {
            prefsManager.setFirstTimeLaunch(true);
        }

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

        if (id == R.id.action_settings) {
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

                Toast.makeText(this, "Gender: "+gender, Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
