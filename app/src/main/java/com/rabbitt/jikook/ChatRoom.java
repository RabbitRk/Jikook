package com.rabbitt.jikook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.rabbitt.jikook.ChatAdapter.ChatMessage;
import com.rabbitt.jikook.ChatAdapter.ToggleAdapter;
import com.rabbitt.jikook.Preferences.PrefsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.rabbitt.jikook.Preferences.PrefsManager.CHAT_WITH;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_NAME;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_PARTNER;
import static com.rabbitt.jikook.Preferences.PrefsManager.USER_PREFS;

public class ChatRoom extends AppCompatActivity implements ToggleAdapter.OnRecycleItemListener{

    private static final String TAG = "ChatRoom";
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    SharedPreferences sp;
    String userName, chatwith;
    RecyclerView recyclerView;
    List<ChatMessage> data = new ArrayList<>();
    ChatMessage model = null;
//    public ArrayList<ChatMessage> messages;
    private ToggleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        recyclerView = findViewById(R.id.chatlist);
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
                    addMessageBox(message, 0);
                }
                else{
                    addMessageBox(message, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addMessageBox(String message, int type) {

        Log.i(TAG, "addMessageBox: "+message+type);

        model = new ChatMessage();
        model.setMessage(message);
        model.setIsMine(type);

        Log.i(TAG, message+" "+type);
        data.add(model);

        adapter = new ToggleAdapter(data, this, this);
        Log.i("HIteshdata", "" + data);

        LinearLayoutManager reLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        reLayoutManager.setStackFromEnd(true);
        reLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void OnItemClick(int position) {

    }
}
