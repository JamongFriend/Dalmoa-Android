package com.dalmoa.android.feature.subscribe.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.dalmoa.android.model.SubCategory

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: Map<SubCategory, Double> = emptyMap()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    private val colors = mapOf(
        SubCategory.OTT to Color.parseColor("#FF5252"),     // Red
        SubCategory.MUSIC to Color.parseColor("#FF4081"),   // Pink
        SubCategory.GAME to Color.parseColor("#7C4DFF"),    // Deep Purple
        SubCategory.LIFESTYLE to Color.parseColor("#448AFF"), // Blue
        SubCategory.FINANCE to Color.parseColor("#00E676"),   // Green
        SubCategory.ETC to Color.parseColor("#9E9E9E")      // Grey
    )

    fun setData(newData: Map<SubCategory, Double>) {
        this.data = newData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val total = data.values.sum()
        if (total == 0.0) return

        val width = width.toFloat()
        val height = height.toFloat()
        val size = if (width < height) width * 0.8f else height * 0.8f
        
        rectF.set(
            (width - size) / 2,
            (height - size) / 2,
            (width + size) / 2,
            (height + size) / 2
        )

        var startAngle = -90f
        data.forEach { (category, value) ->
            val sweepAngle = (value / total * 360).toFloat()
            paint.color = colors[category] ?: Color.LTGRAY
            paint.style = Paint.Style.FILL
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)
            startAngle += sweepAngle
        }

        // Draw center hole for Donut effect
        paint.color = Color.WHITE
        canvas.drawCircle(width / 2, height / 2, size * 0.3f, paint)
    }
}
