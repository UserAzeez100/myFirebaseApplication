package com.example.myfirebaseapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.databinding.ActivityVerificationBinding;
import com.example.myfirebaseapplication.other.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    UploadTask uploadTask;
    private String mVerificationId;
    String phoneNumber;
    String userName;
    String userCountry;
    Uri uri;
    boolean state = false;
    boolean fromMainActivity;
    UserData userData;
//    AddUserByPhoneInterface phoneInterface  ;
//    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        userData = new UserData();


        //getting mobile number from the previous activity and sending massage
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        userName = intent.getStringExtra("name");
        userCountry = intent.getStringExtra("country");
        uri = intent.getParcelableExtra("imageUri");
        fromMainActivity = getIntent().getBooleanExtra("fromMainActivity", false);

        sendVerificationCode(phoneNumber);


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(VerificationActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });


        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cadeMassageEt = binding.codeMassageEt.getText().toString().trim();
                if (!cadeMassageEt.isEmpty() || cadeMassageEt.length() > 6) {

                    verifyVerificationCode(cadeMassageEt);
                } else binding.codeMassageEt.setError("Enter a valid  code");


            }
        });

        //to Resend the code to user again:
        binding.ResendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(phoneNumber);
                Toast.makeText(VerificationActivity.this, "⌛⌛", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    //Getting the code sent by SMS
                    String code = phoneAuthCredential.getSmsCode();

                    //sometime the code is not detected automatically
                    //in this case the code will be null
                    //so user has to manually enter the code
                    if (code != null) {
                        binding.codeMassageEt.setText(code);
                        //verifying the code
                        verifyVerificationCode(code);


                    } else
                        Toast.makeText(VerificationActivity.this, "enter your code manually  ", Toast.LENGTH_SHORT).show();

                }


                @Override
                public void onVerificationFailed(FirebaseException e) {
//                    FirebaseUser  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//                    Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.e("TAG", e.getMessage());
//                    Log.e("TAG", "onComplete: " + "Verification  success " +
//                            "🫡😉😂idUser::: "+firebaseUser.getUid()+"country:"+userCountry+ "phone:"+phoneNumber+"image:"+uri);

                }

                //when the code is generated then this method will receive the code.
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                    super.onCodeSent(s, forceResendingToken);
                    //storing the verification id that is sent to the user
                    mVerificationId = s;
                }
            };

    private void verifyVerificationCode(String code) {
        //creating the credential //اعتماد
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        signInWithPhoneAuthCredential(credential); //used for signing the user

    }

    //used for signing the user
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    //todo:------------

                                    if (fromMainActivity) {
                                        if (firebaseUser != null) {
                                            UserData userData = new UserData(firebaseUser.getUid(), userName, userCountry, phoneNumber, "");
                                            if (uri != null) {
                                                userData.setImage(uri.toString());
                                                userData.setImageName("ImageName"+firebaseUser.getUid());
                                                uploadImageToFirebase(uri,"ImageName"+firebaseUser.getUid());//upload Image To Firebase:
                                                Log.e(TAG, "onComplete: image Uploaded successfully");
                                            }
                                            addNewUserToFirebaseStore(userData);//add to fire store user data
                                            Log.e("TAG", "onComplete: " + "Verification  success " + "🫡😉😂idUser::: " + firebaseUser.getUid() + "country:" + userCountry + "phone:" + phoneNumber + "image:" + uri);

                                        }
                                        Toast.makeText(VerificationActivity.this, "Verification success🫡😉", Toast.LENGTH_SHORT).show();
                                        //verification successful we will start the profile activity
                                       successDialog();



                                    }
                                } else {
                                    Log.e("TAG", "onComplete: " + "Verification not success 🥴😥😥");
//--------------------------------


                                    //verification unsuccessful.. display an error message

                                    String message = "Somthing is wrong, we will fix it soon...";

                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = task.getException()+"";
                                    }

                                    Toast.makeText(VerificationActivity.this, message, Toast.LENGTH_SHORT).show();


                                }
                            }
                        });


    }

    //upload Image To Firebase:
    private void uploadImageToFirebase(Uri uri, String imageName) {
        StorageReference storageRef = firebaseStorage.getReference().child("images/" + imageName);
        uploadTask = storageRef.putFile(uri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getBaseContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "Error uploading image: " + exception.getMessage());
            }
        });

    }

    // add New user profile To Firebase Store function:
    private void addNewUserToFirebaseStore(UserData userData) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("profileData").document(mAuth.getCurrentUser().getUid()).
                set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getBaseContext(), "the data added successfully ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private boolean checkPhoneNumberExistInFirebase(String phoneNumber) {
        firebaseFirestore.collection("profileData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userData = document.toObject(UserData.class);
                                if (phoneNumber.equals(document.get("phone"))) {

                                    Log.e("TAG_phone", "phone:" + document.get("phone") + "****" + userData.getPhone() + "/-----/phoneEt:" + phoneNumber);
//                                    successDialog();
                                    state = true;
                                    break;

                                } else {
                                    Log.e(TAG, "phone: no phone😥😥😥");
                                    Toast.makeText(VerificationActivity.this, "you don't have account", Toast.LENGTH_SHORT).show();
                                    state = false;
                                }
                            }

                        } else Log.e("55555", "phone: ههههههههههههه0");


                    }
                });
        return state;
    }


    //alert dialog success massage:
    void successDialog() {
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE).setTitleText("Good job!")
                .setContentText("You Sign In successfully").
                setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).show();
    }



}