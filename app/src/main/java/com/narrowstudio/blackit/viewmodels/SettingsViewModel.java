package com.narrowstudio.blackit.viewmodels;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.BoringLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.narrowstudio.blackit.service.models.SettingsModel;
import com.narrowstudio.blackit.views.UI.FloatingViewService;

import static androidx.core.content.ContextCompat.getSystemService;

public class SettingsViewModel extends AndroidViewModel {

    private SharedPreferences mPreferences;
    private Context context;
    private SettingsModel mSettings;

    private boolean isFloatingSetBool = false, isClockBool = true, isBrightnessBool = false, isButtonsBool = true,
            isRotationBool = false, isPortraitBool = false;
    private int unlockModeInt = 0, iconSizeInt = 0;

    private MutableLiveData<SharedPreferences> mPrefLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFloatingSet = new MutableLiveData<>();
    private MutableLiveData<Integer> unlockMode = new MutableLiveData<>();
    private MutableLiveData<Integer> iconSize = new MutableLiveData<>();
    /*
    0 - small
    1 - medium
    2 - big
     */

    private MutableLiveData<Boolean> isClock = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBrightness = new MutableLiveData<>();
    private MutableLiveData<Boolean> isButtons = new MutableLiveData<>();
    private MutableLiveData<Boolean> isServiceRunning = new MutableLiveData<>();
    /*
    0- single click
    1- double click
    2- knock code
     */

    private MutableLiveData<Integer> screenMode = new MutableLiveData<>();
    /*
    0- black screen
    1- clock
     */

    private MutableLiveData<Boolean> isRotation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPortrait = new MutableLiveData<>();

    private long startTime = 0,millis;
    private int millisSet=500;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            int millisInt = (int) millis;
            if (millisInt > millisSet) {
                startTime = System.currentTimeMillis();
                isMyServiceRunning();
            }
            timerHandler.postDelayed(this, 50);
        }
    };

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
    }

    public void init(){
        mSettings = new SettingsModel(context);
        mPreferences = mSettings.getPreferences().getValue();
        mPrefLD.postValue(mPreferences);
        isFloatingSetBool = mPreferences.getBoolean("floating_mode", false);
        unlockModeInt = mPreferences.getInt("unlock_mode", 0);
        iconSizeInt = mPreferences.getInt("icon_size", 1);
        isFloatingSet.postValue(isFloatingSetBool);
        unlockMode.postValue(mPreferences.getInt("unlock_mode", 0));
        iconSize.postValue(mPreferences.getInt("icon_size", 1));
        screenMode.postValue(mPreferences.getInt("screen_mode", 0));
        isClockBool = mPreferences.getBoolean("clock", true);
        isClock.postValue(isClockBool);
        isBrightnessBool = mPreferences.getBoolean("brightness", false);
        isBrightness.postValue(isBrightnessBool);
        isButtonsBool = mPreferences.getBoolean("buttons", false);
        isButtons.postValue(isButtonsBool);
        isRotationBool = mPreferences.getBoolean("rotation", false);
        isRotation.postValue(isRotationBool);
        isPortraitBool = mPreferences.getBoolean("portrait", false);
        isPortrait.postValue(isPortraitBool);
        isButtons.postValue(isPortraitBool);
        isServiceRunning.postValue(false);
        isMyServiceRunning();


        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);
    }

    public SharedPreferences getPreferences(){
        return mPreferences;
    }

    public LiveData<Boolean> getIsFloating(){
        return isFloatingSet;
    }

    public boolean getIsFloatingBoolean(){
        return isFloatingSetBool;
    }

    public LiveData<Integer> getUnlockMode(){
        return unlockMode;
    }

    public int getUnlockModeInt() {
        int ret = mPreferences.getInt("unlock_mode", 0);
        return ret;
    }

    public void setUnlockMode(int mode) {
        mSettings.setUnlockMode(mode);
        unlockMode.postValue(mode);
        unlockModeInt = mode;
    }

    public int getIconSizeInt() {
        int ret = mPreferences.getInt("icon_size", 1);
        return ret;
    }

    public void setIconSize(int size) {
        mSettings.setIconSize(size);
        iconSize.postValue(size);
        iconSizeInt = size;
    }

    public LiveData<Integer> getScreenMode(){
        return screenMode;
    }

    public LiveData<SharedPreferences> getPrefLD(){
        return mPrefLD;
    }

    public void setIsFloating(boolean sett){
        isFloatingSetBool = sett;
        mSettings.setIsFloating(sett);
        isFloatingSet.postValue(sett);
    }


    //clock visibility
    public LiveData<Boolean> getIsClockEnabled(){
        return isClock;
    }

    public boolean getIsClockEnabledBool(){
        return mPreferences.getBoolean("clock", true);
    }

    public void setIsClockEnabled(boolean sett){
        isClockBool = !isClockBool;
        mSettings.setIsClockEnabled(isClockBool);
        isClock.postValue(isClockBool);
    }


    //dimming the brightness
    public LiveData<Boolean> getIsBrightnessOff(){
        return isBrightness;
    }

    public boolean getIsBrightnessOffBool(){
        return mPreferences.getBoolean("brightness", false);
    }

    public void setIsBrightnessOff(boolean sett){
        isBrightnessBool = !isBrightnessBool;
        mSettings.setIsBrightnessOff(sett);
        isBrightness.postValue(sett);
    }

    public LiveData<Boolean> getIsButtonsEnabled(){
        return isButtons;
    }

    public boolean getIsButtonsEnabledBool(){
        return mPreferences.getBoolean("buttons", false);
    }

    public void setIsButtonsEnabled(boolean sett){
        isButtonsBool = !isButtonsBool;
        mSettings.setIsButtonsEnabled(isButtonsBool);
        isButtons.postValue(isButtonsBool);
    }

    public void setServiceNotRunning(){
        isServiceRunning.postValue(false);
    }


    //--------------------------------------------------------------------------------------- rotation
    public boolean getIsRotationBool() {
        boolean ret = mPreferences.getBoolean("rotation", false);
        return ret;
    }

    public void setIsRotation(boolean sett) {
        mSettings.setRotation(sett);
        isRotation.postValue(sett);
        isRotationBool = sett;
    }

    public LiveData<Boolean> getIsRotation(){
        return isRotation;
    }

    //-------------------------------------------------------------------------------------- portrait
    public boolean getIsPortraitBool() {
        boolean ret = mPreferences.getBoolean("portrait", false);
        return ret;
    }

    public void setIsPortrait(boolean sett) {
        mSettings.setPortrait(sett);
        isPortrait.postValue(sett);
        isPortraitBool = sett;
    }

    public LiveData<Boolean> getIsPortrait(){
        return isPortrait;
    }







    public void isMyServiceRunning() {
        Class<?> serviceClass = FloatingViewService.class;
        boolean wasChanged = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                isServiceRunning.postValue(true);
                wasChanged = true;
            }
        }
        if (!wasChanged) {
            isServiceRunning.postValue(false);
        }
    }


    public LiveData<Boolean> getIsMyServiceRunning() {
        return isServiceRunning;
    }

    public void killFloatingService() {
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), FloatingViewService.class);
        context.getApplicationContext().stopService(intent);
        isMyServiceRunning();
    }





}
