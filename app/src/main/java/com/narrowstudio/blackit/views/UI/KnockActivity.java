package com.narrowstudio.blackit.views.UI;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.narrowstudio.blackit.R;
import com.narrowstudio.blackit.viewmodels.KnockViewModel;

public class KnockActivity extends AppCompatActivity {

    private Button knockButton0, knockButton1, knockButton2, knockButton3, restartButton, confirmButton;
    private KnockViewModel mKnockViewModel;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knock);

        mKnockViewModel = new ViewModelProvider(this).get(KnockViewModel.class);
        mKnockViewModel.init();

        knockButton0 = (Button) findViewById(R.id.knock_settings_button_0);
        knockButton1 = (Button) findViewById(R.id.knock_settings_button_1);
        knockButton2 = (Button) findViewById(R.id.knock_settings_button_2);
        knockButton3 = (Button) findViewById(R.id.knock_settings_button_3);
        restartButton = (Button) findViewById(R.id.knock_start_again_button);
        confirmButton = (Button) findViewById(R.id.knock_confirm_button);

        knockButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIntToKnockCode(0);
            }
        });

        knockButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIntToKnockCode(1);
            }
        });

        knockButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIntToKnockCode(2);
            }
        });

        knockButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIntToKnockCode(3);
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetNewKnockCode();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewKnockCode();
                resetNewKnockCode();
            }
        });








    }//end of onCreate

    private void addIntToKnockCode(int i){
        int prevL = mKnockViewModel.getNewKnockCodeSize();
        mKnockViewModel.addToNewKnockCode(i, this);

        if (i == 0){
            String s = knockButton0.getText().toString();
            if (s.length() < 1){
                s = Integer.toString(mKnockViewModel.getNewKnockCodeSize());
            } else if (prevL < 8) {
                s = s + ", " + mKnockViewModel.getNewKnockCodeSize();
            }
            knockButton0.setText(s);
        } else if (i == 1){
            String s = knockButton1.getText().toString();
            if (s.length() < 1){
                s = Integer.toString(mKnockViewModel.getNewKnockCodeSize());
            } else if (prevL < 8)  {
                s = s + ", " + mKnockViewModel.getNewKnockCodeSize();
            }
            knockButton1.setText(s);
        } else if (i == 2){
            String s = knockButton2.getText().toString();
            if (s.length() < 1){
                s = Integer.toString(mKnockViewModel.getNewKnockCodeSize());
            } else if (prevL < 8)  {
                s = s + ", " + mKnockViewModel.getNewKnockCodeSize();
            }
            knockButton2.setText(s);
        } else if (i == 3){
            String s = knockButton3.getText().toString();
            if (s.length() < 1){
                s = Integer.toString(mKnockViewModel.getNewKnockCodeSize());
            } else if (prevL < 8)  {
                s = s + ", " + mKnockViewModel.getNewKnockCodeSize();
            }
            knockButton3.setText(s);
        }
    }

    private void resetNewKnockCode(){
        knockButton0.setText("");
        knockButton1.setText("");
        knockButton2.setText("");
        knockButton3.setText("");
        mKnockViewModel.resetNewKnockCode();
    }

    private void setNewKnockCode(){
        mKnockViewModel.setNewKnockCode(this);
        Toast toast = Toast.makeText(this, getResources().getString(R.string.knock_code_set), Toast.LENGTH_SHORT);
        toast.show();
    }


}