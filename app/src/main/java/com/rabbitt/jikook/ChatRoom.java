package com.rabbitt.jikook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rabbitt.jikook.ChatAdapter.ChatMessage;
import com.rabbitt.jikook.ChatAdapter.ToggleAdapter;
import com.rabbitt.jikook.Preferences.PrefsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    ImageView sendButton;
    ImageView sendImage;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    SharedPreferences sp;
    String userName, chatwith;
    RecyclerView recyclerView;
    List<ChatMessage> data = new ArrayList<>();
    ChatMessage model = null;

//    public ArrayList<ChatMessage> messages;
    public ToggleAdapter adapter;
//    private static final int REQUEST_IMAGE = 2;
//    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
//    private String mPhotoUrl;
//    private FirebaseUser mFirebaseUser;
//    private FirebaseAuth mFirebaseAuth;
//    public static final String MESSAGES_CHILD = "messages";


    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.chatlist);
        sendButton = findViewById(R.id.sendButton);
        sendImage = findViewById(R.id.sendMedia);
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

        reference1 = new Firebase("https://jikook-0215.firebaseio.com//messages/" + userName + "_" + chatwith);
        reference2 = new Firebase("https://jikook-0215.firebaseio.com//messages/" + chatwith + "_" + userName);

        sendButton.setOnClickListener(v -> {
            String messageText = messageArea.getText().toString();

            if(!messageText.equals("")){
                Map<String, String> map = new HashMap<>();
                map.put("message", messageText);
                map.put("user", userName);
                map.put("type", "text");
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
            }
        });

        sendImage.setOnClickListener(v -> Pix.start(this, Options.init().setRequestCode(100)));


        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String type = Objects.requireNonNull(map.get("type")).toString();
                String message = Objects.requireNonNull(map.get("message")).toString();
                String userNamee = Objects.requireNonNull(map.get("user")).toString();


                switch (type)
                {
                    case "text":
                        if(userNamee.equals(userName)){
                            addMessageBox(message, 0, 0);
                        }
                        else{
                            addMessageBox(message, 1, 0);
                        }
                        break;
                    case "image":
                        if(userNamee.equals(userName)){
                            addMessageBox(message, 2, 1);
                        }
                        else{
                            addMessageBox(message, 3, 1);
                        }
                        break;
                }


//                if(userNamee.equals(userName)){
//                    addMessageBox(message, 0);
//                }
//                else{
//                    addMessageBox(message, 1);
//                }

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

    private void addMessageBox(String message, int type, int s) {

        Log.i(TAG, "addMessageBox: "+message+type);

        model = new ChatMessage();
        model.setMessage(message);
        model.setIsMine(type);
        model.setType(s);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            if (returnValue != null) {
                imageUri = Uri.fromFile(new File(returnValue.get(0)));
                putImageInStorage(imageUri);
            }
        }

    }

    private void putImageInStorage(Uri uri) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference riversRef = storageRef.child("uploads/" + getCurrentTimeStamp());

        riversRef.putFile(uri).addOnSuccessListener(task -> riversRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            Log.i(TAG, "onSuccess: "+ uri1.toString());
                Map<String, String> map = new HashMap<>();
                map.put("user", userName);
                map.put("message", String.valueOf(uri1));
                map.put("type", "image");
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
        }));
    }

    public static String getCurrentTimeStamp(){
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void OnItemClick(int position) {

    }
}
