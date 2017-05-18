package com.prolik.android.mobilesecurity.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.prolik.android.mobilesecurity.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSafeService extends Service {
    private BlackNumberDao blackNumberDao;
    private  InnerReceiver innerReceiver;
    private TelephonyManager mTelephonyManager;
    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberDao = new BlackNumberDao(this);

        //获取系统的电话服务
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        //初始化短信广播
        innerReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver( innerReceiver,filter);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听电话状态改变
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
               //电话闲置
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                //电话铃响
                case TelephonyManager.CALL_STATE_RINGING:
                    Uri uri = Uri.parse("content://call_log/calls");
                    getContentResolver().registerContentObserver(uri, true, new CallLogObserver(new Handler(), incomingNumber));
                    String mode = blackNumberDao.findNumber(incomingNumber);
                    if(mode.equals("1") || mode.equals("2")){
                        endCall();
                    }
                    break;
                //电话接通
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
        }


    }
    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射实现当前方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent. getExtras().get("pdus");

            for (Object object : objects) {// 短信最多140字节,
                // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                String messageBody = message.getMessageBody();// 短信内容
                //通过短信的电话号码查询拦截的模式
                String mode = blackNumberDao.findNumber(originatingAddress);
                /**
                 * 黑名单拦截模式
                 * 1 全部拦截 电话拦截 + 短信拦截
                 * 2 电话拦截
                 * 3 短信拦截
                 */
                if(mode.equals("1")){
                    abortBroadcast();
                }else if(mode.equals("3")){
                    abortBroadcast();
                }
            }
        }
    }
    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;
        public CallLogObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }


    }
    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingNumber});
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(innerReceiver);
        super.onDestroy();
    }


}
