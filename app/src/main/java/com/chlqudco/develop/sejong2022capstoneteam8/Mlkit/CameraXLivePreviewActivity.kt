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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chlqudco.develop.sejong2022capstoneteam8.Fitness.AllFitnessEndActivity
import com.chlqudco.develop.sejong2022capstoneteam8.Fitness.SetEndActivity
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_COUNT
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_SET
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.common.MlKitException

/** Live preview demo app for ML Kit APIs using CameraX. */
class CameraXLivePreviewActivity : AppCompatActivity(){

  private val sharedPreferences by lazy { getSharedPreferences(SharedPreferenceKey.SETTING, Context.MODE_PRIVATE) }

  private var previewView : PreviewView? = null
  private var graphicOverlay: GraphicOverlay? = null
  private var cameraProvider: ProcessCameraProvider? = null
  private var previewUseCase: Preview? = null
  private var analysisUseCase: ImageAnalysis? = null
  private var imageProcessor: VisionImageProcessor? = null
  private var needUpdateGraphicOverlayImageSourceInfo = false
  private var selectedModel = "Pose Detection"
  private var lensFacing = CameraSelector.LENS_FACING_FRONT
  private var cameraSelector: CameraSelector? = null

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState != null) {
      selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, POSE_DETECTION)
    }
    cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    setContentView(R.layout.activity_vision_camerax_live_preview)

    //무슨 운동 하는지
    val mainTextView = findViewById<TextView>(R.id.VisionMainTextView)
    val fitnessType = sharedPreferences.getString(SharedPreferenceKey.FITNESS_CHOICE,"null")
    mainTextView.text = if (fitnessType=="pushups_down") "팔굽혀펴기 측정중" else "스쿼트 측정중"

    previewView = findViewById(R.id.preview_view)
    graphicOverlay = findViewById(R.id.graphic_overlay)

    ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
      .get(CameraXViewModel::class.java)
      .processCameraProvider
      .observe(this,
        Observer { provider: ProcessCameraProvider? ->
          cameraProvider = provider
          bindAllCameraUseCases()
        }
      )
  }

  override fun onSaveInstanceState(bundle: Bundle) {
    super.onSaveInstanceState(bundle)
    bundle.putString(STATE_SELECTED_MODEL, selectedModel)
  }

  public override fun onResume() {
    super.onResume()
    bindAllCameraUseCases()
  }

  override fun onPause() {
    super.onPause()

    imageProcessor?.run { this.stop() }
  }

  public override fun onDestroy() {
    super.onDestroy()
    imageProcessor?.run { this.stop() }
  }


  private fun bindAllCameraUseCases() {
    if (cameraProvider != null) {
      cameraProvider!!.unbindAll()
      bindPreviewUseCase()
      bindAnalysisUseCase()
    }
  }

  private fun bindPreviewUseCase() {
    if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
      return
    }
    if (cameraProvider == null) {
      return
    }
    if (previewUseCase != null) {
      cameraProvider!!.unbind(previewUseCase)
    }

    val builder = Preview.Builder()
    val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution)
    }
    previewUseCase = builder.build()
    previewUseCase!!.setSurfaceProvider(previewView!!.getSurfaceProvider())
    cameraProvider!!.bindToLifecycle(this, cameraSelector!!, previewUseCase)
  }

  private fun bindAnalysisUseCase() {
    if (cameraProvider == null) {
      return
    }
    if (analysisUseCase != null) {
      cameraProvider!!.unbind(analysisUseCase)
    }
    if (imageProcessor != null) {
      imageProcessor!!.stop()
    }
    val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
    val shouldShowInFrameLikelihood = PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
    val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
    val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
    val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)

    imageProcessor = PoseDetectorProcessor(this, poseDetectorOptions, shouldShowInFrameLikelihood, visualizeZ, rescaleZ, runClassification, true)

    val builder = ImageAnalysis.Builder()
    val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution)
    }
    analysisUseCase = builder.build()

    needUpdateGraphicOverlayImageSourceInfo = true

    analysisUseCase?.setAnalyzer(
      ContextCompat.getMainExecutor(this),
      ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
        if (needUpdateGraphicOverlayImageSourceInfo) {
          val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
          val rotationDegrees = imageProxy.imageInfo.rotationDegrees
          if (rotationDegrees == 0 || rotationDegrees == 180) {
            graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, isImageFlipped)
          } else {
            graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, isImageFlipped)
          }
          needUpdateGraphicOverlayImageSourceInfo = false
        }
        try {
          imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
        } catch (e: MlKitException) {
        }
      }
    )
    cameraProvider!!.bindToLifecycle( this, cameraSelector!!, analysisUseCase)
  }

  fun setEnd(){
    val intent = Intent(this,SetEndActivity::class.java)
    startActivity(intent)
    finish()
  }

  fun AllEnd(){
    val intent = Intent(this,AllFitnessEndActivity::class.java)
    startActivity(intent)
    finish()
  }

  companion object{

    private const val TAG = "CameraXLivePreview"
    private const val POSE_DETECTION = "Pose Detection"
    private const val STATE_SELECTED_MODEL = "selected_model"
  }

}
