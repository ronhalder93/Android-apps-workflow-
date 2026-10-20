package com.ronhalder.rgbkeyboard

import android.inputmethodservice.InputMethodService
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import android.view.View
import android.view.MotionEvent
import android.view.inputmethod.InputConnection
import kotlin.math.floor

class RgbKeyboardService : InputMethodService() {
    override fun onCreateInputView(): View = RgbKeysView()

    inner class RgbKeysView : View(this@RgbKeyboardService) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rows = listOf("1234567890", "qwertyuiop", "asdfghjkl", "⇧zxcvbnm⌫", "?123,🌐☻ space ↵")
        private var phase = 0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.rgb(12, 14, 24))
            val rowH = height / rows.size.toFloat()
            rows.forEachIndexed { r, row ->
                val chars = if (row == " ?") listOf(row) else splitKeys(row)
                val gap = 5f
                val keyW = (width - gap * (chars.size + 1)) / chars.size.toFloat()
                chars.forEachIndexed { c, key ->
                    val hue = (phase + (r * 38) + (c * 26)) % 360f
                    paint.color = Color.HSVToColor(floatArrayOf(hue, 0.82f, 1f))
                    paint.setShadowLayer(9f, 0f, 0f, paint.color)
                    val left = gap + c * (keyW + gap)
                    val top = r * rowH + 5f
                    canvas.drawRoundRect(RectF(left, top, left + keyW, top + rowH - 10f), 10f, 10f, paint)
                    paint.clearShadowLayer()
                    paint.color = Color.rgb(15, 18, 30)
                    canvas.drawRoundRect(RectF(left + 2f, top + 2f, left + keyW - 2f, top + rowH - 12f), 8f, 8f, paint)
                    paint.color = Color.HSVToColor(floatArrayOf(hue, 0.55f, 1f))
                    paint.textAlign = Paint.Align.CENTER
                    paint.textSize = if (key == "space") 14f else 20f
                    canvas.drawText(if (key == "space") "English (US)" else key, left + keyW / 2f, top + rowH / 2f + 7f, paint)
                }
            }
            phase = (phase + 1.2f) % 360f
            postInvalidateDelayed(45)
        }

        private fun splitKeys(row: String): List<String> = when (row) {
            "⇧zxcvbnm⌫" -> listOf("⇧","z","x","c","v","b","n","m","⌫")
            else -> if (row.contains("space")) listOf("?123", ",", "🌐", "☻", "space", ".", "↵")
                   else row.map { it.toString() }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action != MotionEvent.ACTION_UP) return true
            val rowH = height / rows.size.toFloat()
            val r = floor(event.y / rowH).toInt().coerceIn(0, rows.size - 1)
            val row = rows[r]
            val keys = when (r) {
                0 -> row.map { it.toString() }
                1 -> row.map { it.toString() }
                2 -> row.map { it.toString() }
                3 -> listOf("⇧","z","x","c","v","b","n","m","⌫")
                else -> listOf("?123", ",", "🌐", "☻", "space", ".", "↵")
            }
            val gap = 5f
            val keyW = (width - gap * (keys.size + 1)) / keys.size.toFloat()
            val c = floor((event.x - gap) / (keyW + gap)).toInt().coerceIn(0, keys.size - 1)
            val key = keys[c]
            val ic: InputConnection = currentInputConnection ?: return true
            when (key) {
                "⌫" -> ic.deleteSurroundingText(1, 0)
                "↵" -> ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
                "space" -> ic.commitText(" ", 1)
                "⇧" -> { }
                "?123", "🌐", "☻" -> { }
                else -> ic.commitText(key, 1)
            }
            return true
        }
    }
}
