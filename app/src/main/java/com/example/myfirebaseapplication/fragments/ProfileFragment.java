package com.example.myfirebaseapplication.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.MainActivity;
import com.example.myfirebaseapplication.R;
import com.example.myfirebaseapplication.databinding.FragmentProfileBinding;
import com.example.myfirebaseapplication.other.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseStorage firebaseStorage;
    UploadTask uploadTask;
    FirebaseFirestore db;
    CollectionReference usersCollectionRef;
    UserData userData;
    FirebaseUser firebaseAuth;

    StorageReference storageRef;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        firebaseStorage = FirebaseStorage.getInstance();


        db = FirebaseFirestore.getInstance();
        usersCollectionRef = db.collection("profileData");
         firebaseAuth =FirebaseAuth.getInstance().getCurrentUser();
        readAllDataFromFirebase();



        binding.EditeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String country = binding.clintcuontyEt.getText().toString();
                String name = binding.clintNameEt.getText().toString();

                if (!country.isEmpty() && !name.isEmpty()) {
                    userData.setName(name);
                    userData.setCountry(country);
                    Log.e(TAG, "onClick: " + name + "country:" + country + "////old///");

                    updateDataInFirebase( userData);

                } else
                    Toast.makeText(getActivity(), "you cant send empty data ", Toast.LENGTH_SHORT).show();

            }
        });


        binding.logeOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                errorDialog();
            }
        });

        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK) {
                            Intent i = result.getData();
                            Uri b = i.getData();
                            Glide.with(getActivity()).load(b).circleCrop().into(binding.imageViewProfile);
                            uploadImageToFirebase(b, "azeezImage");//upload Image To Firebase:


                        } else if (result.getResultCode() == RESULT_CANCELED) {
//                            binding.imageViewProfile.setImageResource(R.drawable.img);
                            retrieveImageFromFireStore("azeezImage");
                        }
                    }
                }
        );


        binding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);

            }
        });


        return binding.getRoot();
    }

    //upload Image To Firebase:
    private void uploadImageToFirebase(Uri uri, String imageName) {
        StorageReference storageRef = firebaseStorage.getReference().child("images/" + imageName);
        uploadTask = storageRef.putFile(uri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "Error uploading image: " + exception.getMessage());
            }
        });

    }

    //retrieve Image From FireStore:
    private void retrieveImageFromFireStore(String imageName) {
        StorageReference storageRef1 = firebaseStorage.getReference().child("images/" + imageName);
        storageRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//            String imageString=uri.toString();
                Glide.with(getActivity()).load(uri).circleCrop().into(binding.imageViewProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure:image download!! ");
                Glide.with(getActivity()).load(R.drawable.img).circleCrop().into(binding.imageViewProfile);


            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        retrieveImageFromFireStore("azeezImage");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveImageFromFireStore("azeezImage");
    }

    //errorDialog for sign out:
    void errorDialog() {

        new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE)
                .setTitleText("-- Are you sure --")
                .setContentText("you want to sign out ‼")
                .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();

                        //sign out :
                        if (firebaseAuth != null) {

                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                            Log.e("account", "onClick: "+ firebaseAuth);
                            Toast.makeText(getActivity(), "You signed out successfully" + firebaseAuth, Toast.LENGTH_SHORT).show();

                        } else {
                            kAlertDialog.dismissWithAnimation();
                            Toast.makeText(getActivity(), "no Accounts 😣", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setCancelClickListener("Cancel",R.color.gold, new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                    }
                }).cancelButtonColor(R.drawable.shape_yes_or_no_btn).confirmButtonColor(R.drawable.shape_base_btn)
                .show();
    }

    //read all data from firebase Store:
    private void readAllDataFromFirebase() {
        db.collection("profileData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userData = document.toObject(UserData.class);

                                binding.clintNameEt.setText(userData.getName());
                                binding.clintNameTv.setText("M."+ userData.getName());
                                binding.clintcuontyEt.setText(userData.getCountry());
                                if (firebaseAuth.isEmailVerified()){
                                       binding.clintEmailTv.setText(firebaseAuth.getEmail());
                                       binding.clintNameEt.setText(firebaseAuth.getDisplayName());
                                       binding.clintNameTv.setText("M."+ firebaseAuth.getDisplayName());

                                }else
                                    binding.clintEmailTv.setText(firebaseAuth.getPhoneNumber());
                            }
                        } else {
                            Log.w(TAG, "Error_read:", task.getException());
                        }
                    }
                });


    }


    //update user data in fire store ::
    private void updateDataInFirebase( UserData newData) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", newData.getName());
        userMap.put("country", newData.getCountry());
//                whereEqualTo("country",  oldData.getCountry())
        usersCollectionRef.
                whereEqualTo("id","1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    usersCollectionRef.document(document.getId()).
                                            set((userMap), SetOptions.merge())

                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    readAllDataFromFirebase();
//
                                                    Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("eroooor", "onFailure: " + e);
                                                    Toast.makeText(getActivity(), "Error updating fav" + e, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }

                        } else {

                            Log.e(TAG, "Error getting User data.", task.getException());
                            Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }


}

