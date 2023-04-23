package com.example.myfirebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myfirebaseapplication.databinding.ActivityVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    FirebaseAuth mAuth;
    private String mVerificationId;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        //getting mobile number from the previous activity and sending massage
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        sendVerificationCode(phoneNumber);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(VerificationActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });








        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cadeMassageEt = binding.codeMassageEt.getText().toString().trim();
                if (!cadeMassageEt.isEmpty() || cadeMassageEt.length() > 6) {

                    verifyVerificationCode(cadeMassageEt);
                } else   binding.codeMassageEt.setError("Enter a valid  code");


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
                    Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("TAG", e.getMessage());
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
                                    Log.e("TAG", "onComplete: " + "Verification  success🫡😉😂");
                                    Toast.makeText(VerificationActivity.this, "Verification success", Toast.LENGTH_SHORT).show();
                                    //verification successful we will start the profile activity
                                    Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    Log.e("TAG", "onComplete: " + "Verification not success 🥴😥😥");

                                    //verification unsuccessful.. display an error message

                                    String message = "Somthing is wrong, we will fix it soon...";

                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    Toast.makeText(VerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


    }

}