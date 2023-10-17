package com.narrowstudio.blackit.views.UI;

import static android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.SettingsViewModel;
import com.narrowstudio.blackit.views.broadcast.GoFSReceiver;

import java.util.ArrayList;

public class FloatingViewService extends Service {

    private View mBlackScreen, fullScreen, floatingIcon;
    private WindowManager mWindowManager;
    final WindowManager.LayoutParams params = show();
    private int unlockMode = 0, screenMode = 0, clicked = 0, floatingSize = 0, iconSize = 0;
    private int doubleClickTime = 500, knockClickTime = 500;
    private ArrayList<Integer> knockCode = new ArrayList<>();
    private ArrayList<Integer> knockSeq = new ArrayList<>();
    private boolean wasBoxClicked = false;
    private long startTime = 0,millis;
    private int millisSet=500;
    private View box0, box1, box2, box3;
    private TextClock mClock, mClock2, mDate;
    private boolean isClock, isBrightness, isButtons, isFloating, isFloatingTurnedOn, isRotation, isPortrait;
    private ImageView floatingIconIV;
    private int prevX = 20, prevY = 70;
    private boolean viewExists = false;
    private String knockCodeString = "";

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            int millisInt = (int) millis;
            if (millisInt > millisSet) {
                if (!wasBoxClicked) {
                    knockSeq.clear();
                    timerHandler.removeCallbacks(timerRunnable);
                } else {
                    wasBoxClicked = false;
                    startTime = System.currentTimeMillis();
                    timerHandler.post(timerRunnable);
                }
            }
            timerHandler.postDelayed(this, 50);
        }
    };

    public FloatingViewService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        PendingIntent pendingHomeIntent;
        PendingIntent actionIntent;
        PendingIntent actionKillIntent;
        Intent notificationHomeIntent = new Intent(this, MainActivity.class);
        Intent broadcastIntent = new Intent(this, GoFSReceiver.class).setAction("fullscreen");
        Intent broadcastKillIntent = new Intent(this, GoFSReceiver.class).setAction("kill");

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
            pendingHomeIntent = PendingIntent.getActivity(this,
                    0, notificationHomeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            actionIntent = PendingIntent.getBroadcast(this,
                    0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            actionKillIntent = PendingIntent.getBroadcast(this,
                    0, broadcastKillIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingHomeIntent = PendingIntent.getActivity(this,
                    0, notificationHomeIntent, PendingIntent.FLAG_MUTABLE);
            actionIntent = PendingIntent.getBroadcast(this,
                    0, broadcastIntent, PendingIntent.FLAG_MUTABLE);
            actionKillIntent = PendingIntent.getBroadcast(this,
                    0, broadcastKillIntent, PendingIntent.FLAG_MUTABLE);
        }



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.service_channel))
                .setContentText(getString(R.string.service_context))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(getString(R.string.service_channel))
                .setContentIntent(pendingHomeIntent)
                .addAction(R.mipmap.ic_launcher, "Activate", actionIntent)
                .addAction(R.mipmap.ic_launcher, "Close", actionKillIntent)
                .setShowWhen(false);



        if (Build.VERSION.SDK_INT >= 34){
            startForeground(1, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notificationBuilder.build());
        }

        Bundle mBundle = intent.getExtras();
        unlockMode = mBundle.getInt("unlock", 0);
        screenMode = mBundle.getInt("screen");
        isClock = mBundle.getBoolean("clock", true);
        isBrightness = mBundle.getBoolean("brightness", false);
        isButtons = mBundle.getBoolean("buttons", true);
        isFloatingTurnedOn = mBundle.getBoolean("floating", false);
        knockCodeString = mBundle.getString("knock_code", "0123");
        iconSize = mBundle.getInt("icon_size", 1);
        isRotation = mBundle.getBoolean("rotation", false);
        isPortrait = mBundle.getBoolean("portrait", false);
        knockCode = setKnockCodeAL();
        windowSetup();
        if (!isFloatingTurnedOn){
            deactivateFullScreen();
        }
        return Service.START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate() {
        super.onCreate();
        mBlackScreen = LayoutInflater.from(this).inflate(R.layout.layout_floating_screen, null);
        IntentFilter filter = new IntentFilter("com.narrowstudio.blackit.FS_ACTION");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mBroadcastReceiver, filter);
        }


    }


    private void windowSetup(){
        isFloating = isFloatingTurnedOn;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mBlackScreen, params);
        viewExists = true;

        fullScreen = mBlackScreen.findViewById(R.id.fs_full_screen);
        box0 = mBlackScreen.findViewById(R.id.fs_window_0);
        box1 = mBlackScreen.findViewById(R.id.fs_window_1);
        box2 = mBlackScreen.findViewById(R.id.fs_window_2);
        box3 = mBlackScreen.findViewById(R.id.fs_window_3);

        floatingIcon = mBlackScreen.findViewById(R.id.fs_collapsed);

        floatingIconIV = (ImageView) mBlackScreen.findViewById(R.id.fs_collapsed_icon_IV);

        if (isFloatingTurnedOn){
            goFloatingIcon();
        } else {
            goFullScreen();
        }



        /*
        0- single click
        1- double click
        2- knock code
        */
        if (unlockMode == 0){
            fullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deactivateFullScreen();
                }
            });
        }
        else if (unlockMode == 1){
            fullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked++;
                    if (clicked == 2){
                        deactivateFullScreen();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clicked = 0;
                        }
                    }, doubleClickTime);

                }
            });
        }
        else if (unlockMode == 2){
            box0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    knockSeq.add(0);
                    runKnockHandler();
                }
            });
            box1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    knockSeq.add(1);
                    runKnockHandler();
                }
            });
            box2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    knockSeq.add(2);
                    runKnockHandler();
                }
            });
            box3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    knockSeq.add(3);
                    runKnockHandler();
                }
            });

        }


        mDate = (TextClock) mBlackScreen.findViewById(R.id.dateTV);
        mClock = (TextClock) mBlackScreen.findViewById(R.id.clockTV);
        mClock2 = (TextClock) mBlackScreen.findViewById(R.id.clockTV2);
        setClock();

        floatingIcon.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 5 && Ydiff < 5 && Xdiff > -5 && Ydiff > -5) {

                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            goFullScreen();

                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mBlackScreen, params);
                        return true;

                }
                return false;
            }
        });



    }


    private void setClock(){
        mDate = (TextClock) mBlackScreen.findViewById(R.id.dateTV);
        mClock = (TextClock) mBlackScreen.findViewById(R.id.clockTV);
        mClock2 = (TextClock) mBlackScreen.findViewById(R.id.clockTV2);

        //setting format every time so the clock will set when reopened
        if (mDate.is24HourModeEnabled()){
            mDate.setFormat24Hour("dd.MM");
        } else {
            mDate.setFormat12Hour("MM/dd");
        }

        if (mClock.is24HourModeEnabled()){
            mClock.setFormat24Hour("HH");
        } else {
            mClock.setFormat12Hour("hh");
        }

        if (mClock2.is24HourModeEnabled()){
            mClock2.setFormat24Hour("mm");
        } else {
            mClock2.setFormat12Hour("mm");
        }


        if(!isClock){
            mDate.setVisibility(View.INVISIBLE);
            mClock.setVisibility(View.INVISIBLE);
            mClock2.setVisibility(View.INVISIBLE);
        }
        else{
            mDate.setVisibility(View.VISIBLE);
            mClock.setVisibility(View.VISIBLE);
            mClock2.setVisibility(View.VISIBLE);
        }
    }



    private WindowManager.LayoutParams show(){
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    getParamsFlags(),
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    getParamsFlags(),
                    PixelFormat.TRANSLUCENT);
        }
        if (isBrightness){
            if (!isFloating) {
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
            } else {
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            }
        }

        params.gravity = Gravity.START | Gravity.TOP;
        if (isFloating) {
            params.x = prevX;
            params.y = prevY;
        } else {
            params.x = 0;
            params.y = 0;
        }
        return params;

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int tx, ty;
        int nxp, nyp;
        float nxpf, nypf;
        float xper, yper;

        if (isFloating) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                tx = params.y;
                ty = params.x;
                xper = (float) tx / screenWidth;
                yper = (float) ty / screenHeight;
                nxpf = xper * screenHeight;
                nypf = yper * screenWidth;
                nxp = (int) nxpf;
                nyp = (int) nypf;
                params.x = nyp;
                params.y = nxp;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                tx = params.x;
                ty = params.y;
                xper = (float) tx / screenHeight;
                yper = (float) ty / screenWidth;
                nxpf = xper * screenWidth;
                nypf = yper * screenHeight;
                nxp = (int) nxpf;
                nyp = (int) nypf;
                params.x = nxp;
                params.y = nyp;
            }
            mWindowManager.updateViewLayout(mBlackScreen, params);
        } else {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                tx = prevX;
                ty = prevY;
                xper = (float) tx / screenHeight;
                yper = (float) ty / screenWidth;
                nxpf = xper * screenWidth;
                nypf = yper * screenHeight;
                nxp = (int) nxpf;
                nyp = (int) nypf;
                prevY = nyp;
                prevX = nxp;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                tx = prevX;
                ty = prevY;
                xper = (float) tx / screenHeight;
                yper = (float) ty / screenWidth;
                nxpf = xper * screenWidth;
                nypf = yper * screenHeight;
                nxp = (int) nxpf;
                nyp = (int) nypf;
                prevX = nxp;
                prevY = nyp;
            }
            //anti activation while rotating
            if (viewExists) {
                goFullScreen();
            }
        }


    }

    private int getParamsFlags(){
        int mFlags;
        if (isFloating) {
            mFlags = (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED);
        } else {
            mFlags = (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED);
        }
        return mFlags;
    }

    private int getNavigationBarSize(){
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private int getStatusBarSize(){
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void goFullScreen(){
        isFloating = false;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = mWindowManager.getDefaultDisplay();
        display.getMetrics(metrics);

        if (fullScreen.getVisibility() != View.VISIBLE) {
            prevX = params.x;
            prevY = params.y;
        }

        int navBar = getNavigationBarSize();
        int statusBar = getStatusBarSize();
        params.flags = getParamsFlags();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.width = metrics.widthPixels + navBar;
            params.height = metrics.heightPixels + statusBar;
        } else {
            params.width = metrics.widthPixels;
            params.height = metrics.heightPixels + navBar + statusBar;
        }
        params.x = 0;
        params.y = -statusBar;
        if (isBrightness){
            if (!isFloating) {
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
            } else {
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            }
        }
        if (isRotation) {
            if (isPortrait) {
                params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else {
                params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
            }
        }
        if (viewExists) {
            floatingIcon.setVisibility(View.GONE);
            fullScreen.setVisibility(View.VISIBLE);
            mWindowManager.updateViewLayout(mBlackScreen, params);
        } else {
            mWindowManager.addView(mBlackScreen, params);
            viewExists = true;
        }

        setClock();

    }

    private void goFloatingIcon(){
        isFloating = true;

        int iconSizeDP;
        if (iconSize == 0){
            iconSizeDP = 80;
        } else if (iconSize == 1){
            iconSizeDP = 110;
        } else {
            iconSizeDP = 160;
        }
        //params.width = floatingIconIV.getMeasuredWidth();
        //params.height = floatingIconIV.getMeasuredHeight();
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //check if starting point is on the screen
        if (prevY < 20){
            prevY = 20;
        } else if (prevY > size.y) {
            prevY = size.y - 20 - iconSizeDP;
        }
        if (prevX < 20){
            prevX = 20;
        } else if (prevX > size.x) {
            prevX = size.x - 20 - iconSizeDP;
        }

        params.flags = getParamsFlags();
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        params.width = iconSizeDP;
        params.height = iconSizeDP;
        params.x = prevX;
        params.y = prevY;
        floatingIcon.setVisibility(View.VISIBLE);
        fullScreen.setVisibility(View.GONE);
        mWindowManager.updateViewLayout(mBlackScreen, params);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopSelf();
        //if (mBlackScreen != null) mWindowManager.removeView(mBlackScreen);
        try {
            mWindowManager.removeView(mBlackScreen);
            viewExists = false;
        } catch (Exception e) {

        }
    }

    private void deactivateFullScreen(){
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        if (isFloatingTurnedOn){
            goFloatingIcon();
        } else {
            hideFullScreen();
        }
    }

    private ArrayList<Integer> setKnockCodeAL(){
        ArrayList<Integer> retAL = new ArrayList<>();
        for (char c: knockCodeString.toCharArray()){
            retAL.add(Integer.parseInt(String.valueOf(c)));
        }
        if (retAL.get(0) == null){
            retAL.add(0);
        }
        return retAL;
    }

    private void hideFullScreen() {
        /*fullScreen.setVisibility(View.GONE);
        mWindowManager.updateViewLayout(mBlackScreen, params);*/
        mWindowManager.removeView(mBlackScreen);
        viewExists = false;
    }

    private void killService(){
        this.stopSelf();
    }

    private void runKnockHandler(){
        wasBoxClicked = true;
        boolean correct = knockSeq.equals(knockCode);
        if (correct){
            deactivateFullScreen();
        }
        else{
            timerHandler.removeCallbacks(timerRunnable);
            startTime = System.currentTimeMillis();
            timerHandler.post(timerRunnable);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    getResources().getString(R.string.service_channel),
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_MIN
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case "fullscreen":
                    goFullScreen();
                    break;
                case "kill":
                    killService();
                    break;
            }
        }
    };


}