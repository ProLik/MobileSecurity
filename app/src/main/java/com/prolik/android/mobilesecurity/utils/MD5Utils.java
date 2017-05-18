package com.prolik.android.mobilesecurity.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ProLik on 2015/12/10.
 */
public class MD5Utils {

    public static String enCode(String str){
        MessageDigest messageDigest = null;
        String result = "";
        try {
            messageDigest = MessageDigest.getInstance("MD5");//获取MD5对象
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest!=null) {
            byte[] bytes = messageDigest.digest(str.getBytes());
            int length = bytes.length;
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<length;i++){
                int b = bytes[i] & 0xff;// 获取字节的低八位有效值
                String hexString  = Integer.toHexString(b);//将整数转成16进制数
                sb.append(hexString);
            }
            result = sb.toString();
        }
        return result;
    }

}
