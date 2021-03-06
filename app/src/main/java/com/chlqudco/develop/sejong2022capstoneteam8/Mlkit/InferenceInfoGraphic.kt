/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chlqudco.develop.sejong2022capstoneteam8.Mlkit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.GraphicOverlay.Graphic

/** Graphic instance for rendering inference info (latency, FPS, resolution) in an overlay view.  */
class InferenceInfoGraphic(
    private val overlay: GraphicOverlay,
    private val frameLatency: Long,
    private val detectorLatency: Long,
    // Only valid when a stream of input images is being processed. Null for single image mode.
    private val framesPerSecond: Int?
) : Graphic(overlay) {
    private val textPaint: Paint
    private var showLatencyInfo = true

    /** Creates an [InferenceInfoGraphic] to only display image size.  */
    constructor(overlay: GraphicOverlay) : this(overlay, 0, 0, null) {
        showLatencyInfo = false
    }

    @Synchronized
    override fun draw(canvas: Canvas) {
        val x = TEXT_SIZE * 0.5f
        val y = TEXT_SIZE * 1.5f
        if (!showLatencyInfo) {
            return
        }
        // Draw FPS (if valid) and inference latency
        if (framesPerSecond != null) {
            canvas.drawText(
                "FPS: $framesPerSecond, Frame latency: $frameLatency ms",
                x,
                y,
                textPaint
            )
        } else {
            canvas.drawText("Frame latency: $frameLatency ms", x, y + TEXT_SIZE, textPaint)
        }
        canvas.drawText(
            "Detector latency: $detectorLatency ms", x, y + TEXT_SIZE, textPaint
        )
    }

    companion object {
        private const val TEXT_COLOR = Color.WHITE
        private const val TEXT_SIZE = 60.0f
    }

    init {
        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        textPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)
        postInvalidate()
    }
}