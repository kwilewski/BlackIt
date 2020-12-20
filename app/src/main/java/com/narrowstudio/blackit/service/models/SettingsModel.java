package com.narrowstudio.blackit.service.models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.narrowstudio.blackit.R;

import java.util.ArrayList;
import java.util.Set;

public class SettingsModel {

    private SharedPreferences mPreferences;
    private Context context;
    private static final String SHARED_PREFERENCES = "PREFS";

    public MutableLiveData<SharedPreferences> getPreferences(){
        MutableLiveData<SharedPreferences> data = new MutableLiveData<>();
        data.setValue(mPreferences);
        return data;
    }



    public SettingsModel(Context cont){
        this.context = cont;
        setPreferences();
    }

    private void setPreferences(){
        mPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), context.MODE_PRIVATE);
    }

    public void setUnlockMode(int mode) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("unlock_mode", mode);
        editor.apply();
    }

    public void setIsFloating(boolean sett){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("floating_mode", sett);
        editor.apply();
    }

    public void setIsClockEnabled(boolean sett){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("clock", sett);
        editor.apply();
    }

    public void setIsBrightnessOff(boolean sett){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("brightness", sett);
        editor.apply();
    }

    public void setIsButtonsEnabled(boolean sett){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("buttons", sett);
        editor.apply();
    }

    public void setKnockCode(String codeString){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("knock_code", codeString);
        editor.apply();
    }

    public void setIconSize(int size){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("icon_size", size);
        editor.apply();
    }


}
