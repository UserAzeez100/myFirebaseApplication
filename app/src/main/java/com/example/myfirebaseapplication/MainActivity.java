package com.example.myfirebaseapplication;

import static android.view.View.INVISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.databinding.ActivityMainBinding;
import com.example.myfirebaseapplication.other.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    GoogleSignInAccount googleAccount;
    CollectionReference collectionReference;
    private ActivityResultLauncher<Intent> mStartForResultLauncher;
    FirebaseFirestore firebaseFirestore;
    UserData userData;
    FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        collectionReference = FirebaseFirestore.getInstance().collection("profileData");
        firebaseFirestore = FirebaseFirestore.getInstance();
        myAuth=FirebaseAuth.getInstance();

        userData = new UserData();



        // Initialize the ActivityResultLauncher object google
        mStartForResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                // Handle the result of the Google Sign-In
                handleSignInResult(data);
            }
        });

        //when you click phone icon:
        binding.signInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.passwordEt.setHint("enter your phone :_ _ _ _ _ _ _")
                String uPhoneEt = binding.passwordEt.getText().toString().trim();
                binding.emailEtLayout.setVisibility(View.INVISIBLE);
                binding.passwordEt.setInputType(InputType.TYPE_CLASS_PHONE);
                binding.passwordEtlayout.setHint(getText(R.string.phone_number));
                    if (!uPhoneEt.isEmpty() && uPhoneEt.length() >= 6 && uPhoneEt.length() <= 14&&binding.emailEtLayout.getVisibility() == INVISIBLE) {

                        Intent intent = new Intent(MainActivity.this, VerificationActivity.class);
                        intent.putExtra("phone", uPhoneEt);
                        startActivity(intent);

                    } else binding.passwordEt.setError("Enter a valid mobile");
//                    checkPhoneNumberExistInFirebase(uPhoneEt);
            }
        });


        //when you click email icon:
        binding.signInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.emailEtLayout.setVisibility(View.VISIBLE);
                binding.passwordEtlayout.setHint(getText(R.string.enter_your_password));

            }
        });

        //google button icon::
        binding.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInIntoMyApp();
            }
        });


        binding.registerLableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail = binding.emailEt.getText().toString();
                String uPassword = binding.passwordEt.getText().toString();
                if (!uEmail.isEmpty()) {
                    if (!uPassword.isEmpty() && uPassword.length() >= 6) {
                        //sign Up by email ::
                        signInWithEmail(uEmail, uPassword);

                    } else
                        Toast.makeText(MainActivity.this, "you must enter >=6 vales in password ", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "you cant keep the email empty ", Toast.LENGTH_SHORT).show();


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
                            if (user.isEmailVerified()) {
                                Toast.makeText(MainActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();

                                auth.signOut();
                                errorDialog();
                            }

                        } else {
                            auth.signOut();
                            errorDialog();
                            // If sign up fails, display a message to the user
                            Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("email_EX", "onFailure: with email EXC"+e );
                    }
                });
    }

    //connect the firebase with google account and update UI:
    void firebaseAuthWithGoogle(String account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success:

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                Toast.makeText(MainActivity.this, "abcd" + FirebaseAuth.getInstance().getCurrentUser(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);


                Log.d(TAG, "signInWithCredential:success");
            } else {
                // If sign in fails:
                Toast.makeText(this, "fail google exception" + task.getException(), Toast.LENGTH_SHORT).show();

                Log.d(TAG, "signInWithCredential:failure", task.getException());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                        Log.e("fail google exception", "onFailure: "+e );
                Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign-In was successful, authenticate with Firebase
            googleAccount = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(googleAccount.getIdToken());

        } catch (ApiException e) {
            // Google Sign-In failed
            Log.e(TAG, "Google sign in failed", e);
            Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();

        }
    }

    private void googleSignInIntoMyApp() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.wep_client)).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mStartForResultLauncher.launch(signInIntent);


    }


//    void checkPhoneNumberExistInFirebase(String phoneNumber) {
//        firebaseFirestore.collection("profileData")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                userData = document.toObject(UserData.class);
//                                if (phoneNumber.equals(document.get("phone"))) {
//
//                                    Log.e("TAG_phone", "phone:" + document.get("phone") + "****" + userData.getPhone() + "/-----/phoneEt:" + phoneNumber);
//                                    successDialog();
//                                    break;
//
//                                } else{
//                                    Log.e(TAG, "phone: no phone😥😥😥");
//                                    Toast.makeText(MainActivity.this, "you don't have account", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } else Log.e("55555", "phone: ههههههههههههه0");
//
//
//                    }
//                });
//    }

    //alert dialog success massage:
    void successDialog() {
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE).setTitleText("Good job!")
                .setContentText("You Sign In successfully").
                setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);

                    }
                }).show();
    }

    void errorDialog() {
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