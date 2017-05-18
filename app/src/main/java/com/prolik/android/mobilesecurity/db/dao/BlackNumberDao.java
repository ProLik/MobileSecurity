package com.prolik.android.mobilesecurity.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prolik.android.mobilesecurity.bean.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProLik on 2016/2/15.
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper mBlackNumberOpenHelper;

    public BlackNumberDao(Context context) {
        mBlackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }


    /**
     * 增加黑名单
     * @param number 黑名单号码
     * @param mode   拦截模式
     * @return
     */
    public boolean add(String number,String mode){
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number",number);
        contentValues.put("mode",mode);
        long _id =  db.insert(BlackNumberOpenHelper.sTABLE_NAME,null,contentValues);
        db.close();
        return _id == -1 ? false : true;
    }

    /**
     * 删除黑名单
     * @param number  黑名单号码
     * @return
     */
    public boolean delete(String number){
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        int count =  db.delete(BlackNumberOpenHelper.sTABLE_NAME, BlackNumberOpenHelper.sTABLE_COLUMN_NUMBER + " = ?", new String[]{number});
        db.close();
        return count == 0 ? false : true;
    }

    /**
     * 通过电话号码修改拦截模式
     * @param number
     * @param mode
     * @return
     */
    public boolean changeNumberMode(String number,String mode){
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode",mode);
        int count = db.update(BlackNumberOpenHelper.sTABLE_NAME, contentValues, "number = ?", new String[]{number});
        db.close();
        return count > 0 ? true : false;
    }

    /**
     * 根据电话号码返回拦截模式
     * @param number
     * @return
     */
    public String findNumber(String number){
        String mode = "";
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(BlackNumberOpenHelper.sTABLE_NAME, new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            mode = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return mode;
    }

    /**
     * 查询所有黑名单
     * @return
     */
    public List<BlackNumber> findAll(){
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(BlackNumberOpenHelper.sTABLE_NAME, new String[]{"number", "mode"}, null, null, null, null, null);
        List<BlackNumber> list = new ArrayList<BlackNumber>();
        while (cursor.moveToNext()){
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setNumber(cursor.getString(0));
            blackNumber.setMode(cursor.getString(1));
            list.add(blackNumber);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 分页加载数据
     * @param pageNumber  当前是哪几页
     * @param pageSize    每一页有几条数据
     * @return
     * limit  表示限制当前有多少数据
     * offset 表示跳过，从第几条开始
     */
    public List<BlackNumber> findPart(int pageNumber,int pageSize){
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from " + BlackNumberOpenHelper.sTABLE_NAME + " limit ? offset ?"
                , new String[]{String.valueOf(pageSize), String.valueOf(pageNumber * pageSize)});
        List<BlackNumber> list = new ArrayList<BlackNumber>();
        while (cursor.moveToNext()){
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setNumber(cursor.getString(0));
            blackNumber.setMode(cursor.getString(1));
            list.add(blackNumber);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 分批加载数据
     * @param startIndex
     * @param maxCount
     * @return
     */
    public List<BlackNumber> findPart2(int startIndex,int maxCount){
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from " + BlackNumberOpenHelper.sTABLE_NAME + " limit ? offset ?", new String[]{ String.valueOf(maxCount),String.valueOf(startIndex)});
        List<BlackNumber> list = new ArrayList<BlackNumber>();
        while (cursor.moveToNext()){
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setNumber(cursor.getString(0));
            blackNumber.setMode(cursor.getString(1));
            list.add(blackNumber);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取总记录数
     * @return
     */
    public int getTotalNumber(){
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + BlackNumberOpenHelper.sTABLE_NAME, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }


    public void deleteAll() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        db.delete(BlackNumberOpenHelper.sTABLE_NAME, null, null);
        db.close();
    }
}
