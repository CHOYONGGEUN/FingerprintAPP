package com.example.fingerprintapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 4;
    private CircleIndicator3 mIndicator;
    private ImageView beforeImageView;
    private ImageView afterImageView;

    private Handler slideHandler = new Handler();
    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = mPager.getCurrentItem();
            int nextItem = currentItem + 1;
            mPager.setCurrentItem(nextItem);
            slideHandler.postDelayed(this, 3000); // 3초 후에 다음 슬라이드 예약
        }
    };

    private boolean isIrisBlurringOn = false; // 기본값을 false로 설정
    private boolean isFingerprintBlurringOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager2 설정
        mPager = findViewById(R.id.viewpager);
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        // Indicator 설정
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page, 0);
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // 초기 페이지 설정
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(4);

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

        // 자동 슬라이드 시작
        slideHandler.postDelayed(slideRunnable, 3000);

        // 이미지 뷰 설정
        beforeImageView = findViewById(R.id.beforeImage);
        afterImageView = findViewById(R.id.afterImage);

        // 버튼 설정
        Button buttonIris = findViewById(R.id.button4);
        Button buttonFingerprint = findViewById(R.id.button3);

        buttonIris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIrisBlurringOn = !isIrisBlurringOn;
                buttonIris.setText(isIrisBlurringOn ? "홍채 블러링 ON" : "홍채 블러링 OFF");
                buttonIris.setBackgroundResource(isIrisBlurringOn ? R.drawable.rounded_button_blue : R.drawable.rounded_button_grey);
                updateAfterImage();
            }
        });

        buttonFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFingerprintBlurringOn = !isFingerprintBlurringOn;
                buttonFingerprint.setText(isFingerprintBlurringOn ? "지문 블러링 ON" : "지문 블러링 OFF");
                buttonFingerprint.setBackgroundResource(isFingerprintBlurringOn ? R.drawable.rounded_button_blue : R.drawable.rounded_button_grey);
                updateAfterImage();
            }
        });

        // 초기 상태 기반으로 afterImageView 설정
        updateAfterImage();
    }

    private void updateAfterImage() {
        if (isIrisBlurringOn || isFingerprintBlurringOn) {
            afterImageView.setImageResource(R.drawable.blind);
        } else {
            afterImageView.setImageResource(R.drawable.people);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 메모리 누수를 방지하기 위해 콜백 제거
        slideHandler.removeCallbacks(slideRunnable);
    }
}
