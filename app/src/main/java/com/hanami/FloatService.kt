package com.hanami

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView

/**
 *
 * @author lidaisheng
 * @date 2019/4/14
 */
class FloatService : Service() {


    private val layoutParams by lazy { WindowManager.LayoutParams() }
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val floatWindow by lazy { LayoutInflater.from(this).inflate(R.layout.float_window_layout, null) }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun showFloatWindow() {

       // val tvHello = floatWindow.findViewById<TextView>(R.id.tv_hello)
        floatWindow.setOnTouchListener(FloatingOnTouchListener())
       // floatWindow.setOnClickListener { tvHello.visibility = if (tvHello.visibility == View.GONE) View.VISIBLE else View.GONE }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.x = 300
        layoutParams.y = 300
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowManager.addView(floatWindow, layoutParams)
    }

    private inner class FloatingOnTouchListener : View.OnTouchListener {

        var x = 0
        var y = 0

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                    LogUtil.log("MotionEvent.ACTION_DOWN x --> $x y --> $y")
                }
                MotionEvent.ACTION_MOVE -> {
                    var nowX = event.rawX.toInt()
                    var nowY = event.rawY.toInt()
                    var moveX = nowX - x
                    var moveY = nowY - y
                    x = nowX
                    y = nowY
                    layoutParams.x = layoutParams.x + moveX
                    layoutParams.y = layoutParams.y + moveY
                    LogUtil.log("MotionEvent.ACTION_MOVE layoutParams.x --> $x layoutParams.y --> $y")
                    windowManager.updateViewLayout(floatWindow, layoutParams)
                }
            }
            return false
        }
    }
}