package com.rabbitt.jikook;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rabbitt.jikook.Preferences.PrefsManager;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "LoginActivity";


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    EditText userNameTxt, phoneNumberTxt, dobTxt, bioTxt, nickNameTxt;

    Button maleb, femaleb, startVerificationButton;
    String gender = null;
    DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PrefsManager prefsManager = new PrefsManager(getApplicationContext());
        if (prefsManager.isFirstTimeLaunch()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        //intialized firebase auth
        mAuth = FirebaseAuth.getInstance();

        init();


    }

    private void init() {
        userNameTxt = findViewById(R.id.userName);
        phoneNumberTxt = findViewById(R.id.phone_number_edt);
        dobTxt = findViewById(R.id.Dob);
        bioTxt = findViewById(R.id.Bio);
        nickNameTxt = findViewById(R.id.NickName);
        startVerificationButton = findViewById(R.id.start_auth_button);
        maleb = findViewById(R.id.malebtn);
        femaleb = findViewById(R.id.femalebtn);

        //cancelling the softinput for dob
        dobTxt.setShowSoftInputOnFocus(false);

        startVerificationButton.setOnClickListener(this);
        maleb.setOnClickListener(this);
        femaleb.setOnClickListener(this);

        dobTxt.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onVerificationFailed: "+e.getMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,              // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,        // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        try {

                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            if (user!=null)
                            {
                                Log.i(TAG, "signInWithPhoneAuthCredential: "+user.getUid()+" "+user.getDisplayName()+" "+user.getPhoneNumber());
                            }
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        catch (NullPointerException ex)
                        {
                            Log.i(TAG, "signInWithPhoneAuthCredential: "+ex.getMessage());
                        }


                    }
                });
    }

    private boolean validateUserDetails() {

        String userName = userNameTxt.getText().toString();
        String phoneNumber = phoneNumberTxt.getText().toString();
        String dob = dobTxt.getText().toString();
        String nickName = nickNameTxt.getText().toString();
        String Bio = bioTxt.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberTxt.setError("Invalid phone number.");
            return false;
        }
        if (TextUtils.isEmpty(userName)) {
            phoneNumberTxt.setError("Invalid username.");
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            phoneNumberTxt.setError("Invalid date of birth.");
            return false;
        }
        if (TextUtils.isEmpty(nickName)) {
            phoneNumberTxt.setError("Invalid nickname.");
            return false;
        }
        if (TextUtils.isEmpty(Bio)) {
            phoneNumberTxt.setError("Invalid bio.");
            return false;
        }
        if (gender == null) {
            phoneNumberTxt.setError("Invalid gender.");
            return false;
        }




        return true;
    }

    @SuppressLint("DefaultLocale")
    public void openCalendar()
    {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(LoginActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> dobTxt.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year1)), year, month, day);
        picker.show();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id){
            case R.id.start_auth_button:
                if (!validateUserDetails()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumberTxt.getText().toString());
                break;
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
            case R.id.Dob:
                openCalendar();
                break;

        }
    }
}
