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
    public static String userinfo = baseIp + "/userinfo.rm";               //个人中心√
    public static String userhead_update = baseIp + "/userhead_update.rm"; //上传头像√
    public static String userinfo_update = baseIp + "/userinfo_update.rm"; //修改姓名√
    public static String update_pwd2 = baseIp + "/update_pwd2.rm";         //修改密码√

    public static String customer_list = baseIp + "/customer_list.rm";         //报备列表√
    public static String other_userinfo = baseIp + "/other_userinfo.rm";       //客户详情√
    public static String customer_sub = baseIp + "/customer_sub.rm";           //添加客户√
    public static String villagetype_list = baseIp + "/villagetype_list.rm";   //小区类型√
    public static String relationship_list = baseIp + "/relationship_list.rm"; //关系列表√
    public static String preference_list = baseIp + "/preference_list.rm";     //偏好列表√
    public static String industry_list = baseIp + "/industry_list.rm";         //职业列表√
    public static String unittype_list = baseIp + "/unittype_list.rm";         //单位类型√
    public static String purchase_sub = baseIp + "/purchase_sub.rm";           //添加订单√
    public static String bank_list = baseIp + "/bank_list.rm";                 //银行列表√
    public static String staff_list = baseIp + "/staff_list.rm";               //经纪人列表√

    public static String customer_list_all = baseIp + "/customer_list_all.rm";                 //资料列表√
    public static String customer_purchase_list = baseIp + "/customer_purchase_list.rm";       //客户订单√
    public static String customer_purchase_details = baseIp + "/customer_purchase_details.rm"; //订单详情√

    public static String paytype_list = baseIp + "/paytype_list.rm"; //支付方式√
    public static String receipttype_list = baseIp + "/receipttype_list.rm"; //收据类型√
    public static String investtype_list = baseIp + "/investtype_list.rm"; //投资类型√

    public static String achievement_day_list = baseIp + "/achievement_day_list.rm";     //日业绩√
    public static String achievement_week_list = baseIp + "/achievement_week_list.rm";   //周业绩√
    public static String achievement_month_list = baseIp + "/achievement_month_list.rm"; //月业绩√
    public static String achievement_year_list = baseIp + "/achievement_year_list.rm";   //年业绩√

    public static String commission_list = baseIp + "/commission_list.rm"; //经纪人佣金√
    public static String department_commission_list = baseIp + "/department_commission_list.rm"; //销售部佣金√

    public static String product_list = baseIp + "/product_list.rm"; //产品列表√
    public static String get_product = baseIp + "/get_product.rm";   //产品详情√

    public static String yjgz_center = baseIp + "/yjgz_center.rm";             //佣金规则√
    public static String about_us = baseIp + "/about_us.rm";                   //关于我们√
    public static String get_version_staff = baseIp + "/get_version_staff.rm"; //版本升级√
}
