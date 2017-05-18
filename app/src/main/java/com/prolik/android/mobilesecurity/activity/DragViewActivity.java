package com.prolik.android.mobilesecurity.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.utils.Tool;

/*
* 修改归属地显示位置
* */
public class DragViewActivity extends Activity {

    private SharedPreferences mSharedPreferences;

    private ImageView mDrag;
    private TextView mTop;
    private TextView mBottom;

    private int mDragStartX;//图片的起始坐标
    private int mDragStartY;//图片的起始坐标

    private long mHits[] = new long[2];//多击数字，长度多少即多少击

    private static int notifyStatusHeight = 20;//系统通知栏，默认20dp,可以自己获取真正高度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);

        notifyStatusHeight = Tool.getSystemStatusHeight(this);
        if (notifyStatusHeight == 0){
            notifyStatusHeight = 20;
        }

        if(mDrag == null){
            mDrag = (ImageView) findViewById(R.id.iv_drag);
        }
        if(mTop == null){
            mTop = (TextView) findViewById(R.id.tv_top);
        }
        if(mBottom == null){
            mBottom = (TextView) findViewById(R.id.tv_bottom);
        }

        int lastX = mSharedPreferences.getInt("lastX",0);
        int lastY = mSharedPreferences.getInt("lastY",0);

        final int winHeight = getWindowManager().getDefaultDisplay().getHeight();
        final int winWidth = getWindowManager().getDefaultDisplay().getWidth();

      /*  if(lastY > winHeight / 2){//下面显示，上面隐藏
            mBottom.setVisibility(View.INVISIBLE);
            mTop.setVisibility(View.VISIBLE);
        }else{
            mBottom.setVisibility(View.VISIBLE);
            mTop.setVisibility(View.INVISIBLE);
        }*/

        System.out.printf("坐标：（"+lastX + ","+lastY + ")");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDrag.getLayoutParams();
        params.leftMargin = lastX;//设置左边距
        params.topMargin = lastY;//设置顶边距
        mDrag.setLayoutParams(params);// 重新设置位置

        /*双击居中显示*/
        mDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if(mHits[0] > mHits[mHits.length-1] -500){
                    //把图片居中
                    mDrag.layout(winWidth/2-mDrag.getWidth()/2,mDrag.getTop(),winWidth/2 + mDrag.getWidth()/2,mDrag.getBottom());
                }
            }
        });


        mDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.printf("移动了");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDragStartX = (int) event.getRawX();
                        mDragStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - mDragStartX;
                        int dy = endY - mDragStartY;
                        // 更新左上右下距离
                        int l = mDrag.getLeft() + dx;
                        int r = mDrag.getRight() + dx;
                        Log.d("move","边距：（"+mDrag.getLeft()+","+mDrag.getTop()+"）");
                        int t = mDrag.getTop() + dy;
                        int b = mDrag.getBottom() + dy;
                        if (l < 0 || r > winWidth || t < 0 || b > winHeight - notifyStatusHeight) {
                            mDragStartX = endX;
                            mDragStartY = endY;
                            break;
                        }
                     /*   if (t > winHeight / 2) {
                            mBottom.setVisibility(View.INVISIBLE);
                            mTop.setVisibility(View.VISIBLE);
                        } else {
                            mBottom.setVisibility(View.VISIBLE);
                            mTop.setVisibility(View.INVISIBLE);
                        }*/

                        //更新界面
                        mDrag.layout(l, t, r, b);
                        mDragStartX = endX;
                        mDragStartY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        /*保存最新的数据*/
                        mSharedPreferences.edit().putInt("lastX", mDrag.getLeft()).putInt("lastY", mDrag.getTop()).commit();
                        break;
                }
                return false;
            }
        });
    }
}
