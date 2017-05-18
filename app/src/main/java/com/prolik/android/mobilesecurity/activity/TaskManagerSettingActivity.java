package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.service.KillProcessService;
import com.prolik.android.mobilesecurity.utils.ServiceStatusUtils;

public class TaskManagerSettingActivity extends AppCompatActivity {

    @ViewInject(R.id.cb_status)
    private CheckBox mShowSystem;
    @ViewInject(R.id.cb_status_kill_process)
    private CheckBox mKillProcess;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        ViewUtils.inject(this);
        initUI();
    }

    private void initUI() {
        mPreferences = getSharedPreferences("config",MODE_PRIVATE);
        boolean is_show_system = mPreferences.getBoolean("is_show_system", false);
        mShowSystem.setChecked(is_show_system);
        mShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.edit().putBoolean("is_show_system",isChecked).commit();
            }
        });

        final Intent intent = new Intent(this,KillProcessService.class);

        mKillProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(ServiceStatusUtils.isServiceRunning(TaskManagerSettingActivity.this, "com.prolik.android.mobilesecurity.services.KillProcessService")){
            mKillProcess.setChecked(true);
        }else{
            mKillProcess.setChecked(false);
        }
    }
}
