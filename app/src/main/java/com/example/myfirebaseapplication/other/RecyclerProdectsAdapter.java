package com.example.myfirebaseapplication.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfirebaseapplication.databinding.ProdectItemBinding;

import java.util.ArrayList;


public class RecyclerProdectsAdapter extends RecyclerView.Adapter<RecyclerProdectsAdapter.ProdectViweHolder> {

    ArrayList<ProdectClass> prodectArrayList;


    public RecyclerProdectsAdapter(ArrayList<ProdectClass> prodectArrayList) {
        this.prodectArrayList = prodectArrayList;

    }

    @NonNull
    @Override
    public ProdectViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProdectItemBinding binding=ProdectItemBinding.inflate(LayoutInflater.from(parent.getContext())
                ,parent,false);

        return new ProdectViweHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdectViweHolder holder, int position) {
        int pos =position;
        ProdectClass prodectClass = prodectArrayList.get(pos);
        holder.prodectDescriptionTv.setText(prodectClass.getProdectDescription());
        holder.productNameTv.setText(prodectClass.getProdectName());

        holder.heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });






        String imageUrl =  prodectArrayList.get(pos).getImageString();

//            Glide.with(holder.itemView)
//                    .load(imageUrl)
//                    .into(holder.photo);




    }

    @Override
    public int getItemCount() {
        return prodectArrayList.size();
    }


    public class ProdectViweHolder extends RecyclerView.ViewHolder{

        TextView productNameTv,prodectDescriptionTv,deleteIcon;
        ImageView heartIcon,editIcon;
        ImageView imgProductPhoto;


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
