package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.bean.AppInfo;
import com.prolik.android.mobilesecurity.engine.AppInfoEngine;

import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    @ViewInject(R.id.tv_rom)
    private TextView mTvRom;

    @ViewInject(R.id.tv_ram)
    private TextView mTvRam;

    @ViewInject(R.id.tv_app)
    private TextView mTvApp;

    @ViewInject(R.id.list_view)
    private ListView mListView;

    private List<AppInfo> mAppInfos;

    private List<AppInfo> mUserAppInfos;

    private List<AppInfo> mSystemAppInfos;

    private AppInfo mClickAppInfo;

    private PopupWindow mPopupWindow;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerAdapter adapter = new AppManagerAdapter();
            mListView.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();

                mAppInfos = AppInfoEngine.getAppInfos(AppManagerActivity.this);

                mUserAppInfos = new ArrayList<AppInfo>();
                mSystemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo : mAppInfos) {
                    if(appInfo.isUser()){
                        mUserAppInfos.add(appInfo);
                    }else{
                        mSystemAppInfos.add(appInfo);
                    }
                }

                mHandler.sendEmptyMessage(0);

            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);//使用 xUtils 自动注入

        //获取rom的运行内存
        long rom_freeSpace = 0l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        }
        //获取sd卡剩余空间
        long ram_freeSpace = 0l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            ram_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        }
        mTvRom.setText("内存可用：" + android.text.format.Formatter.formatFileSize(this,rom_freeSpace));
        mTvRam.setText("SD卡可用：" + android.text.format.Formatter.formatFileSize(this, ram_freeSpace));


        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserAppInfos != null && mSystemAppInfos != null) {
                    if (firstVisibleItem < mUserAppInfos.size() + 1) {
                        mTvApp.setVisibility(View.VISIBLE);
                        mTvApp.setText("用户程序（" + mUserAppInfos.size() + "）");
                    } else if (firstVisibleItem > mUserAppInfos.size() + 1) {
                        mTvApp.setVisibility(View.VISIBLE);
                        mTvApp.setText("系统程序（" + mSystemAppInfos.size() + "）");
                    }
                }
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = mListView.getItemAtPosition(position);
                if(obj != null && obj instanceof AppInfo){
                    mClickAppInfo = (AppInfo) obj;
                    View contentView = View.inflate(AppManagerActivity.this,R.layout.item_popup,null);

                    LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

                    LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                    LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_run);

                    LinearLayout ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);

                    ll_uninstall.setOnClickListener(AppManagerActivity.this);

                    ll_share.setOnClickListener(AppManagerActivity.this);

                    ll_start.setOnClickListener(AppManagerActivity.this);

                    ll_detail.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();
                    // -2表示包裹内容
                    mPopupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);

                    mPopupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);


                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                    sa.setDuration(400);

                    contentView.startAnimation(sa);


                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            卸载
            case R.id.ll_uninstall :
                Intent uninstall_intent = new Intent("android.intent.action.DELETE", Uri.parse("package:"+mClickAppInfo.getPackageName()));
                startActivity(uninstall_intent);
                popupWindowDismiss();
                initData();
                break;
            case R.id.ll_run :
                Intent run_intent = this.getPackageManager().getLaunchIntentForPackage(mClickAppInfo.getPackageName());
                startActivity(run_intent);
                popupWindowDismiss();
                break;
            case R.id.ll_share :
                /*隐式意图*/
                Intent share_intent = new Intent("android.intent.action.SEND");
                share_intent.setType("text/plain");
                share_intent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_intent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + mClickAppInfo.getName()+"下载地址:"+"https://play.google.com/store/apps/details?id="+mClickAppInfo.getPackageName());
                this.startActivity(Intent.createChooser(share_intent, "分享"));
                break;
            case R.id.ll_detail :
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + mClickAppInfo.getPackageName()));
                startActivity(detail_intent);
                break;
        }
    }


    private class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mAppInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if(position == 0 || position == mUserAppInfos.size() + 1){
                return null;
            }else{
                if(position < mUserAppInfos.size() + 1){
                    return mUserAppInfos.get(position -1 );
                }else{
                    return mSystemAppInfos.get(position - mUserAppInfos.size() -2);
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
                TextView view = new TextView(AppManagerActivity.this);
                view.setText("用户程序（"+mUserAppInfos.size()+"）");
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                return view;
            }
            if(position == mUserAppInfos.size() + 1){
                TextView view = new TextView(AppManagerActivity.this);
                view.setText("系统程序（"+mSystemAppInfos.size()+"）");
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                return view;
            }

            AppInfo appInfo;
            if(position > 0 && position < mUserAppInfos.size() + 1){
                appInfo = mUserAppInfos.get(position - 1);
            }else {
                appInfo = mSystemAppInfos.get(position- mUserAppInfos.size() -2);
            }

            View view = null;
            ViewHolder holder;
            if(convertView != null && convertView instanceof LinearLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(AppManagerActivity.this,R.layout.item_app_manager,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());
            holder.tv_size.setText(Formatter.formatFileSize(AppManagerActivity.this,appInfo.getSize()));
            if(appInfo.isRom()){
                holder.tv_location.setText("手机内存");
            }else{
                holder.tv_location.setText("外部存储");
            }

            return view;
        }
    }


    private void popupWindowDismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_size;
        TextView tv_name;
        TextView tv_location;
    }

}
