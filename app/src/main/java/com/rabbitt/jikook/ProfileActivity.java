package com.rabbitt.jikook;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;


    private DatabaseReference mUsersDatabase;
    String imageUrl, bioStr, dobStr, genderStr, nicknameStr, phoneStr, userStr;
    TextView bioTxt, dobTxt, genderTxt, nicknameTxt, phoneTxt, userTxt;
    ImageView profile_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        profile_img = findViewById(R.id.profileImg);
        userTxt = findViewById(R.id.username);
        nicknameTxt = findViewById(R.id.nickname);
        genderTxt = findViewById(R.id.gender);
        dobTxt = findViewById(R.id.dob);
        phoneTxt = findViewById(R.id.mobile);
        bioTxt = findViewById(R.id.bio);

       String user_id = mAuth.getCurrentUser().getUid();
        Log.i(TAG, "onCreate: "+user_id);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userStr = dataSnapshot.child("username").getValue().toString();
                nicknameStr = dataSnapshot.child("nickname").getValue().toString();
                imageUrl = dataSnapshot.child("imageUrl").getValue().toString();
                genderStr = dataSnapshot.child("gender").getValue().toString();
                phoneStr = dataSnapshot.child("phone").getValue().toString();
                bioStr = dataSnapshot.child("bio").getValue().toString();
                dobStr = dataSnapshot.child("dob").getValue().toString();


                bioTxt.setText(bioStr);
                userTxt.setText(userStr);
                genderTxt.setText(genderStr);
                phoneTxt.setText(phoneStr);
                nicknameTxt.setText(nicknameStr);
                dobTxt.setText(dobStr);

//bioTxt, dobTxt, genderTxt, nicknameTxt, phoneTxt, userTxt
                Log.i(TAG, "onDataChange: "+imageUrl);
//                mProfileName.setText(display_name);
//                mProfileStatus.setText(status);
//
//                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);
                Glide.with(ProfileActivity.this)
                        .load(imageUrl)
                        .into(profile_img);
//                if(mCurrent_user.getUid().equals(user_id)){
//
//                    mDeclineBtn.setEnabled(false);
//                    mDeclineBtn.setVisibility(View.INVISIBLE);
//
//                    mProfileSendReqBtn.setEnabled(false);
//                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
