package com.prolik.android.mobilesecurity;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.prolik.android.mobilesecurity.bean.BlackNumber;
import com.prolik.android.mobilesecurity.db.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public Context mContext;
    @Override
    protected void setUp() throws Exception {
        this.mContext = getContext();
        super.setUp();
    }

    public void testAdd(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 0; i < 20000; i++) {
            Long number = 13300000000l +i;
            dao.add(number +"",String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDeleteAll(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        dao.deleteAll();
    }

    public void testFind(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String number = dao.findNumber("13300000004");
        System.out.println(number);
    }

    public void testFindAll(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackNumber> blackNumbers = dao.findAll();
        for (BlackNumber blackNumber : blackNumbers) {
            System.out.println(blackNumber.getMode() + "" + blackNumber.getNumber());
        }
    }

    public void testDelete(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("13300000000");
        assertEquals(true,delete);
    }
}