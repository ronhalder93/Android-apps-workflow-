package com.ronhalder.rgbkeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(0xFF10121B.toInt())
        }
        val title = TextView(this).apply {
            text = "RGB GLOW KEYBOARD"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(0xFF36F5FF.toInt())
        }
        val info = TextView(this).apply {
            text = "Animated RGB keys • English QWERTY • Emoji and number row"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(0xFFE5E7EB.toInt())
            setPadding(0, 18, 0, 28)
        }
        val enable = Button(this).apply {
            text = "1. Enable keyboard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        }
        val choose = Button(this).apply {
            text = "2. Select RGB keyboard"
            setOnClickListener {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }
        }
        root.addView(title)
        root.addView(info)
        root.addView(enable)
        root.addView(choose)
        setContentView(root)
    }
}
