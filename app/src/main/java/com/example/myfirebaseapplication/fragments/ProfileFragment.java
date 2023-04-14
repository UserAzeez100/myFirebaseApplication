package com.example.myfirebaseapplication.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.myfirebaseapplication.MainActivity;
import com.example.myfirebaseapplication.R;
import com.example.myfirebaseapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseStorage storage;
    StorageReference storageRef;
    UploadTask uploadTask;


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
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();




        binding.logeOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sign out :
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(getActivity(), "You signed out " + FirebaseAuth.getInstance().getCurrentUser(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "no Accounts 😣", Toast.LENGTH_SHORT).show();
                }

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
                            binding.imageViewProfile.setImageResource(R.drawable.img);
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
        storageRef = storageRef.child("images/"+imageName);
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
        StorageReference storageRef = storage.getReference().child("images/"+imageName);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//            String imageString=uri.toString();
                Glide.with(getActivity()).load(uri).circleCrop().into(binding.imageViewProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure:image download!! " );
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

}