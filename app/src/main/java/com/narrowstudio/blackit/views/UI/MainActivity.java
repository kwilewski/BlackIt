package com.narrowstudio.blackit.views.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.MainViewModel;
import com.narrowstudio.blackit.viewmodels.SettingsViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private Button modeFloatingButton, modeStatusButton, settingsButton;
    private ImageView startIV;
    private MainViewModel mMainViewModel;
    private SettingsViewModel mSettingsViewModel;
    private LiveData<Boolean> isFloating, isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeFloatingButton = (Button) findViewById(R.id.floatingMainButton);
        modeStatusButton = (Button) findViewById(R.id.barMainButton);
        startIV = (ImageView) findViewById(R.id.startMainIV);
        settingsButton = (Button) findViewById(R.id.settingsMainButton);

        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mMainViewModel.init();

        mSettingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        mSettingsViewModel.init();

        mSettingsViewModel.getIsFloating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setButtonColor();
            }
        });

        setButtonColor();

        isServiceRunning = mSettingsViewModel.getIsMyServiceRunning();
        isServiceRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setStartIVImage();
            }
        });


        modeFloatingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setMode(true);
            }
        });//floating button

        modeStatusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setMode(false);
            }
        });//status button

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });//settings button

        startIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });



    } //end of onCreate




    private void setMode(boolean sett){
        mSettingsViewModel.setIsFloating(sett);
    }



    private void setButtonColor(){

        if (mSettingsViewModel.getIsFloatingBoolean()){
            modeFloatingButton.setTextColor(getResources().getColor(R.color.colorButtonCheckedText));
            modeFloatingButton.setTypeface(null, Typeface.BOLD);
            modeStatusButton.setTextColor(getResources().getColor(R.color.colorButtonText));
            modeStatusButton.setTypeface(null, Typeface.NORMAL);
        }
        else {
            modeFloatingButton.setTextColor(getResources().getColor(R.color.colorButtonText));
            modeFloatingButton.setTypeface(null, Typeface.NORMAL);
            modeStatusButton.setTextColor(getResources().getColor(R.color.colorButtonCheckedText));
            modeStatusButton.setTypeface(null, Typeface.BOLD);
        }
    }

    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startService(){
        mSettingsViewModel.isMyServiceRunning();

        if (!isServiceRunning.getValue()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    //start service
                    initializeView();
                }
        } else {
            mSettingsViewModel.killFloatingService();
        }

    }//end of startService



    private void setStartIVImage(){
        if (isServiceRunning.getValue()){
            startIV.setImageResource(R.drawable.ic_pause);
        } else {
            startIV.setImageResource(R.drawable.ic_play);
        }
    }

    private void initializeView(){
        int unlockMode = mSettingsViewModel.getUnlockMode().getValue();
        int screenMode = mSettingsViewModel.getScreenMode().getValue();
        boolean isClock = mSettingsViewModel.getIsClockEnabledBool();
        boolean isBrightness = mSettingsViewModel.getIsBrightnessOffBool();
        boolean isButtons = mSettingsViewModel.getIsButtonsEnabledBool();
        boolean isFloatingBoolean = mSettingsViewModel.getIsFloatingBoolean();
        Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
        intent.putExtra("unlock", unlockMode);
        intent.putExtra("screen", screenMode);
        intent.putExtra("clock", isClock);
        intent.putExtra("brightness", isBrightness);
        intent.putExtra("buttons", isButtons);
        intent.putExtra("floating", isFloatingBoolean);
        this.startService(intent);
        //finish();
        this.moveTaskToBack(true);
    }

}
