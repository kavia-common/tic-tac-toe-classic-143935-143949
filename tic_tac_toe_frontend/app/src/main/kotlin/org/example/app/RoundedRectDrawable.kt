package org.example.app

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * PUBLIC_INTERFACE
 * RoundedRectDrawable draws a rounded rectangle with optional subtle shadow-like effect.
 * This avoids XML shape complexity while giving modern rounded cards/buttons.
 *
 * Usage notes:
 * - This class is in the same package as MainActivity for straightforward reference.
 * - Shadow layer is subtle and should render fine across API levels; if needed, reduce blur for performance.
 *
 * @param fillColor The interior color of the rounded rectangle.
 * @param cornerRadius The corner radius in pixels.
 */
class RoundedRectDrawable(
    private val fillColor: Int,
    private val cornerRadius: Float
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = fillColor
        // Subtle inner shadow illusion by shadow layer
        setShadowLayer(6f, 0f, 2f, Color.argb(40, 0, 0, 0))
    }

    private val rectF = RectF()

    override fun draw(canvas: Canvas) {
        rectF.set(bounds)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
