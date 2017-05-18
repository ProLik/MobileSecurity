package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;

import org.w3c.dom.Text;


public class Setup3Activity extends BaseSetupActivity {
    private  EditText mETPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        mETPhone = (EditText) findViewById(R.id.et_phone);
        String phone = mSp.getString("safe_phone","");
        mETPhone.setText(phone);

    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入动画和退出动画
    }

    @Override
    public void showNextPage() {
        String phone = mETPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"安全号码不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        mSp.edit().putString("safe_phone",phone).commit();
        startActivity(new Intent(this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }

    public void selectContact(View view){
        Intent intent = new Intent(this,ContactActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            String phone = data.getStringExtra("phone").trim();
            phone = phone.replaceAll("-", "").replaceAll(" ", "");
            if(!TextUtils.isEmpty(phone)){
                mETPhone.setText(phone);

            }
        }
    }
}
