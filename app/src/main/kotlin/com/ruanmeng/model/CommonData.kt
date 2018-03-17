/**
 * created by 小卷毛, 2018/3/7 0007
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
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：North_Town
 * 创建人：小卷毛
 * 创建时间：2018-03-07 15:07
 */
data class CommonData(
        //报备列表
        var accountInfoId: String = "",
        var cardNo: String = "",
        var telephone: String = "",
        var userhead: String = "",
        var userName: String = "",

        //偏好列表
        var isChecked: Boolean = false,
        var preferenceId: String = "",
        var preferenceName: String = "",

        //职业列表
        var industryId: String = "",
        var industryName: String = "",

        //类型列表
        var villageTypeId: String = "",
        var villageTypeName: String = "",

        //单位列表
        var unitTypeId: String = "",
        var unitTypeName: String = "",

        //银行列表
        var bankId: String = "",
        var bankName: String = "",

        //资料列表
        var amount: String = "",

        //客户订单
        var beginDate: String = "",
        var endDate: String = "",
        var productName: String = "",
        var purchaseId: String = "",
        var rate: String = "",
        var years: String = "",

        //产品列表
        var img: String = "",
        var productId: String = "",

        //利率列表
        var max: String = "",
        var min: String = ""
) : Serializable