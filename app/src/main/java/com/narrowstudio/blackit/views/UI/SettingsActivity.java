package com.narrowstudio.blackit.views.UI;

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

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SettingsViewModel mSettingsViewModel;
    private SwitchCompat clockSwitch, buttonsSwitch, brightnessSwitch;
    private boolean isClock, isButtons, isBrightness;
    private Button knockButton;
    private Spinner unlockModeSpinner;
    private ArrayAdapter unlockSpinnerAdapter;
    int unMode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clockSwitch = (SwitchCompat) findViewById(R.id.settings_clock_switch);
        buttonsSwitch = (SwitchCompat) findViewById(R.id.settings_buttons_switch);
        brightnessSwitch = (SwitchCompat) findViewById(R.id.settings_brightness_switch);
        knockButton = (Button) findViewById(R.id.knock_options_button);
        unlockModeSpinner = (Spinner) findViewById(R.id.unlock_mode_selection_spinner);

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

        unlockSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.select_unlock_array, R.layout.my_spinner_item);
        unlockSpinnerAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        unlockModeSpinner.setAdapter(unlockSpinnerAdapter);
        unlockModeSpinner.setOnItemSelectedListener(this);
        LiveData<Integer> unModeLD = mSettingsViewModel.getUnlockMode();
        unModeLD.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setKnockButton();
            }
        });
        unMode = mSettingsViewModel.getUnlockModeInt();
        unlockModeSpinner.setSelection(unMode);
        setKnockButton();



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

    private void setKnockButton(){
        unMode = mSettingsViewModel.getUnlockModeInt();
        if (unMode != 2){
            knockButton.setEnabled(false);
        } else {
            knockButton.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mSettingsViewModel.setUnlockMode(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
