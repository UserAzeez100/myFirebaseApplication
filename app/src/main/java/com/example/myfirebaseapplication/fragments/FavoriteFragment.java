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
import android.widget.Toast;

import com.example.myfirebaseapplication.databinding.FragmentFavoriteBinding;
import com.example.myfirebaseapplication.other.MyInterFace;
import com.example.myfirebaseapplication.other.ProdectClass;
import com.example.myfirebaseapplication.other.RecyclerProductsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment implements MyInterFace {

    FragmentFavoriteBinding binding;
    ArrayList<ProdectClass> arrayListFavorite=new ArrayList<>();
    RecyclerProductsAdapter adapter;
    FirebaseFirestore db;
    CollectionReference usersCollectionRef;
    ProdectClass prodect;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        usersCollectionRef = db.collection("products");



//        Log.d("TAGGG", "onCreateView: " + prodectClass.isFavoriteBool());

//        Intent intent = new Intent();
//        String name        = intent.getStringExtra("name");
//        String description = intent.getStringExtra("description");
//        boolean fa         = intent.getBooleanExtra("favourite", false);


//        ProdectClass prodectClass =new ProdectClass(name,description);
//        prodectClass.setFavoriteBool(fa);






//        binding=FragmentFavoriteBinding.inflate(inflater,container,false);
//
//
//        ArrayList<ProdectClass> arrayList=new ArrayList<>();
//
//        RecyclerProdectsAdapter adapter = new RecyclerProdectsAdapter(arrayList,this);
//
//        binding.rectangleContaner.setAdapter(adapter);
//        binding.rectangleContaner.setLayoutManager(new LinearLayoutManager(getActivity(),
//                RecyclerView.VERTICAL, false));

        return binding.getRoot();
    }


    @Override
    public void getPositionInterface(int position) {

    }

    @Override
    public void deletePosition(int position) {


    }

    @Override
    public void heartPosition(int position, Boolean status) {
        arrayListFavorite.get(position).setFavoriteBool(status);
        int id =arrayListFavorite.get(position).getId();
        updateDataInFirebaseF(status,id);
        arrayListFavorite.remove(position);
        adapter.getItemCount();
        adapter.notifyDataSetChanged();






    }

    @Override
    public void onResume() {
        super.onResume();
        arrayListFavorite.clear();
        readAllDataFromFirebase();
//        if (arrayListFavorite.size()== 0){
//            binding.textLable.setVisibility(View.VISIBLE);}


    }


    //read all data from firebase Store:
    private void readAllDataFromFirebase() {
        db.collection("products").whereEqualTo("favoriteBool",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            int i=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ++i;
                               prodect = document.toObject(ProdectClass.class);
                                Log.e("jjjjjjj", "onComplete: "+i);
                                Log.e("jjjjjjj", "onComplete: "+i+ prodect.isFavoriteBool()+"****"+arrayListFavorite.size());

                                if (prodect.isFavoriteBool()) {
                                    binding.textLable.setVisibility(View.INVISIBLE);
                                    addToRecyclerFunction(prodect);//"we handle the data here in recycler"

                                }

                            }
                        } else {
                            Log.w(TAG, "Error_ada", task.getException());
//                            arrayListFavorite.clear();
                            binding.textLable.setVisibility(View.VISIBLE);

                        }
                    }
                });
    }

    //"we handling the data here in recycler"
    private void addToRecyclerFunction(ProdectClass prodectClass) {
        arrayListFavorite.add(prodectClass);
        adapter = new RecyclerProductsAdapter(arrayListFavorite,this);
        binding.rectangleContaner.setAdapter(adapter);
        binding.rectangleContaner.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        adapter.notifyItemChanged(arrayListFavorite.size()-1);



    }


    private void updateDataInFirebaseF(boolean b, int id) {
        Map<String, Boolean> productMap = new HashMap<>();
        productMap.put("favoriteBool", b);
        usersCollectionRef.whereEqualTo("id", id).
                get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    usersCollectionRef.document(document.getId()).
                                            set((productMap), SetOptions.merge())

                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
//                                                    readAllDataFromFirebase();
                                                    Toast.makeText(getActivity(), "fav updated successfully", Toast.LENGTH_SHORT).show();

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

                            Log.e(TAG, "Error getting fav.", task.getException());
                            Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}