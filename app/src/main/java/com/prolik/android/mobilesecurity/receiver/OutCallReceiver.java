package com.prolik.android.mobilesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.db.dao.AddressDao;


/*
* 监听去电广播需要权限 android.permission.PROCESS_OUTGOING_CALLS
* */
public class OutCallReceiver extends BroadcastReceiver {
    public OutCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();
        System.out.printf(number);
        Log.d("System.out","去电：" + number);
        String address = AddressDao.getAddress(number);
        Toast.makeText(context,address,Toast.LENGTH_LONG).show();
    }
}
