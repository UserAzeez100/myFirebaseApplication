package com.example.myfirebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail=binding.emailEt.getText().toString();
                String uPassword=binding.passwordEt.getText().toString();
                if (!uEmail.isEmpty()){
                    if (!uPassword.isEmpty()&&uPassword.length()>=6){
                        //sign Up by email ::
                        signInWithEmail( uEmail, uPassword);

                    }else Toast.makeText(MainActivity.this, "you must enter >=6 vales in password ", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MainActivity.this, "you cant keep the email empty ", Toast.LENGTH_SHORT).show();


            }
        });





    }

    private void signInWithEmail(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user.isEmailVerified()){
                                Toast.makeText(MainActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(MainActivity.this, ""+ task.getException(), Toast.LENGTH_SHORT).show();

                            auth.signOut();
                            errorDialog();
                            }



                        } else {
                            auth.signOut();
                            errorDialog();
                            // If sign up fails, display a message to the user
                            Toast.makeText(MainActivity.this, ""+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void errorDialog(){
        new KAlertDialog(this, KAlertDialog.ERROR_TYPE)
                .setTitleText("❗ failed ❗")
                .setContentText("you dont have account")
                .setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();


                    }
                }).show();








    }
}