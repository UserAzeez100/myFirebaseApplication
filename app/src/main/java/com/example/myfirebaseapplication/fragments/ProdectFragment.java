package com.example.myfirebaseapplication.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myfirebaseapplication.R;
import com.example.myfirebaseapplication.databinding.BottomSheetEditProdectBinding;
import com.example.myfirebaseapplication.databinding.FragmentProdectBinding;
import com.example.myfirebaseapplication.other.MyInterFace;
import com.example.myfirebaseapplication.other.ProdectClass;
import com.example.myfirebaseapplication.other.RecyclerProductsAdapter;
import com.example.myfirebaseapplication.other.RecyclerProductsPuplicAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProdectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProdectFragment extends Fragment implements MyInterFace  {

    //Todo:step 1
    BottomSheetEditProdectBinding bottomSheetBinding;
    BottomSheetDialog bottomSheetDialog;
    FragmentProdectBinding binding;
    FirebaseFirestore db;
    ArrayList<ProdectClass> arrayList = new ArrayList<>();
    ProdectClass prodectClass;
    RecyclerProductsPuplicAdapter adapter;
    CollectionReference usersCollectionRef;
    ProdectClass oldProdect;
    String name;
    String description;
    String pNameEt;
    String pDescriptionEt;
    ActivityResultLauncher launcher;
    boolean isChecked=false;



    FirebaseStorage firebaseStorage;
    StorageReference storageRef;
    UploadTask uploadTask;

    Uri uriUpload;
    String imageString;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//    }


    public ProdectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProdectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProdectFragment newInstance(String param1, String param2) {
        ProdectFragment fragment = new ProdectFragment();
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
        binding = FragmentProdectBinding.inflate(inflater, container, false);
        bottomSheetBinding = BottomSheetEditProdectBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();


        return binding.getRoot();
    }



    //read all data from firebase Store:
    private void readAllDataFromFirebase() {
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                prodectClass = document.toObject(ProdectClass.class);

                                addToReciclerFunction(prodectClass);//"we handling the data here in recycler"

                            }
                        } else {
                            Log.w(TAG, "Error_ada", task.getException());
                        }
                    }
                });
    }

    //"we handling the data here in recycler"
    private void addToReciclerFunction(ProdectClass prodectClass) {
        arrayList.add(prodectClass);
        adapter = new RecyclerProductsPuplicAdapter(arrayList, this);

        binding.reciclerContaner.setAdapter(adapter);
        binding.reciclerContaner.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();
        adapter.getItemCount();


    }


    @Override
    public void getPositionInterface(int position) {

    }

    @Override
    public void deletePosition(int position) {

    }

    //update heart :
    @Override
    public void heartPosition(int position, Boolean status) {

    }


    @Override
    public void onResume() {
        super.onResume();
        readAllDataFromFirebase();


    }

    @Override
    public void onPause() {
        super.onPause();
        arrayList.clear();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}

