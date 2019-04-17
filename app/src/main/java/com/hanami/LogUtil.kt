package com.hanami

import android.util.Log

/**
 *
 * @author lidaisheng
 * @date 2019/4/14
 */
class LogUtil {
    companion object {

        const val TAG = "hanami-test"

        fun log(log: String) {
            Log.i(TAG, log)
        }
    }
}