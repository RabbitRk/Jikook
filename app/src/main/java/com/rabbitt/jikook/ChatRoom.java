package com.rabbitt.jikook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private static final int REQUEST_IMAGE = 2;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    public static final String MESSAGES_CHILD = "messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

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

        reference1 = new Firebase("https://jikook-k2b15.firebaseio.com/messages/" + userName + "_" + chatwith);
        reference2 = new Firebase("https://jikook-k2b15.firebaseio.com/messages/" + chatwith + "_" + userName);

        sendButton.setOnClickListener(v -> {
            String messageText = messageArea.getText().toString();

            if(!messageText.equals("")){
                Map<String, String> map = new HashMap<>();
                map.put("message", messageText);
                map.put("user", userName);
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
            }
        });

        sendImage.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
//
//        if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    final Uri uri = data.getData();
//                    Log.d(TAG, "Uri: " + uri.toString());
//
//                    FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl, LOADING_IMAGE_URL);
//
//                    reference1.child(MESSAGES_CHILD).push().setValue(tempMessage, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                    if (databaseError == null) {
//                                        String key = databaseReference.getKey();
//                                        StorageReference storageReference =
//                                                FirebaseStorage.getInstance()
//                                                        .getReference(mFirebaseUser.getUid())
//                                                        .child(key)
//                                                        .child(uri.getLastPathSegment());
//
//                                        putImageInStorage(storageReference, uri, key);
//                                    } else {
//                                        Log.w(TAG, "Unable to write message to database.",
//                                                databaseError.toException());
//                                    }
//                                }
//                            });
//                }
//            }
//        }
//    }
//
//    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
//        storageReference.putFile(uri).addOnCompleteListener(this,
//                new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            task.getResult().getMetadata().getReference().getDownloadUrl()
//                                    .addOnCompleteListener(this,
//                                            new OnCompleteListener<Uri>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Uri> task) {
//                                                    if (task.isSuccessful()) {
//                                                        FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, mPhotoUrl, task.getResult().toString());
//                                                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(friendlyMessage);
//                                                    }
//                                                }
//                                            });
//                        } else {
//                            Log.w(TAG, "Image upload task was not successful.",
//                                    task.getException());
//                        }
//                    }
//                });
//    }
//

    @Override
    public void OnItemClick(int position) {

    }
}
