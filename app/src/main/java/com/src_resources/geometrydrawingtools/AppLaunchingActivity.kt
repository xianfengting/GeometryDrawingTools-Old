package com.src_resources.geometrydrawingtools

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AppLaunchingActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_APP_MAIN_ACTIVITY = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_launching)

        // 延迟两秒启动 AppMainActivity 。
        object : Thread("AppMainActivity-LauncherThread") {
            override fun run() {
                Thread.sleep(2000)
                val intent = Intent(this@AppLaunchingActivity, AppMainActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_APP_MAIN_ACTIVITY)
            }
        }.let {
            it.isDaemon = true
            it.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 如果是 AppMainActivity 返回过来的结果（说明 AppMainActivity 结束了）。
            REQUEST_CODE_APP_MAIN_ACTIVITY -> {
                // 结束当前 Activity （退出程序）。
                finish()
            }
        }
    }
}
