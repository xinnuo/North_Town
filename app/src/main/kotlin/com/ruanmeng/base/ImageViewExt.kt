/**
 * created by 小卷毛, 2018/3/29 0029
 * Copyright (c) 2018, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng.base

import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ruanmeng.north_town.R

/**
 * 项目名称：North_Town
 * 创建人：小卷毛
 * 创建时间：2018-03-29 12:51
 */
fun ImageView.setImageURL(url: String) = GlideApp.with(context)
        .load(url)
        .resourceOption(R.mipmap.default_user)
        .into(this)

fun ImageView.setImageURL(url: String, @DrawableRes resourceId: Int) = Glide.with(context)
        .load(url)
        .apply(RequestOptions
                .centerCropTransform()
                .placeholder(resourceId)
                .error(resourceId)
                .dontAnimate())
        .into(this)