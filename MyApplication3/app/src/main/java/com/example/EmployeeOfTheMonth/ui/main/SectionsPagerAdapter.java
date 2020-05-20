package com.example.EmployeeOfTheMonth.ui.main;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.EmployeeOfTheMonth.CameraFragment;
import com.example.EmployeeOfTheMonth.ShareFragment;
import com.example.EmployeeOfTheMonth.EditFragment;

public class SectionsPagerAdapter extends FragmentStateAdapter{

        public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
            case 0:
                return new CameraFragment();
            case 1:
                return new EditFragment();
            default:
                return new ShareFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
}