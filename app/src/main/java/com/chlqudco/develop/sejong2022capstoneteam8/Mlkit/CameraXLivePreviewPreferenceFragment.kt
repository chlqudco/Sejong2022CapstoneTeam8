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

import android.content.Context
import android.preference.PreferenceCategory
import com.chlqudco.develop.sejong2022capstoneteam8.R
import androidx.camera.core.CameraSelector
import androidx.annotation.StringRes
import android.preference.ListPreference
import android.hardware.camera2.CameraCharacteristics
import android.graphics.SurfaceTexture
import android.preference.Preference.OnPreferenceChangeListener
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraAccessException
import android.preference.Preference
import java.util.*

/** CameraX 라이브 미리보기 데모 설정을 구성합니다.  */
open class CameraXLivePreviewPreferenceFragment : LivePreviewPreferenceFragment() {
    public override fun setUpCameraPreferences() {
        val cameraPreference =
            findPreference(getString(R.string.pref_category_key_camera)) as PreferenceCategory
        cameraPreference.removePreference(findPreference(getString(R.string.pref_key_rear_camera_preview_size)))
        cameraPreference.removePreference(findPreference(getString(R.string.pref_key_front_camera_preview_size)))
        setUpCameraXTargetAnalysisSizePreference(
            R.string.pref_key_camerax_rear_camera_target_resolution,
            CameraSelector.LENS_FACING_BACK
        )
        setUpCameraXTargetAnalysisSizePreference(
            R.string.pref_key_camerax_front_camera_target_resolution,
            CameraSelector.LENS_FACING_FRONT
        )
    }

    private fun setUpCameraXTargetAnalysisSizePreference(
        @StringRes previewSizePrefKeyId: Int, lensFacing: Int
    ) {
        val pref = findPreference(getString(previewSizePrefKeyId)) as ListPreference
        val cameraCharacteristics = getCameraCharacteristics(
            activity, lensFacing
        )
        val entries: Array<String?>
        if (cameraCharacteristics != null) {
            val map =
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes = map!!.getOutputSizes(
                SurfaceTexture::class.java
            )
            entries = arrayOfNulls(outputSizes.size)
            for (i in outputSizes.indices) {
                entries[i] = outputSizes[i].toString()
            }
        } else {
            entries = arrayOf(
                "2000x2000",
                "1600x1600",
                "1200x1200",
                "1000x1000",
                "800x800",
                "600x600",
                "400x400",
                "200x200",
                "100x100"
            )
        }
        pref.entries = entries
        pref.entryValues = entries
        pref.summary = if (pref.entry == null) "Default" else pref.entry
        pref.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                val newStringValue = newValue as String?
                pref.summary = newStringValue
                PreferenceUtils.saveString(activity, previewSizePrefKeyId, newStringValue)
                true
            }
    }

    companion object {
        fun getCameraCharacteristics(
            context: Context, lensFacing: Int
        ): CameraCharacteristics? {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                val cameraList = Arrays.asList(*cameraManager.cameraIdList)
                for (availableCameraId in cameraList) {
                    val availableCameraCharacteristics = cameraManager.getCameraCharacteristics(
                        availableCameraId!!
                    )
                    val availableLensFacing =
                        availableCameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                            ?: continue
                    if (availableLensFacing == lensFacing) {
                        return availableCameraCharacteristics
                    }
                }
            } catch (e: CameraAccessException) {
                // Accessing camera ID info got error
            }
            return null
        }
    }
}