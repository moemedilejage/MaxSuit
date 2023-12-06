package com.imegga.suitcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.imegga.suitcase.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Fragment fragment;
    private FrameLayout frameLayout;
    private float dY;

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        frameLayout = findViewById(R.id.frame_layout);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            //Navigation Bar
            if (itemId == R.id.home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.profile) {
                fragment = new ProfileFragment();
            } else if (itemId == R.id.add) {
                fragment = new AddFragment();
            } else if (itemId == R.id.activity) {
                fragment = new ActivityFragment();
            } else {
                fragment = new HomeFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
            return true;
        });
    }
    private void replaceFragment() {
        HomeFragment newFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, newFragment)
                .commit();
    }
}