package com.prolik.android.mobilesecurity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.adapter.MyBaseAdapter;
import com.prolik.android.mobilesecurity.bean.BlackNumber;
import com.prolik.android.mobilesecurity.db.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity extends AppCompatActivity {

    private EditText mEditPage;
    private TextView mTextPage;
    private LinearLayout mProcessLayout;
    private ListView mListView;
    private List<BlackNumber> mBlackNumberList;
    private CallSafeAdapter mCallSafeAdapter;

    /**
     * 开始的位置
     */
    private int mStartIndex = 0;
    /**
     * 每页展示20条数据
     */
    private int maxCount = 20;

    /**
     * 一共有多少页面
     */
    private int totalNumber;


    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProcessLayout.setVisibility(View.INVISIBLE);
            if(mCallSafeAdapter == null){
                mCallSafeAdapter = new CallSafeAdapter(mBlackNumberList,CallSafeActivity.this);
                mListView.setAdapter(mCallSafeAdapter);
            }else{
                mCallSafeAdapter.notifyDataSetChanged();
            }
        }
    };
    private BlackNumberDao mBlackNumberDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);

        initUI();
        initData();


    }

    /**
     * 初始化页面
     */
    private void initUI(){
        if(mEditPage == null){
            mEditPage = (EditText) findViewById(R.id.et_page_number);
        }
        if(mTextPage == null){
            mTextPage = (TextView) findViewById(R.id.tv_page_number);
        }
        if(mProcessLayout == null){
            mProcessLayout = (LinearLayout) findViewById(R.id.ll_pb);
        }
        mProcessLayout.setVisibility(View.VISIBLE);
        if(mListView == null){
            mListView = (ListView) findViewById(R.id.list_view);
        }
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    /**
                     * The view is not scrolling. Note navigating the list using the trackball counts as
                     * being in the idle state since these transitions are not animated.
                     * 没有滚动，
                     */
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE :
                        int lastVisiblePosition = mListView.getLastVisiblePosition();
                        if(lastVisiblePosition == mBlackNumberList.size() - 1){
                            /*加载更多数据*/
                            mStartIndex += maxCount;
                            if(mStartIndex > totalNumber){
                                Toast.makeText(CallSafeActivity.this,"没有更多数据了！",Toast.LENGTH_SHORT).show();
                            }
                            initData();
                        }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    private void initData(){
        mBlackNumberDao = new BlackNumberDao(CallSafeActivity.this);
        totalNumber = mBlackNumberDao.getTotalNumber();
        new Thread(){
            @Override
            public void run() {

            //分批加载数据
            if (mBlackNumberList == null) {
                mBlackNumberList = mBlackNumberDao.findPart2(mStartIndex, maxCount);
            } else {
                //把后面的数据。追加到mBlackNumberList集合里面。防止黑名单被覆盖
                mBlackNumberList.addAll(mBlackNumberDao.findPart2(mStartIndex, maxCount));
            }
            mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    public void addBlackNumber(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_add_black_number, null);
        final EditText et_number = (EditText) dialogView.findViewById(R.id.et_number);

        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);

        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        final CheckBox cb_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);

        final CheckBox cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_number = et_number.getText().toString().trim();
                if(TextUtils.isEmpty(str_number)){
                    Toast.makeText(CallSafeActivity.this,"请输入黑名单号码",Toast.LENGTH_SHORT).show();
                    return;
                }

                String mode = "";

                if(cb_phone.isChecked()&& cb_sms.isChecked()){
                    mode = "1";
                }else if(cb_phone.isChecked()){
                    mode = "2";
                }else if(cb_sms.isChecked()){
                    mode = "3";
                }else{
                    Toast.makeText(CallSafeActivity.this,"请勾选拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumber blackNumberInfo = new BlackNumber();
                blackNumberInfo.setNumber(str_number);
                blackNumberInfo.setMode(mode);
                mBlackNumberList.add(0,blackNumberInfo);
                //把电话号码和拦截模式添加到数据库。
                mBlackNumberDao.add(str_number,mode);

                if(mCallSafeAdapter == null){
                    mCallSafeAdapter = new CallSafeAdapter(mBlackNumberList, CallSafeActivity.this);
                    mListView.setAdapter(mCallSafeAdapter);
                }else{
                    mCallSafeAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        dialog.show();
    }


    private class CallSafeAdapter extends MyBaseAdapter<BlackNumber>{

        public CallSafeAdapter(List<BlackNumber> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(CallSafeActivity.this,R.layout.item_call_safe,null);
                viewHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BlackNumber blackNumber = mBlackNumberList.get(position);
            String mode = blackNumber.getMode();
            if("1".equals(mode)){
                viewHolder.tv_mode.setText("全部拦截");
            }else if("2".equals(mode)){
                viewHolder.tv_mode.setText("电话拦截");
            }else{
                viewHolder.tv_mode.setText("短信拦截");
            }
            viewHolder.tv_number.setText(blackNumber.getNumber());
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = blackNumber.getNumber();
                    if (mBlackNumberDao.delete(number)) {
                        mBlackNumberList.remove(blackNumber);
                        mCallSafeAdapter.notifyDataSetChanged();
                        Toast.makeText(CallSafeActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }

    private class ViewHolder{
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }
}
