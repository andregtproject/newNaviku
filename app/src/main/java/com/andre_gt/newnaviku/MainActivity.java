package com.andre_gt.newnaviku;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.andre_gt.newnaviku.SightHub.HomeMobilityFragment;
import com.andre_gt.newnaviku.VoiceMate.HomeTextFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Deklarasikan instance fragment
    private HomeMobilityFragment homeMobilityFragment;
    private HomeTextFragment homeTextFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Inisialisasi instance fragment
        homeMobilityFragment = new HomeMobilityFragment();
        homeTextFragment = new HomeTextFragment();

        // Menangani item yang dipilih pada BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_mobility:
                    loadFragment(homeMobilityFragment);
                    return true;
                case R.id.navigation_text_recognition:
                    loadFragment(homeTextFragment);
                    return true;
            }
            return false;
        });

        // Menampilkan fragment pertama saat aplikasi pertama kali dibuka
        bottomNavigationView.setSelectedItemId(R.id.navigation_mobility);
    }

    // Fungsi untuk mengganti fragment di dalam contentFrame
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Keluar aplikasi
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            finish();
        }
    }
}
