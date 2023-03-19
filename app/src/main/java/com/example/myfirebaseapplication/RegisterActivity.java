package com.example.myfirebaseapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding  binding;
    GoogleSignInAccount account;

    private ActivityResultLauncher<Intent> mStartForResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.loginTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail=binding.emilEt.getText().toString();
                String uPassword=binding.passwordEt.getText().toString();
                if (!uEmail.isEmpty()){
                    if (!uPassword.isEmpty()&&uPassword.length()>=6){
                        //sign Up by email ::
                        signUpWithEmail( uEmail, uPassword);



                    }else Toast.makeText(RegisterActivity.this, "you must enter >=6 vales in password ", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(RegisterActivity.this, "you cant keep the email empty ", Toast.LENGTH_SHORT).show();

            }
        });

        // Initialize the ActivityResultLauncher object
        mStartForResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the result of the Google Sign-In flow
                        handleSignInResult(data);
                    }
                });


        //google button::
        binding.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.wep_client))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(RegisterActivity.this, gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                mStartForResultLauncher.launch(signInIntent);

            }

        });

        binding.signInGest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guest();
            }
        });


    }
    private void guest(){
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    });
    }
    private void signUpWithEmail(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification();
                            auth.signOut();

                            successDialog();


                        } else {
                            // If sign up fails, display a message to the user
                            Toast.makeText(RegisterActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void successDialog(){
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("You created a new account successfully")
                .setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                }).show();



    }
    void firebaseAuthWithGoogle(String   account){
     FirebaseAuth   mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });

    }

    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign-In was successful, authenticate with Firebase
             account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign-In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
        }
    }




}