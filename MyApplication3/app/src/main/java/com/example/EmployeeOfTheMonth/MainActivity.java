package com.example.EmployeeOfTheMonth;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.EmployeeOfTheMonth.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new SectionsPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText("Camera");
                        tab.setIcon(R.drawable.ic_photo);
                        break;
                    }
                        case 1: {
                            tab.setText("Edit");
                            tab.setIcon(R.drawable.ic_gallery);
                            break;
                        }
                            case 2: {
                                tab.setText("Share");
                                tab.setIcon(R.drawable.ic_edit);
                                break;
                            }
                        }
                     }
                }
                );
        tabLayoutMediator.attach();
    }
}