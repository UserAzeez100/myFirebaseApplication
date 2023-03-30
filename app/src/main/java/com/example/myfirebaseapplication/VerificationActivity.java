package com.example.myfirebaseapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myfirebaseapplication.databinding.ActivityVerificationBinding;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

        ActivityVerificationBinding binding;
        FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        String cadeMassage = binding.codeMassageEt.getText().toString().trim();





        //getting mobile number from the previous activity and sending massage
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phone");
        sendVerificationCode(phoneNumber);

        if (!cadeMassage.isEmpty()||cadeMassage.length() > 6){



        }else binding.codeMassageEt.setError("Enter a valid  code");



    }

    private void sendVerificationCode(String phone) {


    }
}