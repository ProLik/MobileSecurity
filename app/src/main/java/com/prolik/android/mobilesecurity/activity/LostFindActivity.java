package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolik.android.mobilesecurity.R;

public class LostFindActivity extends AppCompatActivity {

    private SharedPreferences mSp;
    private TextView mSafePhone;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        if (mSp.getBoolean("configed", false)) {
            setContentView(R.layout.activity_lost_find);

            String safe_phone = mSp.getString("safe_phone", "");
            mSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
            mSafePhone.setText(safe_phone);

            boolean protect = mSp.getBoolean("protect",false);
            mImageView = (ImageView) findViewById(R.id.iv_protect);
            if(protect){
                mImageView.setImageResource(R.drawable.lock);
            }else{
                mImageView.setImageResource(R.drawable.unlock);
            }
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //销毁当前页面
            finish();
        }
    }
    public void reEnter(View view){
        mSp.edit().putBoolean("configed", false).commit();
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        //销毁当前页面
        finish();
    }


}
