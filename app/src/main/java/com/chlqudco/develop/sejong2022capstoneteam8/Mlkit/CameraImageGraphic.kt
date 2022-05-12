
package com.chlqudco.develop.sejong2022capstoneteam8.Mlkit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/** Draw camera image to background. */
// 실시간 이미지 그려주는 친구
public class CameraImageGraphic extends GraphicOverlay.Graphic {

  private final Bitmap bitmap;

  public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
    super(overlay);
    this.bitmap = bitmap;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawBitmap(bitmap, getTransformationMatrix(), null);
  }
}
