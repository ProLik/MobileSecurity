package com.prolik.android.mobilesecurity.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.bean.TaskInfo;
import com.prolik.android.mobilesecurity.engine.TaskEngine;
import com.prolik.android.mobilesecurity.utils.MyAsycTask;
import com.prolik.android.mobilesecurity.utils.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.MemoryHandler;

public class TaskManagerActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_task_process_count)
    private TextView mTaskCount;
    @ViewInject(R.id.tv_task_memory)
    private TextView mTaskMemory;
    @ViewInject(R.id.loading)
    private ProgressBar mLoading;
    @ViewInject(R.id.lv_process)
    private ListView mProcess;

    private List<TaskInfo> mList;
    // 用户程序集合
    private List<TaskInfo> mUserAppInfo;
    // 系统程序的集合
    private List<TaskInfo> mSystemAppInfo;

    private AppManagerAdapter mAdapter;
    //是否显示系统进程
    private boolean isShowSystem = true;

    private int totalRunProcess;
    private long availRam;
    private long totalRam;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initData() {
        totalRunProcess = TaskUtil.getProcessCount(getApplicationContext());
        mTaskCount.setText("运行中进程：" + totalRunProcess);

        availRam = TaskUtil.getAvailableRam(getApplicationContext());
        String availRamStr = Formatter.formatFileSize(getApplicationContext(),availRam);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk >= 16) {
            totalRam = TaskUtil.getTotalRam(getApplicationContext());
        } else {
            totalRam = TaskUtil.getTotalRam();
        }
        // 数据转化
        String totRam = Formatter.formatFileSize(getApplicationContext(),
                totalRam);
        mTaskMemory.setText("剩余/总内存:" + availRamStr + "/" + totRam);
        new MyAsycTask(){

            @Override
            public void preTask() {
                mLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void doingTask() {
                mList = TaskEngine.getAllTaskInfo(getApplicationContext());

                mUserAppInfo = new ArrayList<TaskInfo>();

                mSystemAppInfo = new ArrayList<TaskInfo>();

                for (TaskInfo taskInfo : mList) {

                    if (taskInfo.isUser()){
                        mUserAppInfo.add(taskInfo);
                    } else {
                        mSystemAppInfo.add(taskInfo);
                    }

                }

            }

            @Override
            public void postTask() {
                if (mAdapter == null) {
                    mAdapter = new AppManagerAdapter();
                    mProcess.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                mLoading.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        mProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = mProcess.getItemAtPosition(position);
                if(obj != null && obj instanceof TaskInfo ){
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    TaskInfo taskInfo = (TaskInfo) obj;
                    if(!taskInfo.getPackageName().equals(getPackageName())){
                        if(taskInfo.isChecked()){
                            viewHolder.tv_status.setChecked(false);
                            taskInfo.setChecked(false);
                        }else{
                            viewHolder.tv_status.setChecked(true);
                            taskInfo.setChecked(true);
                        }
                    }else{
                        viewHolder.tv_status.setChecked(false);
                        taskInfo.setChecked(false);
                    }

                }
            }
        });
    }

    private class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            boolean isShowSystem = mSharedPreferences.getBoolean("is_show_system", false);
            return isShowSystem ? mList.size() + 2 : mUserAppInfo.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if(position == 0 || position == mUserAppInfo.size() + 1){
                return null;
            }else{
                if(position < mUserAppInfo.size() + 1){
                    return mUserAppInfo.get(position -1 );
                }else{
                    return mSystemAppInfo.get(position - mUserAppInfo.size() -2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //用户程序
            if(position == 0){
                TextView view = new TextView(TaskManagerActivity.this);
                view.setText("用户程序（"+mUserAppInfo.size()+"）");
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                return view;
            }
            if(position == mUserAppInfo.size() + 1){
                TextView view = new TextView(TaskManagerActivity.this);
                view.setText("系统程序（"+mSystemAppInfo.size()+"）");
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                return view;
            }

            TaskInfo taskInfo;
            if(position > 0 && position < mUserAppInfo.size() + 1){
                taskInfo = mUserAppInfo.get(position - 1);
            }else {
                taskInfo = mSystemAppInfo.get(position- mUserAppInfo.size() -2);
            }

            View view = null;
            ViewHolder holder;
            if(convertView != null && convertView instanceof LinearLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(TaskManagerActivity.this,R.layout.item_task_manager,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_status = (CheckBox) view.findViewById(R.id.tv_app_status);
                holder.tv_memory_size = (TextView) view.findViewById(R.id.tv_app_memory_size);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getName());
            holder.tv_memory_size.setText(Formatter.formatFileSize(TaskManagerActivity.this,taskInfo.getRamSize()));

            if (taskInfo.isChecked()) {
                holder.tv_status.setChecked(true);
            } else {
                holder.tv_status.setChecked(false);
            }
            if(taskInfo.getPackageName().equals(getPackageName())){//如果是当前用户则
                holder.tv_status.setVisibility(View.GONE);
            }else{
                holder.tv_status.setVisibility(View.VISIBLE);
            }




            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_size;
        CheckBox tv_status;
    }

    public void all(View v){
        for (TaskInfo taskInfo : mUserAppInfo){
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : mSystemAppInfo){
            taskInfo.setChecked(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void cancel(View v){
        for (TaskInfo taskInfo : mUserAppInfo){
            if(taskInfo.isChecked()){
                taskInfo.setChecked(false);
            }else{
                taskInfo.setChecked(true);
            }

        }
        for (TaskInfo taskInfo : mSystemAppInfo){
            if(taskInfo.isChecked()){
                taskInfo.setChecked(false);
            }else{
                taskInfo.setChecked(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    public void clear(View v){
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<TaskInfo> checkedTask = new ArrayList<TaskInfo>();
        for (TaskInfo taskInfo : mUserAppInfo){
            if(taskInfo.isChecked()){
                checkedTask.add(taskInfo);
            }
        }
        for (TaskInfo taskInfo : mSystemAppInfo){
            if(taskInfo.isChecked()){
                checkedTask.add(taskInfo);
            }
        }
        int totalCount = 0;
        long killMen = 0l;
        for (TaskInfo taskInfo : checkedTask){
            killMen += taskInfo.getRamSize();
            activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            if(taskInfo.isUser()){
                mUserAppInfo.remove(taskInfo);
            }else{
                mSystemAppInfo.remove(taskInfo);
            }
            totalCount ++;
        }
        Toast.makeText(TaskManagerActivity.this,"共清理"
                + totalCount
                + "个进程,释放"
                + Formatter.formatFileSize(TaskManagerActivity.this,
                killMen) + "内存",Toast.LENGTH_SHORT).show();
        mTaskCount.setText("运行中进程：" + (totalRunProcess - totalCount));
        mTaskMemory.setText("剩余/总内存:" + Formatter.formatFileSize(getApplicationContext(), availRam+killMen) + "/" + Formatter.formatFileSize(getApplicationContext(), totalRam));
        mAdapter.notifyDataSetChanged();
    }

    /*
    * 打开设置界面
    * */

    public void setting(View v){
        Intent intent = new Intent(this,TaskManagerSettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
}
