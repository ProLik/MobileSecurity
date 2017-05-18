package com.prolik.android.mobilesecurity.db.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ProLik on 2016/2/15.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

    public static final String sTABLE_NAME = "blacknumber";
    public static final String sTABLE_COLUMN_NUMBER = "number";
    public static final String sTABLE_COLUMN_MODE = "mode";

    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * blacknumber 表名
     * _id 主键自动增长
     * number 电话号码
     * mode 拦截模式 电话拦截 短信拦截
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + sTABLE_NAME + "  (_id integer primary key autoincrement," + sTABLE_COLUMN_NUMBER + " varchar(20)," + sTABLE_COLUMN_MODE + " varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
