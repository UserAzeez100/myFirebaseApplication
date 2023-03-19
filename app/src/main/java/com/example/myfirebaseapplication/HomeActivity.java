package com.example.myfirebaseapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myfirebaseapplication.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





    }
}