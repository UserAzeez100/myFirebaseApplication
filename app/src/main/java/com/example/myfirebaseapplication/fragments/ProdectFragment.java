package com.example.myfirebaseapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfirebaseapplication.R;
import com.example.myfirebaseapplication.databinding.BottomSheetEditProdectBinding;
import com.example.myfirebaseapplication.databinding.FragmentProdectBinding;
import com.example.myfirebaseapplication.other.ProdectClass;
import com.example.myfirebaseapplication.other.RecyclerProdectsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProdectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProdectFragment extends Fragment {

    BottomSheetEditProdectBinding bindingSheet;
    BottomSheetDialog bottomSheetDialog;
    FragmentProdectBinding binding;
    FirebaseFirestore db;

    EditText nameEt,descriptionEt;



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
        bindingSheet = BottomSheetEditProdectBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        nameEt =bindingSheet.newProdectNameEt.findViewById(R.id.newProdectNameEt);
        descriptionEt =bindingSheet.newProdectDecriptionEt.findViewById(R.id.newProdectDecriptionEt);






        binding.addProdectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });


        ArrayList<ProdectClass> arrayList = new ArrayList<>();
        arrayList.add(new ProdectClass("aaaaaaaa", "sssdbhluifbhusdb"));
        arrayList.add(new ProdectClass("dfggff", "scrvtbdnsvcxacsrvdt"));
        arrayList.add(new ProdectClass("dsrw", "sadcfscghfbuilhsdr"));
        arrayList.add(new ProdectClass("tesc", "ersvvrvvvvvvvvvvvvv"));


        RecyclerProdectsAdapter adapter = new RecyclerProdectsAdapter(arrayList);

        binding.reciclerContaner.setAdapter(adapter);
        binding.reciclerContaner.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));


        return binding.getRoot();
    }

    private void showBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bindingSheet.getRoot());

//Todo:i check here please wait to update the code:::!!!!!!!!!!!!!!!
            bottomSheetDialog.findViewById(R.id.editBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    String pName = nameEt.getText().toString();
                    String pDescription = descriptionEt.getText().toString();
                    Log.e(TAG, "onClick: asdfasfgfewrgwergvw"+pName+"****"+pDescription );



                    Log.e(TAG, "onClick: "+nameEt.getText().toString()+"-------"+descriptionEt.getText().toString() );
                    if (nameEt==null) {
                        Log.d(TAG, "onClick: "+ pDescription +"------"+pName);
                        ProdectClass prodectClass = new ProdectClass(pName, pDescription);
                        //add to fire store:
                        addNewProdectToFirebaseStore(prodectClass);
//                     bottomSheetDialog.dismiss();

                    } else
                        Toast.makeText(getActivity(), "enter the data ", Toast.LENGTH_SHORT).show();}




            });
        }
        bottomSheetDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    // add New Prodect To Firebase Store function:
    private void addNewProdectToFirebaseStore(ProdectClass prodect) {
        db.collection("products")
                .add(prodect)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "the prodect added successfully ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}