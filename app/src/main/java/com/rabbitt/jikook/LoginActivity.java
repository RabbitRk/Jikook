package com.rabbitt.jikook;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rabbitt.jikook.Preferences.PrefsManager;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "LoginActivity";


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText phoneNumberField, smsCodeVerificationField;

    private String verificationid;
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

        phoneNumberField = findViewById(R.id.phone_number_edt);
        Button startVerficationButton = findViewById(R.id.start_auth_button);
        Button verifyPhoneButton = findViewById(R.id.verify_auth_button);

        startVerficationButton.setOnClickListener(this);
        verifyPhoneButton.setOnClickListener(this);

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

                verificationid = s;
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

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            smsCodeVerificationField.setError("Invalid code.");
                        }
                    }
                });
    }

    private boolean validatePhoneNumberAndCode() {
        String phoneNumber = phoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberField.setError("Invalid phone number.");
            return false;
        }


        return true;
    }

    private boolean validateSMSCode(){
        String code = smsCodeVerificationField.getText().toString();
        if (TextUtils.isEmpty(code)) {
            smsCodeVerificationField.setError("Enter verification Code.");
            return false;
        }

        return true;
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id){
            case R.id.start_auth_button:
                if (!validatePhoneNumberAndCode()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumberField.getText().toString());
                break;
            case  R.id.verify_auth_button:
                if (!validateSMSCode()) {
                    return;
                }
                verifyPhoneNumberWithCode(verificationid, smsCodeVerificationField.getText().toString());
                break;
        }

    }
}
