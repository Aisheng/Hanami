package com.hanami

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.hanami.common.utils.CutoutUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_show_float_view.setOnClickListener(this::startFloatService)

        tv_text2.setOnClickListener { ll_container.removeView(tv_text1) }

        LogUtil.log(Build.VERSION.SDK_INT.toString())
    }

    private fun startFloatService(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, FloatService::class.java))
                finish()
            } else {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 99)
            }
        } else {
            startService(Intent(this, FloatService::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
                } else {
                    startService(Intent(this, FloatService::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.log("huawei --> ${CutoutUtil.hasNotchAtHuawei(this)}")
        LogUtil.log("xiaomo --> ${CutoutUtil.getInt("ro.miui.notch", this)}")
        LogUtil.log("has cutout --> ${CutoutUtil.hasNotchScreen(this)}")

    }

}
