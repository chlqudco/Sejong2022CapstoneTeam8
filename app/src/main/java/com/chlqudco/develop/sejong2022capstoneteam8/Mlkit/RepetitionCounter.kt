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

import kotlin.jvm.JvmOverloads
/**
 * Counts reps for the give class.
 */
class RepetitionCounter @JvmOverloads constructor(
    val className: String,
    private val enterThreshold: Float = DEFAULT_ENTER_THRESHOLD,
    private val exitThreshold: Float = DEFAULT_EXIT_THRESHOLD
) {
    var numRepeats = 0
    private var poseEntered = false

    /**
     * Adds a new Pose classification result and updates reps for given class.
     *
     * @param classificationResult {link ClassificationResult} of class to confidence values.
     * @return number of reps.
     */
    fun addClassificationResult(classificationResult: ClassificationResult): Int {
        val poseConfidence = classificationResult.getClassConfidence(className)
        if (!poseEntered) {
            poseEntered = poseConfidence > enterThreshold
            return numRepeats
        }
        if (poseConfidence < exitThreshold) {
            numRepeats++
            poseEntered = false
        }
        return numRepeats
    }

    companion object {
        private const val DEFAULT_ENTER_THRESHOLD = 8f
        private const val DEFAULT_EXIT_THRESHOLD = 6f
    }
}