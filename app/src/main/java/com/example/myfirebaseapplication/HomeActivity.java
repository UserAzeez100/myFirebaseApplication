package com.example.myfirebaseapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.myfirebaseapplication.databinding.ActivityHomeBinding;
import com.example.myfirebaseapplication.fragments.FavoriteFragment;
import com.example.myfirebaseapplication.fragments.ProdectFragment;
import com.example.myfirebaseapplication.fragments.ProfileFragment;
import com.example.myfirebaseapplication.other.PagerAdapter;
import com.example.myfirebaseapplication.other.ProdectClass;
import com.example.myfirebaseapplication.other.TapFragmentClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_con2,new ViewFragment2()).commit();
        PagerAdapter adapter=new PagerAdapter(this);
        adapter.addTapFragment(new TapFragmentClass("Prodect", ProdectFragment.newInstance("","")));
        adapter.addTapFragment(new TapFragmentClass("Favorite", FavoriteFragment.newInstance("","")));
        adapter.addTapFragment(new TapFragmentClass("Profile", ProfileFragment.newInstance("","")));








//        binding.tapLayout.setupWithViewPager(binding.viewPager2);
        //connect the bager with a tap by position:
        binding.viewPager2.setAdapter(adapter);
        new TabLayoutMediator(binding.tapLayout, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.tapsArray.get(position).getTapName());
            }
        }).attach();

        binding.viewPager2.setCurrentItem(0,false);









    }


}