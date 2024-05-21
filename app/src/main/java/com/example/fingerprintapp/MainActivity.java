package com.example.fingerprintapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 4;
    private CircleIndicator3 mIndicator;

    private Handler slideHandler = new Handler();
    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = mPager.getCurrentItem();
            int nextItem = currentItem + 1;
            mPager.setCurrentItem(nextItem);
            slideHandler.postDelayed(this, 3000); // Schedule the next slide after 3 seconds
        }
    };

    private boolean isIrisBlurringOn = true;
    private boolean isFingerprintBlurringOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager2
        mPager = findViewById(R.id.viewpager);
        // Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        // Indicator
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page, 0);
        // ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Initial page setup
        mPager.setCurrentItem(1000); // Start point
        mPager.setOffscreenPageLimit(4); // Max image count

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % num_page);
            }
        });

        // Start the auto-slide
        slideHandler.postDelayed(slideRunnable, 3000);

        // Setup buttons
        Button buttonIris = findViewById(R.id.button4);
        Button buttonFingerprint = findViewById(R.id.button3);

        buttonIris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIrisBlurringOn = !isIrisBlurringOn;
                buttonIris.setText(isIrisBlurringOn ? "홍채 블러링 ON" : "홍채 블러링 OFF");
                buttonIris.setBackgroundResource(isIrisBlurringOn ? R.drawable.rounded_button_blue : R.drawable.rounded_button_grey);
            }
        });

        buttonFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFingerprintBlurringOn = !isFingerprintBlurringOn;
                buttonFingerprint.setText(isFingerprintBlurringOn ? "지문 블러링 ON" : "지문 블러링 OFF");
                buttonFingerprint.setBackgroundResource(isFingerprintBlurringOn ? R.drawable.rounded_button_blue : R.drawable.rounded_button_grey);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to avoid memory leaks
        slideHandler.removeCallbacks(slideRunnable);
    }
}
