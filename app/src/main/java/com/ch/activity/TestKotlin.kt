package com.ch.activity

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.TypedValue
import android.widget.TextSwitcher
import android.widget.TextView

class TestKotlin {
    internal var textSwitcher: TextSwitcher? = null

    internal var handler = Handler()
    internal var runnable: Runnable = object : Runnable {
        override fun run() {

            handler.postDelayed(this, 2000)
        }
    }

    private fun testFun(context: Context) {
        textSwitcher!!.setFactory {
            val tv = TextView(context)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            tv.setTextColor(Color.MAGENTA)
            tv
        }
    }

    //事件处理函数，控制显示下一个字符串
    fun next(tv: String) {
        textSwitcher!!.setText(tv)
        handler.postDelayed(runnable, 2000)//每两秒执行一次runnable.
    }
}
