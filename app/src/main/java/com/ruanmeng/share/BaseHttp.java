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

    public static String account_login = baseIp + "/account_login.rm"; //登录√
    public static String get_smscode = baseIp + "/get_smscode.rm"; //获取验证码√
    public static String update_pwd = baseIp + "/update_pwd.rm"; //忘记密码√
}
