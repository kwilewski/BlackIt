package com.narrowstudio.blackit.views.UI;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.SettingsViewModel;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SettingsViewModel mSettingsViewModel;
    private SwitchCompat clockSwitch, buttonsSwitch, brightnessSwitch;
    private boolean isClock, isButtons, isBrightness;
    private Button knockButton;
    private Spinner unlockModeSpinner, iconSizeSpinner;
    private ArrayAdapter unlockSpinnerAdapter, iconSizeAdapter;
    int unMode = 0, iconSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adViewSettings);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        clockSwitch = (SwitchCompat) findViewById(R.id.settings_clock_switch);
        buttonsSwitch = (SwitchCompat) findViewById(R.id.settings_buttons_switch);
        brightnessSwitch = (SwitchCompat) findViewById(R.id.settings_brightness_switch);
        knockButton = (Button) findViewById(R.id.knock_options_button);
        unlockModeSpinner = (Spinner) findViewById(R.id.unlock_mode_selection_spinner);
        iconSizeSpinner = (Spinner) findViewById(R.id.icon_size_spinner);

        mSettingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        mSettingsViewModel.init();


        //-------------------------------------------------------------------------------------------------------------clock
        mSettingsViewModel.getIsClockEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isClock = mSettingsViewModel.getIsClockEnabledBool();
                clockSwitch.setChecked(isClock);
            }
        });

        clockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockStatusChange();
            }
        });


        //-------------------------------------------------------------------------------------------------------------brightness
        mSettingsViewModel.getIsBrightnessOff().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isBrightness = mSettingsViewModel.getIsBrightnessOffBool();
                brightnessSwitch.setChecked(isBrightness);
            }
        });

        brightnessSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightnessStatusChange();
            }
        });


        //-------------------------------------------------------------------------------------------------------------buttons
        mSettingsViewModel.getIsButtonsEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isButtons = mSettingsViewModel.getIsButtonsEnabledBool();
                buttonsSwitch.setChecked(isButtons);
            }
        });

        buttonsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsStatusChange();
            }
        });

        //setting up unlock spinner
        unlockSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.select_unlock_array, R.layout.my_spinner_item);
        unlockSpinnerAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        unlockModeSpinner.setAdapter(unlockSpinnerAdapter);
        unlockModeSpinner.setOnItemSelectedListener(this);
        unlockSpinnerAdapter.notifyDataSetChanged();
        unMode = mSettingsViewModel.getUnlockModeInt();
        unlockModeSpinner.setSelection(unMode);

        //setting up icon size spinner
        iconSizeAdapter = ArrayAdapter.createFromResource(this, R.array.icon_size_array, R.layout.my_spinner_item);
        iconSizeAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        iconSizeSpinner.setAdapter(iconSizeAdapter);
        iconSizeSpinner.setOnItemSelectedListener(this);
        iconSizeAdapter.notifyDataSetChanged();
        //iconSize:
        // 0 - small
        // 1 - medium
        // 2 - big
        iconSize = mSettingsViewModel.getIconSizeInt();
        iconSizeSpinner.setSelection(iconSize);




        knockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startKnockActivity();
            }
        });



    }

    private void clockStatusChange(){
        isClock = !mSettingsViewModel.getIsClockEnabledBool();
        clockSwitch.setChecked(isClock);
        mSettingsViewModel.setIsClockEnabled(isClock);
    }

    private void brightnessStatusChange(){
        isBrightness = !mSettingsViewModel.getIsBrightnessOffBool();
        brightnessSwitch.setChecked(isBrightness);
        mSettingsViewModel.setIsBrightnessOff(isBrightness);
    }

    private void buttonsStatusChange(){
        isButtons = !mSettingsViewModel.getIsButtonsEnabledBool();
        buttonsSwitch.setChecked(isButtons);
        mSettingsViewModel.setIsButtonsEnabled(isButtons);
    }

    private void startKnockActivity(){
        Intent intent = new Intent(this,  KnockActivity.class);
        startActivity(intent);
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == unlockModeSpinner) {
            mSettingsViewModel.setUnlockMode(i);
        } else if (adapterView == iconSizeSpinner) {
            mSettingsViewModel.setIconSize(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
