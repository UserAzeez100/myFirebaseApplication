package com.example.myfirebaseapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthenticationClass {

    GoogleSignInAccount googleAccount;
    private ActivityResultLauncher<Intent> mStartForResultLauncher;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Activity activity ;
    Context context;

    public FirebaseAuthenticationClass(Activity activity) {
        this.activity = activity;
    }

    private  void googleSignInIntoMyApp(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.wep_client))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mStartForResultLauncher.launch(signInIntent);

    }
    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign-In was successful, authenticate with Firebase
            googleAccount = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(googleAccount.getIdToken());

        } catch (ApiException e) {
            // Google Sign-In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    //connect the firebase with google account and update UI:
    void firebaseAuthWithGoogle(String account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success:
//                         Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(activity, HomeActivity.class);
//                        Activity.startActivity(intent);

                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success");
                    } else {
                        // If sign in fails:
//                        Toast.makeText(this, "bad", Toast.LENGTH_SHORT).show();

//                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                    }
                });

    }


}
