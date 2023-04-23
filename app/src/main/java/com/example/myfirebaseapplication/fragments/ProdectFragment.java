package com.example.myfirebaseapplication.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
public class ProdectFragment extends Fragment implements MyInterFace {

    //Todo:step 1
    BottomSheetEditProdectBinding bottomSheetBinding;
    BottomSheetDialog bottomSheetDialog;
    FragmentProdectBinding binding;
    FirebaseFirestore db;
    ArrayList<ProdectClass> arrayList = new ArrayList<>();
    ProdectClass prodectClass;
    RecyclerProductsAdapter adapter;
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
        usersCollectionRef = db.collection("products");

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();


        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK) {
                            Intent i = result.getData();
                             uriUpload = i.getData();
                            Glide.with(getActivity()).load(uriUpload).circleCrop().into(bottomSheetBinding.imageView);
                            imageString = uriUpload.toString();
                            isChecked=true;
                            Log.e("TAGia", "onActivityResult: "+imageString );

                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            bottomSheetBinding.imageView.setImageResource(R.drawable.img);
//                            retrieveImageFromFireStore("azeezImage");
                        }
                    }
                }
        );


//         documentRef = db.collection("products").document(Objects.requireNonNull(auth.getUid()));

        binding.addProdectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog_add();
            }
        });
        return binding.getRoot();
    }

    //bottom Sheet add new product:
    private void showBottomSheetDialog_add() {

        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        }
        bottomSheetBinding.imageView.setImageResource(R.drawable.img);



        bottomSheetBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pNameEt = bottomSheetBinding.newProdectNameEt.getText().toString();
                pDescriptionEt = bottomSheetBinding.newProdectDecriptionEt.getText().toString();


                //  text input  validation:
                if (!pNameEt.isEmpty() && !pDescriptionEt.isEmpty()) {
                    if(uriUpload!=null) {


                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        prodectClass.setId(arrayList.size());
                        prodectClass.setImageString(imageString);

//                    DateFormat dtForm = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.");
//                    String date = dtForm.format(Calendar.getInstance().getTime());
//                    String fileName = date + FirebaseAuth.getInstance().getCurrentUser().getUid();

                        uploadImageToFirebase(uriUpload, prodectClass.getId());//upload Image To Firebase:
                        addNewProdectToFirebaseStore(prodectClass);  //add to fire store:
                        addToReciclerFunction(prodectClass);//add new item


                    }else Toast.makeText(getActivity(), "select Image ", Toast.LENGTH_SHORT).show();
                } else  Toast.makeText(getActivity(), "enter the data ", Toast.LENGTH_SHORT).show();

            }
        });
        bottomSheetDialog.show();
        bottomSheetBinding.pageTitle.setText("Add New Prodect");
//        Glide.with(getActivity()).load(R.drawable.img).circleCrop().into(bottomSheetBinding.imageView);
        bottomSheetBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);

            }
        });


    }

    //bottom Sheet Edit product:
    private void showBottomSheetDialog_edit(String name, String description, int imageName, int position,String myImageString) {

        if (bottomSheetDialog == null) {

            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        }
        bottomSheetBinding.imageView.setImageResource(R.drawable.img);


        retrieveImageFromFireStore(imageName);//retrieve Image From FireStore and put in a image sheet
        bottomSheetBinding.newProdectNameEt.setText(name);
        bottomSheetBinding.newProdectDecriptionEt.setText(description);


        Log.e("image", "showBottomSheetDialog_edit: |||"+arrayList.get(position).getImageString()+"|||" );

        bottomSheetBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldProdect = new ProdectClass(name, description);


                pNameEt = bottomSheetBinding.newProdectNameEt.getText().toString();
                pDescriptionEt = bottomSheetBinding.newProdectDecriptionEt.getText().toString();

                //  text input  validation:
                if (!pNameEt.isEmpty() && !pDescriptionEt.isEmpty()) {
                    if (uriUpload!=null) {
                        uploadImageToFirebase(uriUpload, imageName);//upload Image To Firebase:
//
                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        prodectClass.setImageString(imageString);
                        uriUpload = null;
                    }else {
//
                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        prodectClass.setImageString(myImageString);

                        Log.e("empty", "onClick: |||"+arrayList.get(position).getImageString()+"||old:"+myImageString );

                    }

                    updateDataInFirebase(oldProdect, mapFuc(prodectClass));////update data in fire store:

                    arrayList.get(position).setProdectName(prodectClass.getProdectName());
                    arrayList.get(position).setProdectDescription(prodectClass.getProdectDescription());
                    arrayList.get(position).setImageString(prodectClass.getImageString());
                    adapter.notifyItemChanged(position);


                    bottomSheetDialog.dismiss();
                    bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            bottomSheetBinding.newProdectNameEt.setText("");
                            bottomSheetBinding.newProdectDecriptionEt.setText("");

                        }
                    });

                } else
                    Toast.makeText(getActivity(), " enter the data ", Toast.LENGTH_SHORT).show();

            }
        });
//        bottomSheetBinding.newProdectNameEt.setText(name);
//        bottomSheetBinding.newProdectDecriptionEt.setText(description);
        bottomSheetDialog.show();
        bottomSheetBinding.pageTitle.setText("Edit Your Product");
        bottomSheetBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);

            }
        });

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

    //update in fire store:
    private void updateDataInFirebase(ProdectClass oldProduct, Map map) {

        usersCollectionRef
                .whereEqualTo("prodectName", oldProduct.getProdectName())
                .whereEqualTo("prodectDescription", oldProduct.getProdectDescription())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    usersCollectionRef.document(document.getId())
                                            .set((map), SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(getActivity(), "Document updated successfully", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("eroooor", "onFailure: " + e);
                                                    Toast.makeText(getActivity(), "Error updating document" + e, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }
                        } else {

                            Log.e(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private Map<String, Object> mapFuc(ProdectClass prodectClass) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("prodectName", prodectClass.getProdectName());
        productMap.put("prodectDescription", prodectClass.getProdectDescription());
        productMap.put("favoriteBool", prodectClass.isFavoriteBool());
//        if ()
        productMap.put("imageString",prodectClass.getImageString());


        Log.e("map", "mapFuc:imageString "+prodectClass.getImageString());
//        productMap.put("productImage",productImage);

//        updateDataInFirebase();
        return productMap;
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
        adapter = new RecyclerProductsAdapter(arrayList, this);

        binding.reciclerContaner.setAdapter(adapter);
        binding.reciclerContaner.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();


    }



    @Override// when Edit :::"
    public void getPositionInterface(int position) {
        int image = arrayList.get(position).getId();
        String imageString =arrayList.get(position).getImageString();
        name = arrayList.get(position).getProdectName();
        description = arrayList.get(position).getProdectDescription();

        showBottomSheetDialog_edit(name, description, image, position,imageString);

        Log.e(TAG, "getPositionInterface: imageString"+imageString );


//        arrayList.set(position,prodectClass);


    }

    //delete from fire store :
    void deleteFromFireStore(ProdectClass prodectClass) {
        usersCollectionRef.whereEqualTo("prodectName", prodectClass.getProdectName())
                .whereEqualTo("prodectDescription", prodectClass.getProdectDescription())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    usersCollectionRef.document(document.getId()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(getActivity(), "Document deleted successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("eroooor", "onFailure: " + e);

                                                }
                                            });
                                }
                            }
                        } else {

                            Log.e(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override//when delete:
    public void deletePosition(int position) {
        name = arrayList.get(position).getProdectName();
        description = arrayList.get(position).getProdectDescription();
        int imageId =arrayList.get(position).getId();

        prodectClass = new ProdectClass(name, description);


        deleteImageFromFirebaseStorage(imageId);
        deleteFromFireStore(prodectClass);
        arrayList.remove(position);//delete item:
        adapter.getItemCount();
        adapter.notifyDataSetChanged();






    }

    //update heart :
    @Override
    public void heartPosition(int position, Boolean status) {
        int image = arrayList.get(position).getId();
        name = arrayList.get(position).getProdectName();
        description = arrayList.get(position).getProdectDescription();
        int id = arrayList.get(position).getId();


        arrayList.get(position).setFavoriteBool(status);
        updateDataInFirebaseF(status, id);

//        Log.e("heart", "heartPosition: position::"+id +"======idI===="+image );


        Toast.makeText(getContext(), "its added to favorite activity", Toast.LENGTH_SHORT).show();


    }


    //update in fire store fav:
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

    //upload Image To Firebase
    private void uploadImageToFirebase(Uri uri, int imageName) {
        StorageReference  storageRef2 =  FirebaseStorage.getInstance()
                .getReference().child("productsImage/"+ imageName);
//        storageRef2.getName();
        uploadTask = storageRef2.putFile(uri);

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
    private void retrieveImageFromFireStore(int imageName) {

            StorageReference storageRef = firebaseStorage.getReference().child("productsImage/" +imageName );
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
//            String imageString=uri.toString();
                    Glide.with(getActivity()).load(uri).circleCrop().into(bottomSheetBinding.imageView);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "onFailure:image download!! ");
                    Glide.with(getActivity()).load(R.drawable.img).circleCrop().into(bottomSheetBinding.imageView);


                }
            });
        }

        //delete Image FromFirebase Storage:
        private void deleteImageFromFirebaseStorage(int imageName){
        StorageReference sr= firebaseStorage.getReference().child("productsImage/"+imageName);
        sr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: image Deleted");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: image not Deleted !!");

            }
        });
        }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

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

        //TODO:here we have one question??!////////////////////////////////////////////////
//        for (int i = 0; i <=arrayList.size(); i++) {
//            int imageId=   arrayList.get(i).getId();
//            retrieveImageFromFireStore(imageId);
//            Log.e("imageLoop", "onViewCreated: "+"("+i+")"+imageId+( arrayList.size()) );
//
//        }
        ///////////////////////////////////////////////

    }
}

