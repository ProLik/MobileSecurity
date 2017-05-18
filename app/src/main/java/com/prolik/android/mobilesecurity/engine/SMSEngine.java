package com.prolik.android.mobilesecurity.engine;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

import com.prolik.android.mobilesecurity.utils.Crypto;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ProLik on 2016/5/5.
 */
public class SMSEngine {

    public interface ShowProgress{
        //设置最大进度
        public void setMax(int max);
        //设置当前进度
        public void setProgress(int progerss);
        public void dismiss();
    }

    public static boolean getAllSMS(Context context, ShowProgress showProgress){
        boolean result = false;

        //判断当前sd卡状态
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //获取内容解析者
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            int count = cursor.getCount();//获取短信个数
            showProgress.setMax(count);
            int process = 0;
            /*生成XML文件*/
            XmlSerializer serializer = Xml.newSerializer();
            try {
                serializer.setOutput(new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"backupsms.xml")),"utf-8");
                //standalone : 是否是独立文件
                serializer.startDocument("utf-8",true);
                serializer.startTag(null,"smss");
                while (cursor.moveToNext()){
                    //增加处理速度
                    SystemClock.sleep(2000);
                    serializer.startTag(null,"sms");
                    serializer.attribute(null,"size",String.valueOf(count));
                    serializer.startTag(null, "address");
                    String address = cursor.getString(0);
                    //2.7设置文本内容
                    serializer.text(address);
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    String date = cursor.getString(1);
                    serializer.text(date);
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    String type = cursor.getString(2);
                    serializer.text(type);
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    String body = cursor.getString(3);
                    serializer.text(Crypto.encrypt("123",body));
                    serializer.endTag(null, "body");
                    serializer.endTag(null,"sms");
                    process ++;
                    showProgress.setProgress(process);

                }
                cursor.close();
                serializer.endTag(null,"smss");
                serializer.endDocument();
//              将数据写入文件中
                serializer.flush();
                showProgress.dismiss();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
