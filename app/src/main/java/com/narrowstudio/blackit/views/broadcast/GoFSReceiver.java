package com.narrowstudio.blackit.views.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GoFSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent("com.narrowstudio.blackit.FS_ACTION").putExtra("actionname", intent.getAction());
        context.sendBroadcast(newIntent);
    }

}
