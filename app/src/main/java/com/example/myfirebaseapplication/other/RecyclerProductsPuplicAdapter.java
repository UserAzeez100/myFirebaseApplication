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
import com.example.myfirebaseapplication.databinding.ProdectsPublicItemBinding;

import java.util.ArrayList;


public class RecyclerProductsPuplicAdapter extends RecyclerView.Adapter<RecyclerProductsPuplicAdapter.ProdectViweHolder> {

    ArrayList<ProdectClass> prodectArrayList;
    MyInterFace myInterFace;


    public RecyclerProductsPuplicAdapter(ArrayList<ProdectClass> prodectArrayList, MyInterFace myInterFace) {
        this.prodectArrayList = prodectArrayList;
        this.myInterFace = myInterFace;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ProdectViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProdectsPublicItemBinding binding = ProdectsPublicItemBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent, false);

        return new ProdectViweHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdectViweHolder holder, int position) {


        ProdectClass prodectClass = prodectArrayList.get(position);
        holder.prodectDescriptionTv.setText(prodectClass.getProdectDescription());
        holder.productNameTv.setText(prodectClass.getProdectName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        String imageUrl = prodectArrayList.get(position).getImageString();

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
        ImageView imgProductPhoto;


        public ProdectViweHolder(@NonNull ProdectsPublicItemBinding binding) {
            super(binding.getRoot());

            imgProductPhoto = binding.imgProductPhoto;
            productNameTv = binding.productNameTv;
            prodectDescriptionTv = binding.prodectDescriptionTv;


//            String imageUrl = photo.get(position);
//            ImageView imageView = holder.imageView;
//


        }

    }




}
