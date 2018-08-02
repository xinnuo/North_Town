/**
 * created by 小卷毛, 2018/3/22 0022
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
 * 创建时间：2018-03-22 16:57
 */
data class CommonModel(
        var count: String = "",
        var balance: String = "",
        var withdrawDeposit: String = "",
        var accountInfoCount: String = "",
        var introducerProfit: String = "0.00",
        var bl: List<CommonData> ?= ArrayList(),
        var accountInfoList: List<CommonData> ?= ArrayList(),
        var balanceLogList: List<CommonData> ?= ArrayList(),
        var companyList: List<CommonData> ?= ArrayList()
): Serializable