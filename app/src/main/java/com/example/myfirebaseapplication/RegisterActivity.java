package com.example.myfirebaseapplication;

import static android.view.View.GONE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.other.AddUserByPhoneInterface;
import com.example.myfirebaseapplication.other.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {


    com.example.myfirebaseapplication.databinding.ActivityRegisterBinding binding;
    GoogleSignInAccount googleAccount;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    UploadTask uploadTask;
    CollectionReference usersCollectionRef;
    UserData userData;
    AddUserByPhoneInterface phoneInterface;
    boolean imageStat = false;
    Uri uriImage;

    private ActivityResultLauncher<Intent> mStartForResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.myfirebaseapplication.databinding.ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();


        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK) {
                            Intent i = result.getData();
                            uriImage = i.getData();
                            Glide.with(getBaseContext()).load(uriImage).circleCrop().into(binding.avtar);
                            imageStat = true;


                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            binding.avtar.setImageResource(R.drawable.avtar);
                            imageStat = false;
//                            retrieveImageFromFireStore("azeezImage");
                        }
                    }
                }
        );
        binding.avtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);

            }
        });


        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionRef = firebaseFirestore.collection("profileData");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //back to login
        binding.loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        //google button icon::
        binding.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryU = binding.countryEt.getText().toString();
                String nameU = binding.nameEt.getText().toString();
                if (imageStat) {
                    if (!nameU.isEmpty()) {
                        if (!countryU.isEmpty()) {

                            googleSignInIntoMyApp();

                        } else
                            Toast.makeText(RegisterActivity.this, "you cant keep the country empty  ", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(RegisterActivity.this, "you cant keep the name empty ", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "select your Image ", Toast.LENGTH_SHORT).show();
            }
        });

        //when you click phone icon:
        binding.signInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.passwordEt.setHint("enter your phone :_ _ _ _ _ _ _")
                binding.passwordEt.setInputType(InputType.TYPE_CLASS_PHONE);
                binding.textFieldPhoneL.setVisibility(View.VISIBLE);
                binding.textFieldPasswordL.setVisibility(View.GONE);
                binding.textFieldEmailL.setVisibility(View.GONE);
            }
        });


        //when you click email icon:
        binding.signInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.textFieldPhoneL.setVisibility(View.GONE);
                binding.textFieldPasswordL.setVisibility(View.VISIBLE);
                binding.textFieldEmailL.setVisibility(View.VISIBLE);

            }
        });


        //validation the edite texts:
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameU = binding.nameEt.getText().toString();
                String passwordU = binding.passwordEt.getText().toString();
                String countryU = binding.countryEt.getText().toString();
                String phoneU = binding.phoneEt.getText().toString().trim();
                String emailU = binding.emilEt.getText().toString();

                if (binding.textFieldPhoneL.getVisibility() == GONE) {
                    if (imageStat) {
                        if (!nameU.isEmpty()) {
                            if (!emailU.isEmpty()) {
                                if (!passwordU.isEmpty() && passwordU.length() >= 6) {
                                    if (!countryU.isEmpty()) {

                                        //sign Up by email ::
                                        signUpWithEmail(emailU, passwordU);

                                    } else
                                        Toast.makeText(RegisterActivity.this, "you cant keep the country empty ", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(RegisterActivity.this, "you must enter >=6 vales in password ", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(RegisterActivity.this, "you cant keep the email empty ", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(RegisterActivity.this, "you cant keep the name empty ", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(RegisterActivity.this, "select your Image", Toast.LENGTH_SHORT).show();

                } else if (!nameU.isEmpty() || !countryU.isEmpty()) {
                    if (imageStat) {
                        if (!phoneU.isEmpty() && phoneU.length() >= 6 && phoneU.length() <= 14) {

                            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                            intent.putExtra("phone", phoneU);
                            intent.putExtra("name", nameU);
                            intent.putExtra("country", countryU);
                            intent.putExtra("imageUri", uriImage);
                            intent.putExtra("fromRegisterActivity", true);
                            Log.e("uriImageTag", "onClick: image:"+uriImage );
                            startActivity(intent);

                        } else binding.phoneEt.setError("Enter a valid mobile");

                    } else
                        Toast.makeText(RegisterActivity.this, "select your Image", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "you cant keep any inputText empty ", Toast.LENGTH_SHORT).show();

            }
        });

        // Initialize the ActivityResultLauncher object google
        mStartForResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                // Handle the result of the Google Sign-In
                handleSignInResult(data);
            }
        });


        //guest button icon::
        binding.gaeustText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guest();
            }
        });


    }


    //sign in as guest method:
    private void guest() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //sign in with email and password function:
    private void signUpWithEmail(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    user.sendEmailVerification();
                    userData = new UserData(user.getUid(), binding.nameEt.getText().toString(), binding.countryEt.getText().toString(), "no value", binding.emilEt.getText().toString());
                    userData.setImage(uriImage.toString());
                    userData.setImageName("ImageName"+user.getUid());
                    addNewUserToFirebaseStore(userData);//add to fire store user data
                    uploadImageToFirebase(uriImage,"ImageName"+user.getUid());//upload Image To Firebase:

                    successDialog();


                } else {
                    // If sign up fails
                    Toast.makeText(RegisterActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                }
            }
        });
    }

    //alert dialog success massage:
    void successDialog() {
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE).setTitleText("Good job!")
                .setContentText("You created a new account successfully").
                setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                }).show();


    }

    //connect the firebase with google account and update UI:
    void firebaseAuthWithGoogle(String account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success:

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                userData = new UserData(user.getUid(), binding.nameEt.getText().toString(), binding.countryEt.getText().toString(), "no value", binding.emilEt.getText().toString());
                userData.setImage(uriImage.toString());
                userData.setImageName("ImageName"+user.getUid());
                uploadImageToFirebase(uriImage, "ImageName"+user.getUid());//upload Image To Firebase:
                addNewUserToFirebaseStore(userData);//add to fire store user data

                Toast.makeText(RegisterActivity.this, "abcd" + FirebaseAuth.getInstance().getCurrentUser(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);


                Log.d(TAG, "signInWithCredential:success");
            } else {
                // If sign in fails:
                Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();

                Log.d(TAG, "signInWithCredential:failure", task.getException());
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
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    private void googleSignInIntoMyApp() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.wep_client)).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(RegisterActivity.this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mStartForResultLauncher.launch(signInIntent);


    }

    // add New user profile To Firebase Store function:
    private void addNewUserToFirebaseStore(UserData userData) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uIdString = user.getUid();

        firebaseFirestore.collection("profileData").document(uIdString).
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
}