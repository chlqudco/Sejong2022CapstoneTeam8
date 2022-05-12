package com.chlqudco.develop.sejong2022capstoneteam8.Mlkit

import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.GraphicOverlay
import android.graphics.Bitmap
import android.graphics.Canvas
import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.GraphicOverlay.Graphic

/** Draw camera image to background.  */ // 실시간 이미지 그려주는 친구
class CameraImageGraphic(overlay: GraphicOverlay?, private val bitmap: Bitmap) : Graphic(overlay) {
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, transformationMatrix, null)
    }
}