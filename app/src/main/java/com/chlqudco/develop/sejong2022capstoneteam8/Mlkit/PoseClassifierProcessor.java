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

package com.chlqudco.develop.sejong2022capstoneteam8.Mlkit;

import static android.content.Context.MODE_PRIVATE;

import static com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.FITNESS_CHOICE;
import static com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.FITNESS_COUNT;
import static com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.FITNESS_CURRENT_SET;
import static com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.FITNESS_SET;
import static com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.SETTING;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.Pose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Accepts a stream of {@link Pose} for classification and Rep counting.
 */
public class PoseClassifierProcessor {

  private static final String TAG = "PoseClassifierProcessor";
  private static final String POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv";

  // Specify classes for which we want rep counting.
  // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
  // for your pose samples.
  private static final String PUSHUPS_CLASS = "pushups_down";
  private static final String SQUATS_CLASS = "squats_down";
  private static final String[] POSE_CLASSES = {PUSHUPS_CLASS, SQUATS_CLASS};

  private final boolean isStreamMode;

  private EMASmoothing emaSmoothing;
  private List<RepetitionCounter> repCounters;
  private PoseClassifier poseClassifier;
  private String lastRepResult;

  //sp 연습
  private SharedPreferences preferences;
  private String fitnessType="";
  private int targetCount;
  private int targetSet;
  private int currentSet;
  private Context mContext;

  @WorkerThread
  public PoseClassifierProcessor(Context context, boolean isStreamMode) {
    mContext = context;
    preferences = context.getSharedPreferences(SETTING, MODE_PRIVATE);

    //여기서 관리해보자
    fitnessType = preferences.getString(FITNESS_CHOICE,"");
    targetCount = preferences.getInt(FITNESS_COUNT,0);
    targetSet = preferences.getInt(FITNESS_SET,0);
    currentSet = preferences.getInt(FITNESS_CURRENT_SET,0);

    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    this.isStreamMode = isStreamMode;
    if (isStreamMode) {
      emaSmoothing = new EMASmoothing();
      repCounters = new ArrayList<>();
      lastRepResult = "";
    }
    loadPoseSamples(context);
  }

  private void loadPoseSamples(Context context) {
    List<PoseSample> poseSamples = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(POSE_SAMPLES_FILE)));
      String csvLine = reader.readLine();
      while (csvLine != null) {
        // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if (poseSample != null) {
          poseSamples.add(poseSample);
        }
        csvLine = reader.readLine();
      }
    } catch (IOException e) {
      Log.e(TAG, "Error when loading pose samples.\n" + e);
    }
    poseClassifier = new PoseClassifier(poseSamples);
    if (isStreamMode) {
      for (String className : POSE_CLASSES) {
        repCounters.add(new RepetitionCounter(className));
      }
    }
  }

  @WorkerThread
  public List<String> getPoseResult(Pose pose) {

    //여기서 관리해보자
    fitnessType = preferences.getString(FITNESS_CHOICE,"");
    targetCount = preferences.getInt(FITNESS_COUNT,3);
    targetSet = preferences.getInt(FITNESS_SET,0);
    currentSet = preferences.getInt(FITNESS_CURRENT_SET,0);

    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    List<String> result = new ArrayList<>();
    ClassificationResult classification = poseClassifier.classify(pose);

    // Update {@link RepetitionCounter}s if {@code isStreamMode}.
    if (isStreamMode) {
      // Feed pose to smoothing even if no pose found.
      classification = emaSmoothing.getSmoothedResult(classification);

      // Return early without updating repCounter if no pose found.
      if (pose.getAllPoseLandmarks().isEmpty()) {
        result.add(lastRepResult);
        return result;
      }

      Log.e("jang",""+targetCount);

      for (RepetitionCounter repCounter : repCounters) {
        int repsBefore = repCounter.getNumRepeats();
        int repsAfter = repCounter.addClassificationResult(classification);
        if (repsAfter > repsBefore && (fitnessType.equals(repCounter.getClassName()))) {
          // Play a fun beep when rep counter updates.
          ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
          tg.startTone(ToneGenerator.TONE_PROP_BEEP);
          lastRepResult = String.format(Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter);


          //개수가 다 끝난 경우
          if(repsAfter == targetCount){
            //세트도 다 끝난 경우
            if(targetSet == currentSet + 1){
              ((CameraXLivePreviewActivity)mContext).AllEnd();
            }
            else {
              ((CameraXLivePreviewActivity)mContext).setEnd();
            }
            return new ArrayList<String>();
          }

          //여기서 음성 출력함수를 호출해야 하려나


          break;
        }
      }
      result.add(lastRepResult);
    }

    // Add maxConfidence class of current frame to result if pose is found.
    if (!pose.getAllPoseLandmarks().isEmpty()) {
      String maxConfidenceClass = classification.getMaxConfidenceClass();
      String maxConfidenceClassResult = String.format(Locale.US, "%s : %.2f confidence", maxConfidenceClass, classification.getClassConfidence(maxConfidenceClass) / poseClassifier.confidenceRange());
      result.add(maxConfidenceClassResult);
    }

    return result;
  }

}
