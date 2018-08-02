package com.ruanmeng.north_town

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import com.luck.picture.lib.tools.PictureFileUtils
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.GlideCacheUtil
import com.ruanmeng.utils.OkGoUpdateHttpUtil
import com.ruanmeng.utils.Tools
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.updateApp
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_cache.setRightString(GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity))
        setting_version.setRightString("v" + Tools.getVersion(baseContext))

        setting_about.setOnClickListener {
            startActivityEx<WebActivity>("title" to "关于我们")
        }
        setting_cache.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("清空缓存")
                    .setMessage("确定要清空缓存吗？")
                    .setPositiveButton("清空") { dialog, _ ->
                        dialog.dismiss()

                        GlideCacheUtil.getInstance().clearImageAllCache(baseContext)
                        PictureFileUtils.deleteCacheDirFile(baseContext)
                        setting_cache.setRightString("0B")
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
        setting_version.setOnClickListener { checkUpdate() }
        setting_quit.setOnClickListener {
            AlertDialog.Builder(baseContext)
                    .setTitle("退出登录")
                    .setMessage("确定要退出当前账号吗？")
                    .setPositiveButton("退出") { _, _ ->
                        startActivityEx<LoginActivity>("offLine" to true)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create()
                    .show()
        }
    }

    /**
     * 版本更新
     */
    private fun checkUpdate() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath + Const.SAVE_FILE

        updateApp(BaseHttp.get_version_staff, OkGoUpdateHttpUtil()) {
            //设置请求方式，默认get
            isPost = true
            //设置apk下砸路径
            targetPath = path
        }.check {
            onBefore { showLoadingDialog() }
            parseJson {
                val obj = JSONObject(it)
                val version_new = obj.optString("versionNo").replace(".", "").toInt()
                val version_old = Tools.getVerCode(baseContext)

                UpdateAppBean()
                        //（必须）是否更新Yes,No
                        .setUpdate(if (version_new > version_old) "Yes" else "No")
                        //（必须）新版本号，
                        .setNewVersion(obj.optString("versionNo"))
                        //（必须）下载地址
                        // .setApkFileUrl(obj.optString("url"))
                        .setApkFileUrl(Const.URL_DOWNLOAD)
                        //（必须）更新内容
                        .setUpdateLog(obj.optString("content"))
                        //是否强制更新，可以不设置
                        .setConstraint(false)
            }
            hasNewApp { updateApp, updateAppManager -> showDownloadDialog(updateApp, updateAppManager) }
            noNewApp { showToast("当前已是最新版本！") }
            onAfter { cancelLoadingDialog() }
        }
    }

    /**
     * 自定义对话框
     */
    private fun showDownloadDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("版本更新", "是否升级到${updateApp.newVersion}版本？\n\n${updateApp.updateLog}") {
            positiveButton("升级") { updateAppManager.download() }
            negativeButton("暂不升级") { }
            show()
        }
    }
}
