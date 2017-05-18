package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.prolik.android.mobilesecurity.R;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        if (mCheckBox == null){
            mCheckBox = (CheckBox) findViewById(R.id.cb_protect);
        }

        boolean checked = mSp.getBoolean("protect",false);
        if(checked){
            mCheckBox.setText("防盗保护已开启");
        }else{
            mCheckBox.setText("防盗保护已关闭");
        }
        mCheckBox.setChecked(checked);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSp.edit().putBoolean("protect",true).commit();
                    mCheckBox.setText("防盗保护已开启");
                }else{
                    mSp.edit().putBoolean("protect",false).commit();
                    mCheckBox.setText("防盗保护已关闭");
                }
            }
        });
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入动画和退出动画
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, LostFindActivity.class));
        mSp.edit().putBoolean("configed",true).commit();
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }
}
