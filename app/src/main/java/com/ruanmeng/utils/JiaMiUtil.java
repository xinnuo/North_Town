package com.ruanmeng.utils;

/**
 * 项目名称：North_Town
 * 创建人：小卷毛
 * 创建时间：2018-03-15 15:08
 */

public class JiaMiUtil {
    public static String appkey = "c306e6eb-fdba-11e7-9bb0-00163e0004bf";
    public static String sercret = "jZ0F9RTa5Y4NDZ95C4n38SuddBgtSw05";
    public static String DESIV = "B110BA97";

    public static String getkey(String time) {
        String a;
        String hh = MD5Util.md5Password(time).substring(0, 8);
        a = hh.toUpperCase() + appkey;
        String b = MD5Util.md5Password(a);
        a = b.substring(12, 20).toLowerCase();
        return a;
    }

    public static String getiv(String time) {
        String a;
        StringBuilder buffer3 = new StringBuilder(MD5Util.md5Password(time));
        String dd = buffer3.substring(12, 20);
        a = dd.toLowerCase() + sercret;
        StringBuilder buffer4 = new StringBuilder(MD5Util.md5Password(a));
        a = buffer4.substring(24, 32).toUpperCase();
        return a;
    }
}
