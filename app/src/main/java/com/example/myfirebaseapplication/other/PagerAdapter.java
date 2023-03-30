package com.example.myfirebaseapplication.other;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStateAdapter {
    public ArrayList<TapFragmentClass> tapsArray =new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    public void addTapFragment(TapFragmentClass tapFragmentClass){
        this.tapsArray.add(tapFragmentClass);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return tapsArray.get(position).getFragment() ;
    }




    @Override
    public int getItemCount() {
        return tapsArray.size();
    }
}
