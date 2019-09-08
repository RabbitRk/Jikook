package com.rabbitt.jikook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.rabbitt.jikook.Preferences.PrefsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rabbitt.jikook.Preferences.PrefsManager.CHAT_WITH;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_NAME;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_PARTNER;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_PREFS;

public class ChatRoom extends AppCompatActivity {

    private static final String TAG = "ChatRoom";
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    SharedPreferences sp;
    String userName, chatwith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        PrefsManager prefsManager = new PrefsManager(getApplicationContext());

        if (!prefsManager.isFirstTimeLaunch()) {
            prefsManager.setFirstTimeLaunch(true);
        }


        Firebase.setAndroidContext(this);

        sp = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userName = sp.getString(USER_NAME,"");
        sp = getSharedPreferences(USER_PARTNER, MODE_PRIVATE);
        chatwith = sp.getString(CHAT_WITH,"");

        Log.i(TAG, "onCreate: "+userName+" "+chatwith);

        if (chatwith.equals("") || userName.equals(""))
        {
            Toast.makeText(this, "Invalid data found", Toast.LENGTH_SHORT).show();
            return;
        }

        reference1 = new Firebase("https://jikook-k2b15.firebaseio.com/messages/" + userName + "_" + chatwith);
        reference2 = new Firebase("https://jikook-k2b15.firebaseio.com/messages/" + chatwith + "_" + userName);

        sendButton.setOnClickListener(v -> {
            String messageText = messageArea.getText().toString();

            if(!messageText.equals("")){
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", messageText);
                map.put("user", userName);
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = Objects.requireNonNull(map.get("message")).toString();
                String userNamee = Objects.requireNonNull(map.get("user")).toString();

                if(userNamee.equals(userName)){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addMessageBox(String message, int type) {

        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(5,5,5,5);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 2) {
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);

        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
