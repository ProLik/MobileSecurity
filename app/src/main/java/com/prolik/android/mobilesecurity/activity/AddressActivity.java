package com.prolik.android.mobilesecurity.activity;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.db.dao.AddressDao;

import java.lang.annotation.Annotation;

public class AddressActivity extends AppCompatActivity {

    private EditText mEditText;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        if (mEditText == null) {
            mEditText = (EditText) findViewById(R.id.et_number);
        }
        if (mTextView == null) {
            mTextView = (TextView) findViewById(R.id.tv_result);
        }


        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = AddressDao.getAddress(s.toString());
                mTextView.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void query(View view) {
        String number = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            mTextView.setText(address);
        }else{
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            mEditText.startAnimation(shake);//输入框颤抖
            vibrate();
        }
    }

    /*
    * 手机震动
    * */
    private void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0,100},-1);
        // 先等待1秒,再震动2秒,再等待1秒,再震动3秒,
        // 参2等于-1表示只执行一次,不循环,
        // 参2等于0表示从头循环,
        // 参2表示从第几个位置开始循环
    }

}
