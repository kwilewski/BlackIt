package com.narrowstudio.blackit.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.service.models.SettingsModel;
import com.narrowstudio.blackit.views.UI.KnockActivity;

import java.util.ArrayList;

public class KnockViewModel extends AndroidViewModel {

    private SharedPreferences mPreferences;
    private SettingsModel mSettings;

    private ArrayList<Integer> knockCode = new ArrayList<>();
    private ArrayList<Integer> newKnockCode = new ArrayList<>();

    public KnockViewModel(@NonNull Application application){
        super(application);
        mSettings = new SettingsModel(application);

    }

    public void init(){
        mPreferences = mSettings.getPreferences().getValue();
        String kcstring = mPreferences.getString("knock_code", "0123");
        if (kcstring != null) {
            knockCode = getKnockCodeFromString(kcstring);
        }
    }


    private ArrayList<Integer> getKnockCodeFromString(String string){
        ArrayList<Integer> retAL = new ArrayList<>();
        for (char c: string.toCharArray()){
            retAL.add(Integer.parseInt(String.valueOf(c)));
        }
        if (retAL.get(0) == null){
            retAL.add(0);
        }
        return retAL;
    }

    private String getStringFromKnockCode(ArrayList<Integer> arr){
        String s = "";
        for (int i = 0; i < arr.size(); i++){
            s += Integer.toString(arr.get(i));
        }
        return s;
    }

    public void addToNewKnockCode(int i, Context context){
        if (newKnockCode.size() < 8) {
            newKnockCode.add(i);
        } else {
            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.knock_min_max), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public ArrayList<Integer> getKnockCode(){
        return knockCode;
    }

    public String getKnockCodeString(){
        String kcstring = mPreferences.getString("knock_code", "0123");
        if (kcstring != null){
            return kcstring;
        } else return "0123";
    }


    public ArrayList<Integer> getNewKnockCode(){
        return  newKnockCode;
    }

    public int getNewKnockCodeSize(){
        return newKnockCode.size();
    }

    public void resetNewKnockCode(){
        newKnockCode.clear();
    }

    public void setNewKnockCode(Context context){
        if (newKnockCode.size() < 3){
            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.knock_min_max), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            mSettings.setKnockCode(getStringFromKnockCode(newKnockCode));
        }
    }



}
