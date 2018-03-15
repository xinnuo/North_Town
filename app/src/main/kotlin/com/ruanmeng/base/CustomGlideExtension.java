package com.ruanmeng.base;

import android.annotation.SuppressLint;
import android.support.annotation.DrawableRes;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;

@GlideExtension
public class CustomGlideExtension {
    /**
     * 将构造方法设为私有，作为工具类使用
     */
    private CustomGlideExtension() { }

    /**
     * 1.自己新增的方法的第一个参数必须是RequestOptions options
     * 2.方法必须是静态的
     */
    @SuppressLint("CheckResult")
    @GlideOption
    public static void resourceOption(RequestOptions options, @DrawableRes int resourceId) {
        options.centerCrop()
                .placeholder(resourceId)
                .error(resourceId)
                .dontAnimate();
    }
}
