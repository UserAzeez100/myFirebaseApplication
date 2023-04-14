package com.example.myfirebaseapplication.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfirebaseapplication.databinding.ProdectItemBinding;

import java.util.ArrayList;


public class RecyclerProductsAdapter extends RecyclerView.Adapter<RecyclerProductsAdapter.ProdectViweHolder> {

    ArrayList<ProdectClass> prodectArrayList;
    MyInterFace myInterFace;


    public RecyclerProductsAdapter(ArrayList<ProdectClass> prodectArrayList, MyInterFace myInterFace) {
        this.prodectArrayList = prodectArrayList;
        this.myInterFace = myInterFace;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ProdectViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProdectItemBinding binding = ProdectItemBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent, false);

        return new ProdectViweHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdectViweHolder holder, int position) {

        int pos = position;
        ProdectClass prodectClass = prodectArrayList.get(pos);
        holder.prodectDescriptionTv.setText(prodectClass.getProdectDescription());
        holder.productNameTv.setText(prodectClass.getProdectName());

        if (prodectClass.isFavoriteBool()) {

            holder.heartIcon.setChecked(true);
        }


        holder.heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myInterFace.heartPosition(pos, !prodectClass.isFavoriteBool());

                if (holder.heartIcon.isChecked()) {


                    holder.heartIcon.setChecked(true);


                } else {
                    holder.heartIcon.setChecked(false);
                }


            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterFace.deletePosition(pos);


            }
        });
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myInterFace.getPositionInterface(pos);


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        String imageUrl = prodectArrayList.get(pos).getImageString();

            Glide.with(holder.itemView)
                    .load(imageUrl).circleCrop()
                    .into(holder.imgProductPhoto);


    }

    @Override
    public int getItemCount() {
        return prodectArrayList.size();
    }


    public class ProdectViweHolder extends RecyclerView.ViewHolder {

        TextView productNameTv, prodectDescriptionTv;
        ImageView editIcon, deleteIcon;
        ImageView imgProductPhoto;
        CheckBox heartIcon;


        public ProdectViweHolder(@NonNull ProdectItemBinding binding) {
            super(binding.getRoot());

            imgProductPhoto = binding.imgProductPhoto;
            productNameTv = binding.productNameTv;
            prodectDescriptionTv = binding.prodectDescriptionTv;
            heartIcon = binding.heartIcon;
            editIcon = binding.editIcon;
            deleteIcon = binding.deleteIcon;

//            String imageUrl = photo.get(position);
//            ImageView imageView = holder.imageView;
//


        }

    }




}
