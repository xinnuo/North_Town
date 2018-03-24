/**
 * created by 小卷毛, 2018/3/14 0014
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
@file:Suppress("NOTHING_TO_INLINE")

package com.ruanmeng.base

import android.text.Html
import android.text.Spanned
import android.widget.TextView
import com.ruanmeng.north_town.R

/**
 * 项目名称：North_Town
 * 创建人：小卷毛
 * 创建时间：2018-03-14 17:54
 */
inline fun TextView.setColor(text: String, key: String) {
    @Suppress("DEPRECATION")
    setText(Html.fromHtml(text.replace(key, "<font color='${resources.getColor(R.color.colorAccent)}'>$key</font>")))
}

inline fun TextView.setColor(text: String, key: String, color: String) {
    @Suppress("DEPRECATION")
    setText(Html.fromHtml(text.replace(key, "<font color='$color'>$key</font>")))
}

@Suppress("DEPRECATION")
fun getColorText(text: String, key: String): Spanned = Html.fromHtml(text.replace(key, "<font color='#F90629'>$key</font>"))

@Suppress("DEPRECATION")
fun getColorText(text: String, key: String, color: String): Spanned = Html.fromHtml(text.replace(key, "<font color='$color'>$key</font>"))