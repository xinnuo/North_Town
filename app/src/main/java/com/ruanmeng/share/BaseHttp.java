package com.ruanmeng.share;

import com.ruanmeng.north_town.BuildConfig;

/**
 * 项目名称：North_Town
 * 创建人：小卷毛
 * 创建时间：2018-03-13 16:34
 */

public class BaseHttp {

    private static String baseUrl = BuildConfig.API_HOST;
    private static String baseIp = baseUrl + "/api";
    public static String baseImg = baseUrl + "/";

    public static String account_login = baseIp + "/account_login.rm";     //登录√
    public static String get_smscode = baseIp + "/get_smscode.rm";         //获取验证码√
    public static String update_pwd = baseIp + "/update_pwd.rm";           //忘记密码√
    public static String userhead_update = baseIp + "/userhead_update.rm"; //上传头像√
    public static String userinfo_update = baseIp + "/userinfo_update.rm"; //修改姓名√
    public static String update_pwd2 = baseIp + "/update_pwd2.rm";         //修改密码√

    public static String yjgz_center = baseIp + "/yjgz_center.rm";             //佣金规则√
    public static String about_us = baseIp + "/about_us.rm";                   //关于我们√
    public static String get_version_staff = baseIp + "/get_version_staff.rm"; //版本升级√
}
