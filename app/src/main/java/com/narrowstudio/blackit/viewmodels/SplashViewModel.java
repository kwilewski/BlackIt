package com.narrowstudio.blackit.viewmodels;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashViewModel extends ViewModel {

    private MutableLiveData<Boolean> isSplash = new MutableLiveData<>();
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    public void init(){

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                isSplash.postValue(true);
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    public LiveData<Boolean> getIsSplash(){
        return isSplash;
    }




}
