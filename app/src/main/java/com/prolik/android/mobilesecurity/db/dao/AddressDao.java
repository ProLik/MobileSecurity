package com.prolik.android.mobilesecurity.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by ProLik on 2016/1/14.
 */
public class AddressDao {
    public static final String Path = "data/data/com.prolik.android.mobilesecurity/files/address.db";

    public static String getAddress(String number){
        String address = "未知号码";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Path, null, SQLiteDatabase.OPEN_READONLY);
        if(number.matches("^1[3-8]\\d{9}$")){
            Cursor cursor = db.rawQuery("select location from data2 where id = (select outkey from data1 where id = ? )",new String []{number.substring(0, 7)});
            if(cursor.moveToNext()){
                address = cursor.getString(0);
            }
            cursor.close();
        }else if(number.matches("^\\d+$")){//匹配数字
            switch (number.length()){
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    address="本地电话";
                    break;
                default:
                      if(number.startsWith("0") && number.length() > 10){//查询4位区号
                          Cursor cursor = db.rawQuery("select location from data2 where area =?",new String[]{number.substring(1,4)});
                          if(cursor.moveToNext()){
                              address = cursor.getString(0);
                          }else{
                              cursor.close();
                              cursor = db.rawQuery("select location from data2 where area =?",new String[]{number.substring(1,3)});
                              if (cursor.moveToNext()){
                                  address = cursor.getString(0);
                              }
                          }
                          cursor.close();
                      }
                    break;
            }
        }
        db.close();
        return address;
    }

}
