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
        var userHead: String = "",
        var userName: String = "",
        var vipNo: String = "",
        var investType: String = "",
        var status: String = "",

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

        //住宅名称
        var villageId: String = "",
        var villageName: String = "",

        //关系列表
        var relationshipId: String = "",
        var relationshipName: String = "",

        //单位列表
        var unitTypeId: String = "",
        var unitTypeName: String = "",

        //银行列表
        var bankId: String = "",
        var bankName: String = "",

        //财务录入列表
        var managerName: String = "",
        var remark: String = "",
        var receiptNo: String = "",

        //资料列表
        var amount: String = "",
        var content: String = "",

        //客户订单
        var beginDate: String = "",
        var endDate: String = "",
        var productName: String = "",
        var purchaseId: String = "",
        var rate: String = "",
        var years: String = "",
        var stock: String = "",

        //支付方式
        var payTypeId: String = "",
        var payTypeName: String = "",

        //收据类型
        var receiptTypeId: String = "",
        var receiptTypeName: String = "",

        //投资类型
        var investTypeId: String = "",
        var investTypeName: String = "",

        //经纪人佣金
        var commission: String = "",
        var createDate: String = "",
        var managerInfoName: String = "",
        var ispay: String = "",

        //老带新佣金
        var introducerInfoName: String = "",
        var introducerProfit: String = "",
        var profitAll: String = "",
        var sumAll: String = "",

        //部门列表
        var departmentCode: String = "",
        var departmentId: String = "",
        var departmentName: String = "",

        //产品列表
        var img: String = "",
        var productId: String = "",
        var productType: String = "",
        var isApp: String = "",

        //利率列表
        var max: String = "",
        var min: String = "",
        var cashRate: String = "",
        var surplusRate: String = "",

        //统计列表
        var sum: String = "",
        var managerInfoId: String = "",
        var incrementAmount: String = "",
        var retreatAmount: String = "",
        var stockAmount: String = "",

        //统计详情
        var managerContinueRate: String = "0",
        var managerQuitRate: String = "0",
        var managerSum: String = "0",
        var managerSumContinue: String = "0",
        var managerSumIncreased: String = "0",
        var managerSumOut: String = "0",

        //公司列表
        var commponyName: String = "",
        var compName: String = "",
        var legalMan: String = "",
        var companyId: String = "",
        var numPeople: String = "",
        var surplus: String = "",

        //组列表
        var businessGroupId: String = "",
        var name: String = "",

        //意向列表
        var intentionId: String = "",
        var intentionName: String = "",
        var intentionDegree: String = "",
        var intentionDegreeId: String = "",

        //审核列表
        var serviceCheckDate: String = "",
        var paytypeName: String = ""
) : Serializable