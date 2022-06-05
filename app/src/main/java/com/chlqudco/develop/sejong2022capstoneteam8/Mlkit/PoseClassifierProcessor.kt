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
import android.content.SharedPreferences
import com.google.mlkit.vision.pose.Pose
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey
import android.os.Looper
import android.media.ToneGenerator
import android.media.AudioManager
import android.util.Log
import androidx.annotation.WorkerThread
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.LUNGE
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.LUNGE_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.LUNGE_UP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PULLUP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PULLUP_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PULLUP_UP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PUSHUP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PUSHUP_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PUSHUP_UP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SQUAT
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SQUAT_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SQUAT_UP
import com.google.common.base.Preconditions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * Accepts a stream of [Pose] for classification and Rep counting.
 */
class PoseClassifierProcessor @WorkerThread constructor(
    private val mContext: Context,
    isStreamMode: Boolean
) {
    private val isStreamMode: Boolean
    private var emaSmoothing: EMASmoothing? = null
    private var repCounters: MutableList<RepetitionCounter>? = null
    private var poseClassifier: PoseClassifier? = null
    private var lastRepResult: String? = null

    //sp 연습
    private val preferences: SharedPreferences
    private var fitnessType: String? = ""
    private var targetCount: Int
    private var targetSet: Int
    private var currentSet: Int

    private fun loadPoseSamples(context: Context) {
        val poseSamples: MutableList<PoseSample> = ArrayList()
        try {
            val reader = BufferedReader(InputStreamReader(context.assets.open(POSE_SAMPLES_FILE)))
            var csvLine = reader.readLine()
            while (csvLine != null) {
                // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
                val poseSample = PoseSample.getPoseSample(csvLine, ",")
                if (poseSample != null) {
                    poseSamples.add(poseSample)
                }
                csvLine = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error when loading pose samples.\n$e")
        }
        poseClassifier = PoseClassifier(poseSamples)
        if (isStreamMode) {
            for (className in POSE_CLASSES) {
                repCounters!!.add(RepetitionCounter(className))
            }
        }
    }

    @WorkerThread
    fun getPoseResult(pose: Pose): List<String?> {

        //여기서 관리해보자
        fitnessType = preferences.getString(SharedPreferenceKey.FITNESS_CHOICE, "")
        targetCount = preferences.getInt(SharedPreferenceKey.FITNESS_COUNT, 3)
        targetSet = preferences.getInt(SharedPreferenceKey.FITNESS_SET, 0)
        currentSet = preferences.getInt(SharedPreferenceKey.FITNESS_CURRENT_SET, 0)
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String?> = ArrayList()
        var classification = poseClassifier!!.classify(pose)

        // Update {@link RepetitionCounter}s if {@code isStreamMode}.
        if (isStreamMode) {
            // Feed pose to smoothing even if no pose found.
            classification = emaSmoothing!!.getSmoothedResult(classification)

            // Return early without updating repCounter if no pose found.
            if (pose.allPoseLandmarks.isEmpty()) {
                result.add(lastRepResult)
                return result
            }

            for (repCounter in repCounters!!) {
                val repsBefore = repCounter.numRepeats
                val repsAfter = repCounter.addClassificationResult(classification)
                if (repsAfter > repsBefore && fitnessType == repCounter.className) {
                    // Play a fun beep when rep counter updates.
                    val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP)
                    lastRepResult = String.format(Locale.US, "현재 횟수 : %d 회", repsAfter)

                    //일단 음성먼저 출력
                    (mContext as CameraXLivePreviewActivity).playSound(repsAfter)

                    //개수가 다 끝난 경우
                    if (repsAfter == targetCount) {
                        //세트도 다 끝난 경우
                        if (targetSet == currentSet + 1) {
                            (mContext as CameraXLivePreviewActivity).allEnd()
                        } else {
                            (mContext as CameraXLivePreviewActivity).setEnd()
                        }
                        return ArrayList()
                    }

                    break
                }
            }
            result.add(lastRepResult)
        }

        // Add maxConfidence class of current frame to result if pose is found.
        if (!pose.allPoseLandmarks.isEmpty()) {
            //여기서 같은 운동일 때만 보여줘야함

            //예측한 운동 이름
            val maxConfidenceClass = classification.maxConfidenceClass

            if (isMatchedFitnessType(maxConfidenceClass)){
                val percentNum = "${((classification.getClassConfidence(maxConfidenceClass) / poseClassifier!!.confidenceRange())*100).toInt()} % 일치"
                val maxConfidenceClassResult = String.format(
                    "%s : %s",
                    getFitnessString(maxConfidenceClass),
                    percentNum
                )
                result.add(maxConfidenceClassResult)
            }

        }
        return result
    }

    //내가 선택한 운동이 맞는지
    private fun isMatchedFitnessType(name : String): Boolean {
        if (name== PUSHUP_DOWN || name == PUSHUP_UP){
            if(fitnessType == PUSHUP_DOWN) return true
        }
        if (name==SQUAT_DOWN || name == SQUAT_UP){
            if (fitnessType == SQUAT_DOWN) return true
        }
        if (name==PULLUP_DOWN || name == PULLUP_UP){
            if (fitnessType == PULLUP_DOWN) return true
        }
        if (name== LUNGE_DOWN || name == LUNGE_UP){
            if (fitnessType == LUNGE_DOWN) return true
        }
        return false
    }

    private fun getFitnessString(fitnessName: String): String {
        if (fitnessName == PUSHUP_DOWN) return "팔굽혀펴기 내려감"
        if (fitnessName == PUSHUP_UP) return "팔굽혀펴기 올라옴"

        if (fitnessName == SQUAT_DOWN) return "스쿼트 내려감"
        if (fitnessName == SQUAT_UP) return "스쿼트 올라옴"

        if (fitnessName == LUNGE_DOWN) return "런지 내려감"
        if (fitnessName == LUNGE_UP) return "런지 올라옴"

        if (fitnessName == PULLUP_DOWN) return "턱걸이 내려감"
        if (fitnessName == PULLUP_UP) return "턱걸이 올라옴"
        return ""
    }

    companion object {
        private const val TAG = "PoseClassifierProcessor"
        private const val POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv"

        // Specify classes for which we want rep counting.
        // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
        // for your pose samples.
        private const val PUSHUPS_CLASS = "pushups_down"
        private const val SQUATS_CLASS = "squats_down"
        private const val PULLUPS_CLASS = "pullup_down"
        private const val LUNGES_CLASS = "lunge_down"
        private val POSE_CLASSES = arrayOf(PUSHUPS_CLASS, SQUATS_CLASS, PULLUPS_CLASS, LUNGES_CLASS)
    }

    init {
        preferences = mContext.getSharedPreferences(SharedPreferenceKey.SETTING, Context.MODE_PRIVATE)

        //여기서 관리해보자
        fitnessType = preferences.getString(SharedPreferenceKey.FITNESS_CHOICE, "")
        targetCount = preferences.getInt(SharedPreferenceKey.FITNESS_COUNT, 0)
        targetSet = preferences.getInt(SharedPreferenceKey.FITNESS_SET, 0)
        currentSet = preferences.getInt(SharedPreferenceKey.FITNESS_CURRENT_SET, 0)
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        this.isStreamMode = isStreamMode
        if (isStreamMode) {
            emaSmoothing = EMASmoothing()
            repCounters = ArrayList()
            lastRepResult = ""
        }
        loadPoseSamples(mContext)
    }
}