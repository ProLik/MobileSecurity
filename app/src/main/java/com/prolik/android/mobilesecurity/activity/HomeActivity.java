package com.prolik.android.mobilesecurity.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.utils.MD5Utils;

/**
 * Created by ProLik on 2015/11/27.
 */
public class HomeActivity extends Activity {

    private GridView mGridView;
    private String[] mItems = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

    private int[] mPics = new int[] { R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings };

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        if(mGridView== null){
            mGridView = (GridView) findViewById(R.id.gv_home);
            mGridView.setAdapter(new MyAdapter());
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            /*手机防盗*/
                            showPassWordDialog();
                            break;
                        case 1:
                            startActivity(new Intent(HomeActivity.this, CallSafeActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(HomeActivity.this,AppManagerActivity.class));
                            break;
                        case 3://进程管理
                            startActivity(new Intent(HomeActivity.this,TaskManagerActivity.class));
                            break;
                        case 7:
                             /*高级工具*/
                            startActivity( new Intent(HomeActivity.this,AToolsActivity.class));
                            break;
                        case 8:
                            /*设置中心*/
                            startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                            break;
                    }
                }
            });
        }
    }

    private void showPassWordDialog() {
        if(sp.getString("password", null)== null){
            showPasswordSetDialog();
        }else{
            showPasswordInputDialog();
        }

    }

    /*手机防盗--打开输入密码对话框*/
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_input_password,null);
        dialog.setView(view);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        Button  btOk = (Button) view.findViewById(R.id.bt_ok);
        Button  btCancel = (Button) view.findViewById(R.id.bt_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if(!TextUtils.isEmpty(password)){
                    String savedPassword = sp.getString("password", null);
                    if(MD5Utils.enCode(password).equals(savedPassword)){

                        dialog.dismiss();
                        /*打开手机防盗页面*/
                        Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(HomeActivity.this,"输入不正确，请再次输入！",Toast.LENGTH_SHORT ).show();
                    }
                }else{
                    Toast.makeText(HomeActivity.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*手机防盗--第一次打开输入，确认密码*/
    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_set_password,null);
        dialog.setView(view);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_confirm_password);
        Button  btOk = (Button) view.findViewById(R.id.bt_ok);
        Button  btCancel = (Button) view.findViewById(R.id.bt_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        sp.edit().putString("password", MD5Utils.enCode(password)).commit();
                        /**
                         * Dismiss this dialog, removing it from the screen. This method can be
                         * invoked safely from any thread.  Note that you should not override this
                         * method to do cleanup when the dialog is dismissed, instead implement
                         * that in {@link #onStop}.
                         */
                        dialog.dismiss();
                        /*打开手机防盗页面*/
                        Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HomeActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,R.layout.home_list,null);
            ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item);
            ivItem.setImageResource(mPics[position]);
            tvItem.setText(mItems[position]);
            return view;
        }
    }
}
