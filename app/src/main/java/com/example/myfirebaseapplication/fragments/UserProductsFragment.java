package com.example.myfirebaseapplication.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myfirebaseapplication.R;
import com.example.myfirebaseapplication.databinding.BottomSheetEditProdectBinding;
import com.example.myfirebaseapplication.databinding.FragmentUserProductsBinding;
import com.example.myfirebaseapplication.other.MyInterFace;
import com.example.myfirebaseapplication.other.ProdectClass;
import com.example.myfirebaseapplication.other.RecyclerProductsAdapter;
import com.example.myfirebaseapplication.other.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProductsFragment extends Fragment implements MyInterFace {


    //Todo:step 1
    BottomSheetEditProdectBinding bottomSheetBinding;
    BottomSheetDialog bottomSheetDialog;
    FragmentUserProductsBinding binding;
    FirebaseFirestore db;
    ArrayList<ProdectClass> arrayList = new ArrayList<>();
    ProdectClass prodectClass;
    RecyclerProductsAdapter adapter;
    CollectionReference publicCollectionRef;
    CollectionReference userCollection;
    DocumentReference documentReference;
    ProdectClass oldProdect;
    String name;
    String description;
    String pNameEt;
    String pDescriptionEt;
    ActivityResultLauncher launcher;
    boolean isChecked = false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    FirebaseStorage firebaseStorage;
    StorageReference storageRef;
    UploadTask uploadTask;

    Uri uriUpload;
    String imageString;
    ProdectClass userProductClass;


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


    public UserProductsFragment() {
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
    public static UserProductsFragment newInstance(String param1, String param2) {
        UserProductsFragment fragment = new UserProductsFragment();
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
        binding = FragmentUserProductsBinding.inflate(inflater, container, false);
        bottomSheetBinding = BottomSheetEditProdectBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        publicCollectionRef = db.collection("products");
        //todo:1
        userCollection = db.collection("profileData");
        documentReference = db.collection("profileData").document(firebaseUser.getUid()).collection("userArrayListProducts").getParent();


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
                            isChecked = true;
                            Log.e("TAGia", "onActivityResult: " + imageString);

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
        bottomSheetBinding.imageView.setImageResource(R.drawable.upload_image);


        bottomSheetBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pNameEt = bottomSheetBinding.newProdectNameEt.getText().toString();
                pDescriptionEt = bottomSheetBinding.newProdectDecriptionEt.getText().toString();


                //  text input  validation:
                if (!pNameEt.isEmpty() && !pDescriptionEt.isEmpty()) {
                    if (uriUpload != null) {


                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        userProductClass = new ProdectClass(pNameEt, pDescriptionEt);

                        ///---------------todo------------------------
                        UserData userData = new UserData();
                        userData.setUserArrayListProducts(arrayList);

                        //create a unique id:
                        DateFormat dtForm = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.");
                        String date = dtForm.format(Calendar.getInstance().getTime());
                        String uniqueId = date + FirebaseAuth.getInstance().getCurrentUser().getUid();


                        prodectClass.setId(uniqueId);
                        prodectClass.setImageString(imageString);
                        //------

                        userProductClass.setId(uniqueId);
                        userProductClass.setImageString(imageString);
//                    DateFormat dtForm = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.");
//                    String date = dtForm.format(Calendar.getInstance().getTime());
//                    String fileName = date + FirebaseAuth.getInstance().getCurrentUser().getUid();


                        uploadImageToFirebase(imageString, prodectClass.getId());//upload Image To Firebase:
                        addNewProdectToFirebaseStoreUser(userProductClass);  //add to fire store User products
                        addNewProdectToFirebaseStorePublic(prodectClass);  //add to fire store public products

                        addToReciclerFunction(userProductClass);//add new item

                    } else
                        Toast.makeText(getActivity(), "select Image ", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "enter the data ", Toast.LENGTH_SHORT).show();

            }
        });
        bottomSheetDialog.show();
        bottomSheetBinding.pageTitle.setText("Add New Prodect");
//        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().getAttributes().windowAnimations = com.facebook.R.style.Base_DialogWindowTitle_AppCompat;
//        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

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
    private void showBottomSheetDialog_edit(String name, String description, String imageName, int position, String myImageString) {

        if (bottomSheetDialog == null) {

            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        }
        bottomSheetBinding.imageView.setImageResource(R.drawable.upload_image);


        retrieveImageFromFireStore(imageName);//retrieve Image From FireStore and put in a image sheet
        bottomSheetBinding.newProdectNameEt.setText(name);
        bottomSheetBinding.newProdectDecriptionEt.setText(description);


        Log.e("image", "showBottomSheetDialog_edit: |||" + arrayList.get(position).getImageString() + "|||");

        bottomSheetBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldProdect = new ProdectClass(name, description);


                pNameEt = bottomSheetBinding.newProdectNameEt.getText().toString();
                pDescriptionEt = bottomSheetBinding.newProdectDecriptionEt.getText().toString();

                //  text input  validation:
                if (!pNameEt.isEmpty() && !pDescriptionEt.isEmpty()) {
                    if (uriUpload != null) {
                        uploadImageToFirebase(imageString, imageName);//upload Image To Firebase:
//
                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        prodectClass.setImageString(imageString);
                        //----------------------
                        userProductClass = new ProdectClass(pNameEt, pDescriptionEt);
                        userProductClass.setImageString(imageString);

                        uriUpload = null;
                    } else {
//
                        prodectClass = new ProdectClass(pNameEt, pDescriptionEt);
                        prodectClass.setImageString(myImageString);
                        //-----------------
                        userProductClass = new ProdectClass(pNameEt, pDescriptionEt);
                        userProductClass.setImageString(myImageString);

                        Log.e("empty", "onClick: |||" + arrayList.get(position).getImageString() + "||old:" + myImageString);

                    }

                    //todo:2
                    updateDataInFirebaseUser(oldProdect, mapFuc(userProductClass));////update data in fire store user:
                    updateDataInFirebasePublic(oldProdect, mapFuc(prodectClass));////update data in fire store Public:


                    arrayList.get(position).setProdectName(userProductClass.getProdectName());
                    arrayList.get(position).setProdectDescription(userProductClass.getProdectDescription());
                    arrayList.get(position).setImageString(userProductClass.getImageString());
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
    private void addNewProdectToFirebaseStoreUser(ProdectClass prodectClass) {
        db.collection("profileData").document(documentReference.getId())
                .collection("userArrayListProducts").add(prodectClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        WriteBatch batch = db.batch();
////
//                        DocumentReference documentReference1=documentReference.collection("userArrayListProducts").document(documentReference.getId());
////
//                        batch.set(documentReference1,prodectClass);
//                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Log.d(TAG, "onSuccess: the product added successfully to products document ");
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "onFailure: the product added failure!!"+e.getMessage());
//                            }
//                        });

                        Toast.makeText(getActivity(), "the prodect added successfully  to firebase ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document" + e.getMessage(), e);
                    }
                });

    }

    private void addNewProdectToFirebaseStorePublic(ProdectClass prodectClassPublic) {
        db.collection("products").add(prodectClassPublic)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(getActivity(), "the prodect added successfully  to firebase Public", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document" + e.getMessage(), e);
                    }
                });

    }

    //todo:3
    //update in fire store user:
    private void updateDataInFirebaseUser(ProdectClass oldProduct, Map map) {

        userCollection.document(firebaseUser.getUid()).collection("userArrayListProducts")

                .whereEqualTo("prodectName", oldProduct.getProdectName())
                .whereEqualTo("prodectDescription", oldProduct.getProdectDescription())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (int i = 0; i < querySnapshot.size(); i++) {
                            if (querySnapshot.size() > 0) {
                                // Get the document reference of the first matching document
                                DocumentReference documentReference = querySnapshot.getDocuments().get(i).getReference();

                                // Update the document using the document reference
                                documentReference.set(map, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Document updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Error updating document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "onFailure: " + e.getMessage(), e);
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), "No matching document found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //update in fire store public:
    private void updateDataInFirebasePublic(ProdectClass oldProduct, Map map) {

        publicCollectionRef
                .whereEqualTo("prodectName", oldProduct.getProdectName())
                .whereEqualTo("prodectDescription", oldProduct.getProdectDescription())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    publicCollectionRef.document(document.getId())
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


//       collectionReference.document(firebaseUser.getUid()).collection("userArrayListProducts")
//                .whereEqualTo("prodectName", oldProduct.getProdectName())
//                .whereEqualTo("prodectDescription", oldProduct.getProdectDescription())
//                .get()

//        db.collection("profileData")
//                .document(firebaseUser.getUid())
//                .collection("userArrayListProducts")
//                .document(documentReference.getId())
//                .set(mapFuc(userProductClass), SetOptions.merge())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                     Toast.makeText(getActivity(), "Document updated successfully", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), "Error updating document"+e.getMessage() + e, Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onFailure: e.getMessage()"+e.getMessage());
//                    }
//                });
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            if (!task.getResult().getDocuments().isEmpty()) {
//                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                                    collectionReference.document(document.getId())
//                                            .set((map), SetOptions.merge())
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//
//                                                    Toast.makeText(getActivity(), "Document updated successfully", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.e("eroooor", "onFailure: " + e);
//                                                    Toast.makeText(getActivity(), "Error updating document"+e.getMessage() + e, Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            });
//                                }
//                            }
//                        } else {
//
//                            Log.e(TAG, "Error getting documents.", task.getException());
//                            Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });


    //todo:1
    private Map<String, Object> mapFuc(ProdectClass prodectClass) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("prodectName", prodectClass.getProdectName());
        productMap.put("prodectDescription", prodectClass.getProdectDescription());
        productMap.put("favoriteBool", prodectClass.isFavoriteBool());
//        if ()
        productMap.put("imageString", prodectClass.getImageString());


        Log.e("map", "mapFuc:imageString " + prodectClass.getImageString());
//        productMap.put("productImage",productImage);

//        updateDataInFirebase();
        return productMap;
    }


    //read all data from firebase Store user:
    private void readAllDataFromFirebase() {
        db.collection("profileData").document(firebaseUser.getUid()).collection("userArrayListProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userProductClass = document.toObject(ProdectClass.class);

                                addToReciclerFunction(userProductClass);//"we handling the data here in recycler"

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
        String image = arrayList.get(position).getId();
        String imageString = arrayList.get(position).getImageString();
        name = arrayList.get(position).getProdectName();
        description = arrayList.get(position).getProdectDescription();

        adapter.notifyItemChanged(position);
        adapter.getItemCount();


        showBottomSheetDialog_edit(name, description, image, position, imageString);

        Log.e(TAG, "getPositionInterface: imageString" + imageString);


//        arrayList.set(position,prodectClass);


    }

    //delete from fire store :
    void deleteFromFireStoreUser(ProdectClass prodectClass) {
        userCollection.document(firebaseUser.getUid()).collection("userArrayListProducts").
                whereEqualTo("prodectName", prodectClass.getProdectName())
                .whereEqualTo("prodectDescription", prodectClass.getProdectDescription())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (int i = 0; i < querySnapshot.size(); i++) {
                            if (querySnapshot.size() > 0) {
                                // Get the document reference of the first matching document
                                DocumentReference documentReference = querySnapshot.getDocuments().get(i).getReference();
                                // Update the document using the document reference
                                documentReference.delete()
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
                            }else
                        Toast.makeText(getActivity(), "Error getting up document", Toast.LENGTH_SHORT).show();

                    }
                }
    });
}

    void deleteFromFireStorePublic(ProdectClass prodectClass) {
        publicCollectionRef.
                whereEqualTo("prodectName", prodectClass.getProdectName())
                .whereEqualTo("prodectDescription", prodectClass.getProdectDescription())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    publicCollectionRef.document(document.getId()).delete()
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
        String imageId = arrayList.get(position).getId();

        prodectClass = new ProdectClass(name, description);


        deleteImageFromFirebaseStorage(imageId);
        deleteFromFireStoreUser(prodectClass);
        deleteFromFireStorePublic(prodectClass);
        arrayList.remove(position);//delete item:
        adapter.notifyItemRemoved(position);
        adapter.getItemCount();



    }

    //update heart :
    @Override
    public void heartPosition(int position, Boolean status) {
        String image = arrayList.get(position).getId();
        name = arrayList.get(position).getProdectName();
        description = arrayList.get(position).getProdectDescription();
        String id = arrayList.get(position).getId();


        arrayList.get(position).setFavoriteBool(status);
        updateDataInFirebaseF(status, id);

//        Log.e("heart", "heartPosition: position::"+id +"======idI===="+image );


        Toast.makeText(getContext(), "its added to favorite activity", Toast.LENGTH_SHORT).show();


    }


    //update in fire store fav:
    private void updateDataInFirebaseF(boolean b, String id) {
        Map<String, Boolean> productMap = new HashMap<>();
        productMap.put("favoriteBool", b);
        userCollection.document(firebaseUser.getUid()).collection("userArrayListProducts")
                .whereEqualTo("id", id).
                get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (int i = 0; i < querySnapshot.size(); i++) {
                            if (querySnapshot.size() > 0) {
                                // Get the document reference of the first matching document
                                DocumentReference documentReference = querySnapshot.getDocuments().get(i).getReference();

                                // Update the document using the document reference
                                documentReference.set(productMap, SetOptions.merge())

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

                    }
                });


    }

    //upload Image To Firebase
    private void uploadImageToFirebase(String imageStringUri, String imageName) {
        StorageReference storageRef2 = FirebaseStorage.getInstance()
                .getReference().child("productsImage/" + imageName);
//        storageRef2.getName();
        uploadTask = storageRef2.putFile(Uri.parse(imageStringUri));

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

        StorageReference storageRef = firebaseStorage.getReference().child("productsImage/" + imageName);
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
    private void deleteImageFromFirebaseStorage(String imageName) {
        StorageReference sr = firebaseStorage.getReference().child("productsImage/" + imageName);
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
//            String  imageString =arrayList.get(i).getImageString();
//
//            retrieveImageFromFireStore(imageId);
////            uploadImageToFirebase(imageString,imageId);
//
//            Log.e("imageLoop", "onViewCreated: "+"("+i+")"+imageId+( arrayList.size())+"imageString::"+((imageString)));
//
//        }
        /////////////////////////////////////////////

    }

}