package com.rabbitt.jikook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rabbitt.jikook.Preferences.PrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    EditText userNameTxt, phoneNumberTxt, bioTxt, nickNameTxt;
    TextView dobTxt;
    LinearLayout parent;

    Button maleb, femaleb, startVerificationButton;
    ImageView profile;
    String gender = null;
    DatePickerDialog picker;

    String userName, phoneNumber, dob, nickName, Bio;
//    SpinnerLoading sp;


    Uri selectedImage;
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;

    Uri imageUri;
    String user_id;

    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PrefsManager prefsManager = new PrefsManager(getApplicationContext());
        if (prefsManager.isFirstTimeLaunch()) {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }

        //intialized firebase auth
        mAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

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
        parent = findViewById(R.id.dobParent);
        profile = findViewById(R.id.profileImg);

        //cancelling the softinput for dob
        dobTxt.setShowSoftInputOnFocus(false);

        startVerificationButton.setOnClickListener(this);
        maleb.setOnClickListener(this);
        femaleb.setOnClickListener(this);

        dobTxt.setOnClickListener(this);
        parent.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();

        //creates a storage reference
        storageRef = storage.getReference();

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
                Log.i(TAG, "onVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
            }
        };
    }
    
    private void startPhoneNumberVerification(String phoneNumber) {
        //send otp to the server
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
                            user_id = mAuth.getCurrentUser().getUid();
                            if (user != null) {
                                uploadImage_fire();
                            }
//                            sp.setVisibility(View.GONE);
                        } catch (NullPointerException ex) {
                            Log.i(TAG, "signInWithPhoneAuthCredential: " + ex.getMessage());
                        }
                    } else {
                        Toast.makeText(this, "Soul Registration unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean setPrefsdetails() {
        PrefsManager prefsManager = new PrefsManager(this);
        prefsManager.userPreferences(userName, phoneNumber, dob, nickName, Bio);
        Log.i(TAG, "set preference Hid.............." + userName);
        return true;
    }

    private void FireToDatabase(Uri uri) {

        String url = "https://jikook-0215.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, s -> {

            Firebase reference = new Firebase("https://jikook-0215.firebaseio.com/users");
           
            if (s.equals("null")) {
                reference.child(userName).child("user_id").setValue(userName);
                reference.child(userName).child("dob").setValue(dob);
                reference.child(userName).child("nickname").setValue(nickName);
                reference.child(userName).child("phone").setValue(phoneNumber);
                reference.child(userName).child("gender").setValue(gender);
                reference.child(userName).child("bio").setValue(Bio);
                reference.child(userName).child("imageUrl").setValue(String.valueOf(uri));

                Toast.makeText(LoginActivity.this, "Soul Registered successfully", Toast.LENGTH_LONG).show();
            }
            else
            {
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.has(userName)) {
                        reference.child(userName).child("user_id").setValue(user_id);
                        reference.child(userName).child("dob").setValue(dob);
                        reference.child(userName).child("nickname").setValue(nickName);
                        reference.child(userName).child("phone").setValue(phoneNumber);
                        reference.child(userName).child("gender").setValue(gender);
                        reference.child(userName).child("bio").setValue(Bio);
                        reference.child(userName).child("imageUrl").setValue(String.valueOf(uri));

                        Toast.makeText(LoginActivity.this, "Soul Registered successfully", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, volleyError -> System.out.println("" + volleyError));

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    private boolean validateUserDetails() {

        userName = userNameTxt.getText().toString();
        phoneNumber = phoneNumberTxt.getText().toString();
        dob = dobTxt.getText().toString();
        nickName = nickNameTxt.getText().toString();
        Bio = bioTxt.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            userNameTxt.setError("Invalid username.");
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            dobTxt.setError("Invalid date of birth.");
            return false;
        }
        if (TextUtils.isEmpty(nickName)) {
            nickNameTxt.setError("Invalid nickname.");
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberTxt.setError("Invalid phone number.");
            return false;
        }
        if (gender == null) {
            Toast.makeText(this, "Invalid gender.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(Bio)) {
            bioTxt.setError("Invalid bio.");
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please set an Avatar", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    public void openCalendar() {
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
        switch (id) {
            case R.id.start_auth_button:
                if (!validateUserDetails()) {
                    return;
                }
//                sp.setVisibility(View.VISIBLE);
                loading = ProgressDialog.show(this, "Registering", "Please wait...we will automatically verify your OTP", false, true);

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
            case R.id.dobParent:
                openCalendar();
                break;

        }
    }

    public void image_(View view) {
//        Options options = Options.init()
//                .setRequestCode(100)                                                 //Request code for activity results
//                .setCount(3)                                                         //Number of images to restict selection count
//                .setFrontfacing(false)                                                //Front Facing camera on start
//                .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
//                .setPreSelectedUrls(returnValue)                                     //Pre selected Image Urls
//                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
//                .setPath("/pix/images");                                             //Custom Path For Image Storage
//        Pix.start(MainActivity.this, options);

        Pix.start(this, Options.init().setRequestCode(100));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS); 
            if (returnValue != null) {
                imageUri = Uri.fromFile(new File(returnValue.get(0)));
                profile.setImageURI(imageUri);
//                uploadImage_fire();
            }
        }
    }

    public void uploadImage_fire() {

        //create reference to images folder and assing a name to the file that will be uploaded
//        imageRef = storageRef.child("images/"+getFileExtension(imageUri));

        loading.dismiss();
        //creating and showing progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        Log.i(TAG, "uploadImage_fire: " + getFileExtension(imageUri));
        StorageReference riversRef = storageRef.child("images/" + userNameTxt.getText().toString());

        riversRef.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG, "onFailure: " + exception.getMessage());
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG, "onSuccess: "+uri.toString());
                        FireToDatabase(uri);
                        if (setPrefsdetails()) {
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            finish();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}