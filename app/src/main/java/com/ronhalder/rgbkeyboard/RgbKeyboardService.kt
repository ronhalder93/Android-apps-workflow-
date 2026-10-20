package com.ronhalder.rgbkeyboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.inputmethodservice.InputMethodService
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import kotlin.math.max

private data class KeyboardKey(val label: String, val value: String = label, val weight: Float = 1f)
private data class KeyboardHit(val key: KeyboardKey, val rect: RectF)

class RgbKeyboardService : InputMethodService() {
    override fun onCreateInputView(): View = RgbKeysView()

    inner class RgbKeysView : View(this@RgbKeyboardService) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var phase = 0f
        private var shifted = false
        private var symbols = false
        private var hits: List<KeyboardHit> = emptyList()

        private fun rows(): List<List<KeyboardKey>> {
            return if (symbols) listOf(
                "1234567890".map { KeyboardKey(it.toString()) },
                listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")", "/").map { KeyboardKey(it) },
                listOf(KeyboardKey("<"), KeyboardKey("*"), KeyboardKey("\""), KeyboardKey("'"), KeyboardKey(":"), KeyboardKey(";"), KeyboardKey("!"), KeyboardKey("?"), KeyboardKey("⌫", "⌫", 1.3f)),
                listOf(KeyboardKey("ABC", "symbols"), KeyboardKey(",", ","), KeyboardKey("😊", "emoji"), KeyboardKey("English", "space", 4.2f), KeyboardKey(".", "."), KeyboardKey("↵", "enter", 1.3f))
            ) else listOf(
                "qwertyuiop".map { KeyboardKey(if (shifted) it.uppercase() else it.toString()) },
                "asdfghjkl".map { KeyboardKey(if (shifted) it.uppercase() else it.toString()) },
                listOf(KeyboardKey("⇧", "shift", 1.35f)) + "zxcvbnm".map { KeyboardKey(if (shifted) it.uppercase() else it.toString()) } + KeyboardKey("⌫", "⌫", 1.35f),
                listOf(KeyboardKey("?123", "symbols", 1.4f), KeyboardKey(",", ","), KeyboardKey("😊", "emoji"), KeyboardKey("English", "space", 4.4f), KeyboardKey(".", "."), KeyboardKey("↵", "enter", 1.4f))
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.rgb(13, 15, 25))
            val layout = rows()
            val gap = dp(5f)
            val rowGap = dp(7f)
            val rowHeight = (height - rowGap * (layout.size + 1)) / layout.size.toFloat()
            val newHits = mutableListOf<KeyboardHit>()
            layout.forEachIndexed { r, keys ->
                val totalWeight = keys.sumOf { it.weight.toDouble() }.toFloat()
                val available = width - gap * (keys.size + 1)
                var x = gap
                val y = rowGap + r * (rowHeight + rowGap)
                keys.forEach { key ->
                    val keyWidth = available * key.weight / totalWeight
                    val rect = RectF(x, y, x + keyWidth, y + rowHeight)
                    val hue = (phase + r * 27f + x / max(1f, width.toFloat()) * 130f) % 360f
                    val rgb = Color.HSVToColor(floatArrayOf(hue, .88f, 1f))
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = dp(2f)
                    paint.color = rgb
                    paint.setShadowLayer(dp(7f), 0f, 0f, rgb)
                    canvas.drawRoundRect(rect, dp(8f), dp(8f), paint)
                    paint.clearShadowLayer()
                    paint.style = Paint.Style.FILL
                    paint.color = if (key.value in listOf("shift", "⌫", "symbols", "enter")) Color.rgb(39, 43, 62) else Color.rgb(24, 27, 42)
                    canvas.drawRoundRect(RectF(rect.left + dp(2f), rect.top + dp(2f), rect.right - dp(2f), rect.bottom - dp(2f)), dp(7f), dp(7f), paint)
                    paint.color = Color.WHITE
                    paint.textAlign = Paint.Align.CENTER
                    paint.textSize = when {
                        key.value == "space" -> dp(12f)
                        key.label.length > 3 -> dp(12f)
                        else -> dp(20f)
                    }
                    val label = if (key.value == "space") "English (US)" else key.label
                    canvas.drawText(label, rect.centerX(), rect.centerY() - (paint.ascent() + paint.descent()) / 2f, paint)
                    newHits.add(KeyboardHit(key, rect))
                    x += keyWidth + gap
                }
            }
            hits = newHits
            phase = (phase + 1.5f) % 360f
            postInvalidateDelayed(45)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action != MotionEvent.ACTION_UP) return true
            val key = hits.firstOrNull { it.rect.contains(event.x, event.y) }?.key ?: return true
            val ic: InputConnection = currentInputConnection ?: return true
            when (key.value) {
                "⌫" -> ic.deleteSurroundingText(1, 0)
                "enter" -> ic.commitText("\n", 1)
                "space" -> ic.commitText(" ", 1)
                "shift" -> { shifted = !shifted; invalidate() }
                "symbols" -> { symbols = !symbols; invalidate() }
                "emoji" -> ic.commitText("😊", 1)
                else -> ic.commitText(key.value, 1)
            }
            return true
        }

        private fun dp(v: Float): Float = v * resources.displayMetrics.density
    }
}
