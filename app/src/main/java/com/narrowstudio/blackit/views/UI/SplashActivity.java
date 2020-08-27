package com.narrowstudio.blackit.views.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.SplashViewModel;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel mSplashViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mSplashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        mSplashViewModel.init();
        mSplashViewModel.getIsSplash().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                openMainActivity();
            }
        });
    }

    private void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
