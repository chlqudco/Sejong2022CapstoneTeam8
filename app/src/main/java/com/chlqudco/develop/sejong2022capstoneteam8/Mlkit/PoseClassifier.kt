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

import android.util.Pair
import kotlin.jvm.JvmOverloads
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import java.util.*

/**
 * Classifies {link Pose} based on given [PoseSample]s.
 *
 *
 * Inspired by K-Nearest Neighbors Algorithm with outlier filtering.
 * https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm
 */
class PoseClassifier @JvmOverloads constructor(
    private val poseSamples: List<PoseSample>,
    private val maxDistanceTopK: Int = MAX_DISTANCE_TOP_K,
    private val meanDistanceTopK: Int = MEAN_DISTANCE_TOP_K,
    private val axesWeights: PointF3D = AXES_WEIGHTS
) {
    /**
     * Returns the max range of confidence values.
     *
     *
     * <Since we calculate confidence by counting></Since>[PoseSample]s that survived
     * outlier-filtering by maxDistanceTopK and meanDistanceTopK, this range is the minimum of two.
     */
    fun confidenceRange(): Int {
        return Math.min(maxDistanceTopK, meanDistanceTopK)
    }

    fun classify(pose: Pose): ClassificationResult {
        return classify(extractPoseLandmarks(pose))
    }

    fun classify(landmarks: List<PointF3D?>): ClassificationResult {
        val result = ClassificationResult()
        // Return early if no landmarks detected.
        if (landmarks.isEmpty()) {
            return result
        }

        // We do flipping on X-axis so we are horizontal (mirror) invariant.
        val flippedLandmarks: List<PointF3D?> = ArrayList(landmarks)
        Utils.multiplyAll(flippedLandmarks, PointF3D.from(-1f, 1f, 1f))
        val embedding = PoseEmbedding.getPoseEmbedding(landmarks)
        val flippedEmbedding = PoseEmbedding.getPoseEmbedding(flippedLandmarks)
        val maxDistances = PriorityQueue(
            maxDistanceTopK
        ) { o1: Pair<PoseSample, Float?>, o2: Pair<PoseSample, Float?> ->
            -java.lang.Float.compare(
                o1.second!!, o2.second!!
            )
        }
        for (poseSample in poseSamples) {
            val sampleEmbedding = poseSample.embedding
            var originalMax = 0f
            var flippedMax = 0f
            for (i in embedding.indices) {
                originalMax = Math.max(
                    originalMax, Utils.maxAbs(
                        Utils.multiply(
                            Utils.subtract(
                                embedding[i], sampleEmbedding[i]
                            ), axesWeights
                        )
                    )
                )
                flippedMax = Math.max(
                    flippedMax, Utils.maxAbs(
                        Utils.multiply(
                            Utils.subtract(
                                flippedEmbedding[i], sampleEmbedding[i]
                            ), axesWeights
                        )
                    )
                )
            }
            // Set the max distance as min of original and flipped max distance.
            maxDistances.add(Pair(poseSample, Math.min(originalMax, flippedMax)))
            // We only want to retain top n so pop the highest distance.
            if (maxDistances.size > maxDistanceTopK) {
                maxDistances.poll()
            }
        }

        // Keeps higher mean distances on top so we can pop it when top_k size is reached.
        val meanDistances = PriorityQueue(
            meanDistanceTopK
        ) { o1: Pair<PoseSample, Float?>, o2: Pair<PoseSample, Float?> ->
            -java.lang.Float.compare(
                o1.second!!, o2.second!!
            )
        }
        // Retrive top K poseSamples by least mean distance to remove outliers.
        for (sampleDistances in maxDistances) {
            val poseSample = sampleDistances.first
            val sampleEmbedding = poseSample.embedding
            var originalSum = 0f
            var flippedSum = 0f
            for (i in embedding.indices) {
                originalSum += Utils.sumAbs(
                    Utils.multiply(
                        Utils.subtract(
                            embedding[i], sampleEmbedding[i]
                        ), axesWeights
                    )
                )
                flippedSum += Utils.sumAbs(
                    Utils.multiply(
                        Utils.subtract(
                            flippedEmbedding[i], sampleEmbedding[i]
                        ), axesWeights
                    )
                )
            }
            // Set the mean distance as min of original and flipped mean distances.
            val meanDistance = Math.min(originalSum, flippedSum) / (embedding.size * 2)
            meanDistances.add(Pair(poseSample, meanDistance))
            // We only want to retain top k so pop the highest mean distance.
            if (meanDistances.size > meanDistanceTopK) {
                meanDistances.poll()
            }
        }
        for (sampleDistances in meanDistances) {
            val className = sampleDistances.first.className

            //테스트
            result.incrementClassConfidence(className)
        }
        return result
    }

    companion object {
        private const val TAG = "PoseClassifier"
        private const val MAX_DISTANCE_TOP_K = 30
        private const val MEAN_DISTANCE_TOP_K = 10

        // Note Z has a lower weight as it is generally less accurate than X & Y.
        private val AXES_WEIGHTS = PointF3D.from(1f, 1f, 0.2f)
        private fun extractPoseLandmarks(pose: Pose): List<PointF3D?> {
            val landmarks: MutableList<PointF3D?> = ArrayList()
            for (poseLandmark in pose.allPoseLandmarks) {
                landmarks.add(poseLandmark.position3D)
            }
            return landmarks
        }
    }
}