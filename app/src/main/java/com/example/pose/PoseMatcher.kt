package com.example.pose

import com.example.model.BodyPartFeedback
import com.example.model.MatchStatus
import com.example.model.PoseLandmarkPoint
import com.example.model.PoseMatchResult
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class PoseMatcher {

    private var smoothedScore: Float = 0f
    private var lastGuidanceTime: Long = 0L
    private var cachedSuggestion: String = "Align with reference pose"

    fun reset() {
        smoothedScore = 0f
        lastGuidanceTime = 0L
        cachedSuggestion = "Align with reference pose"
    }

    /**
     * Compare live detected landmarks with reference template landmarks.
     */
    fun match(
        detected: Map<Int, PoseLandmarkPoint>?,
        reference: Map<Int, PoseLandmarkPoint>,
        isFullBodyRequired: Boolean = true
    ): PoseMatchResult {
        if (detected == null || detected.isEmpty()) {
            return PoseMatchResult(
                overallScore = 0,
                isMatched = false,
                bodyPartGuidance = listOf(
                    BodyPartFeedback("Full Body", MatchStatus.MISSING, "Move into camera frame")
                ),
                primarySuggestion = "Move into the camera frame",
                detectedLandmarkCount = 0
            )
        }

        if (detected.size < 6) {
            return PoseMatchResult(
                overallScore = 15,
                isMatched = false,
                bodyPartGuidance = listOf(
                    BodyPartFeedback("Lighting", MatchStatus.ADJUST, "Low visibility")
                ),
                primarySuggestion = "Move into better lighting / step back",
                detectedLandmarkCount = detected.size
            )
        }

        // Normalize both pose coordinate sets relative to their torso center & scale
        val normDetected = normalizePose(detected)
        val normReference = normalizePose(reference)

        // Joint angle evaluations
        val angleComparisons = mutableListOf<Float>()
        val feedbackList = mutableListOf<BodyPartFeedback>()
        val suggestions = mutableListOf<String>()

        // 1. Evaluate Head
        val noseRef = normReference[PoseLandmarkConstants.NOSE]
        val noseDet = normDetected[PoseLandmarkConstants.NOSE]
        if (noseRef != null && noseDet != null) {
            val dist = landmarkDistance(noseDet, noseRef)
            if (dist < 0.25f) {
                feedbackList.add(BodyPartFeedback("Head", MatchStatus.MATCHED, "Well aligned"))
            } else {
                feedbackList.add(BodyPartFeedback("Head", MatchStatus.ADJUST, "Adjust head position"))
                if (noseDet.y > noseRef.y + 0.12f) {
                    suggestions.add("Lift chin slightly")
                } else if (noseDet.y < noseRef.y - 0.12f) {
                    suggestions.add("Lower chin slightly")
                }
            }
        } else {
            feedbackList.add(BodyPartFeedback("Head", MatchStatus.MISSING, "Face camera"))
        }

        // 2. Evaluate Shoulders
        val leftShRef = normReference[PoseLandmarkConstants.LEFT_SHOULDER]
        val rightShRef = normReference[PoseLandmarkConstants.RIGHT_SHOULDER]
        val leftShDet = normDetected[PoseLandmarkConstants.LEFT_SHOULDER]
        val rightShDet = normDetected[PoseLandmarkConstants.RIGHT_SHOULDER]

        if (leftShRef != null && rightShRef != null && leftShDet != null && rightShDet != null) {
            val shAngleRef = calculateLineAngle(leftShRef, rightShRef)
            val shAngleDet = calculateLineAngle(leftShDet, rightShDet)
            val angleDiff = abs(shAngleDet - shAngleRef)
            val shDist = (landmarkDistance(leftShDet, leftShRef) + landmarkDistance(rightShDet, rightShRef)) / 2f

            if (angleDiff < 12f && shDist < 0.25f) {
                feedbackList.add(BodyPartFeedback("Shoulders", MatchStatus.MATCHED, "Level and ready"))
            } else {
                feedbackList.add(BodyPartFeedback("Shoulders", MatchStatus.ADJUST, "Square your shoulders"))
                if (shAngleDet > shAngleRef + 10f) {
                    suggestions.add("Level your left shoulder")
                } else if (shAngleDet < shAngleRef - 10f) {
                    suggestions.add("Level your right shoulder")
                }
            }
        }

        // 3. Evaluate Left Arm
        val leftElbowRef = normReference[PoseLandmarkConstants.LEFT_ELBOW]
        val leftWristRef = normReference[PoseLandmarkConstants.LEFT_WRIST]
        val leftElbowDet = normDetected[PoseLandmarkConstants.LEFT_ELBOW]
        val leftWristDet = normDetected[PoseLandmarkConstants.LEFT_WRIST]

        if (leftShRef != null && leftElbowRef != null && leftShDet != null && leftElbowDet != null) {
            val angleRef = calculateJointAngle(leftShRef, leftElbowRef, leftWristRef ?: leftElbowRef)
            val angleDet = calculateJointAngle(leftShDet, leftElbowDet, leftWristDet ?: leftElbowDet)
            val angleDiff = abs(angleDet - angleRef)
            val armScore = max(0f, 100f - angleDiff * 1.2f)
            angleComparisons.add(armScore)

            val wristYDet = leftWristDet?.y ?: leftElbowDet.y
            val wristYRef = leftWristRef?.y ?: leftElbowRef.y

            if (angleDiff < 22f && abs(wristYDet - wristYRef) < 0.25f) {
                feedbackList.add(BodyPartFeedback("Left Arm", MatchStatus.MATCHED, "Perfect angle"))
            } else {
                feedbackList.add(BodyPartFeedback("Left Arm", MatchStatus.ADJUST, "Adjust arm"))
                if (wristYDet > wristYRef + 0.20f) {
                    suggestions.add("Raise your left arm")
                } else if (wristYDet < wristYRef - 0.20f) {
                    suggestions.add("Lower your left arm")
                } else if (angleDet < angleRef - 25f) {
                    suggestions.add("Straighten your left arm")
                } else if (angleDet > angleRef + 25f) {
                    suggestions.add("Bend your left arm")
                }
            }
        } else {
            feedbackList.add(BodyPartFeedback("Left Arm", MatchStatus.MISSING, "Show left arm in frame"))
        }

        // 4. Evaluate Right Arm
        val rightElbowRef = normReference[PoseLandmarkConstants.RIGHT_ELBOW]
        val rightWristRef = normReference[PoseLandmarkConstants.RIGHT_WRIST]
        val rightElbowDet = normDetected[PoseLandmarkConstants.RIGHT_ELBOW]
        val rightWristDet = normDetected[PoseLandmarkConstants.RIGHT_WRIST]

        if (rightShRef != null && rightElbowRef != null && rightShDet != null && rightElbowDet != null) {
            val angleRef = calculateJointAngle(rightShRef, rightElbowRef, rightWristRef ?: rightElbowRef)
            val angleDet = calculateJointAngle(rightShDet, rightElbowDet, rightWristDet ?: rightElbowDet)
            val angleDiff = abs(angleDet - angleRef)
            val armScore = max(0f, 100f - angleDiff * 1.2f)
            angleComparisons.add(armScore)

            val wristYDet = rightWristDet?.y ?: rightElbowDet.y
            val wristYRef = rightWristRef?.y ?: rightElbowRef.y

            if (angleDiff < 22f && abs(wristYDet - wristYRef) < 0.25f) {
                feedbackList.add(BodyPartFeedback("Right Arm", MatchStatus.MATCHED, "Great positioning"))
            } else {
                feedbackList.add(BodyPartFeedback("Right Arm", MatchStatus.ADJUST, "Adjust arm"))
                if (wristYDet > wristYRef + 0.20f) {
                    suggestions.add("Raise your right arm")
                } else if (wristYDet < wristYRef - 0.20f) {
                    suggestions.add("Lower your right arm")
                } else if (angleDet < angleRef - 25f) {
                    suggestions.add("Straighten your right arm")
                } else if (angleDet > angleRef + 25f) {
                    suggestions.add("Bend your right arm")
                }
            }
        } else {
            feedbackList.add(BodyPartFeedback("Right Arm", MatchStatus.MISSING, "Show right arm in frame"))
        }

        // 5. Evaluate Hips / Torso
        val leftHipRef = normReference[PoseLandmarkConstants.LEFT_HIP]
        val rightHipRef = normReference[PoseLandmarkConstants.RIGHT_HIP]
        val leftHipDet = normDetected[PoseLandmarkConstants.LEFT_HIP]
        val rightHipDet = normDetected[PoseLandmarkConstants.RIGHT_HIP]

        if (leftHipRef != null && rightHipRef != null && leftHipDet != null && rightHipDet != null) {
            val hipDist = (landmarkDistance(leftHipDet, leftHipRef) + landmarkDistance(rightHipDet, rightHipRef)) / 2f
            if (hipDist < 0.25f) {
                feedbackList.add(BodyPartFeedback("Hips", MatchStatus.MATCHED, "Centered"))
            } else {
                feedbackList.add(BodyPartFeedback("Hips", MatchStatus.ADJUST, "Shift hips"))
                if (leftHipDet.x > leftHipRef.x + 0.15f) {
                    suggestions.add("Shift torso left")
                } else if (leftHipDet.x < leftHipRef.x - 0.15f) {
                    suggestions.add("Shift torso right")
                }
            }
        }

        // 6. Evaluate Legs (if full-body pose)
        if (isFullBodyRequired) {
            val leftKneeRef = normReference[PoseLandmarkConstants.LEFT_KNEE]
            val leftKneeDet = normDetected[PoseLandmarkConstants.LEFT_KNEE]
            val rightKneeRef = normReference[PoseLandmarkConstants.RIGHT_KNEE]
            val rightKneeDet = normDetected[PoseLandmarkConstants.RIGHT_KNEE]

            if (leftKneeRef != null && leftKneeDet != null) {
                val dist = landmarkDistance(leftKneeDet, leftKneeRef)
                if (dist < 0.28f) {
                    feedbackList.add(BodyPartFeedback("Left Leg", MatchStatus.MATCHED, "Good stance"))
                } else {
                    feedbackList.add(BodyPartFeedback("Left Leg", MatchStatus.ADJUST, "Adjust leg position"))
                    if (leftKneeDet.x > leftKneeRef.x + 0.15f) {
                        suggestions.add("Move left foot outward")
                    }
                }
            } else if (leftKneeRef != null) {
                suggestions.add("Step back to show legs")
                feedbackList.add(BodyPartFeedback("Left Leg", MatchStatus.MISSING, "Step back for legs"))
            }

            if (rightKneeRef != null && rightKneeDet != null) {
                val dist = landmarkDistance(rightKneeDet, rightKneeRef)
                if (dist < 0.28f) {
                    feedbackList.add(BodyPartFeedback("Right Leg", MatchStatus.MATCHED, "Good stance"))
                } else {
                    feedbackList.add(BodyPartFeedback("Right Leg", MatchStatus.ADJUST, "Adjust leg position"))
                    if (rightKneeDet.x < rightKneeRef.x - 0.15f) {
                        suggestions.add("Move right foot outward")
                    }
                }
            } else if (rightKneeRef != null) {
                feedbackList.add(BodyPartFeedback("Right Leg", MatchStatus.MISSING, "Step back for legs"))
            }
        }

        // Compute overall distance match score across all common landmarks
        var totalDistScore = 0f
        var commonCount = 0

        for ((id, refPoint) in normReference) {
            val detPoint = normDetected[id]
            if (detPoint != null) {
                val d = landmarkDistance(detPoint, refPoint)
                val ptScore = max(0f, 100f - d * 60f)
                totalDistScore += ptScore
                commonCount++
            }
        }

        val baseDistScore = if (commonCount > 0) totalDistScore / commonCount else 0f
        val baseAngleScore = if (angleComparisons.isNotEmpty()) angleComparisons.average().toFloat() else baseDistScore

        val rawScore = (baseDistScore * 0.55f + baseAngleScore * 0.45f).coerceIn(0f, 100f)

        // Exponential smoothing to prevent erratic jitter
        smoothedScore = if (smoothedScore == 0f) rawScore else (smoothedScore * 0.65f + rawScore * 0.35f)
        val finalScore = smoothedScore.toInt().coerceIn(0, 100)

        // Debounce primary suggestion for stability
        val now = System.currentTimeMillis()
        if (now - lastGuidanceTime > 400 || cachedSuggestion == "Align with reference pose") {
            cachedSuggestion = when {
                finalScore >= 80 -> "Hold steady! Pose locked in 🔥"
                finalScore >= 65 -> suggestions.firstOrNull() ?: "Almost there, fine-tune angle"
                suggestions.isNotEmpty() -> suggestions.first()
                else -> "Align body with glowing skeleton"
            }
            lastGuidanceTime = now
        }

        return PoseMatchResult(
            overallScore = finalScore,
            isMatched = finalScore >= 75,
            bodyPartGuidance = feedbackList,
            primarySuggestion = cachedSuggestion,
            detectedLandmarkCount = detected.size
        )
    }

    /**
     * Normalizes landmarks: centers around midpoint between hips/shoulders and scales by torso length.
     */
    fun normalizePose(landmarks: Map<Int, PoseLandmarkPoint>): Map<Int, PoseLandmarkPoint> {
        if (landmarks.isEmpty()) return emptyMap()

        val leftSh = landmarks[PoseLandmarkConstants.LEFT_SHOULDER]
        val rightSh = landmarks[PoseLandmarkConstants.RIGHT_SHOULDER]
        val leftHip = landmarks[PoseLandmarkConstants.LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkConstants.RIGHT_HIP]

        // Center calculation
        val centerX: Float
        val centerY: Float

        if (leftSh != null && rightSh != null && leftHip != null && rightHip != null) {
            centerX = (leftSh.x + rightSh.x + leftHip.x + rightHip.x) / 4f
            centerY = (leftSh.y + rightSh.y + leftHip.y + rightHip.y) / 4f
        } else if (leftSh != null && rightSh != null) {
            centerX = (leftSh.x + rightSh.x) / 2f
            centerY = (leftSh.y + rightSh.y) / 2f
        } else {
            centerX = landmarks.values.map { it.x }.average().toFloat()
            centerY = landmarks.values.map { it.y }.average().toFloat()
        }

        // Scale calculation (torso height or shoulder span)
        val scale: Float
        if (leftSh != null && rightSh != null && leftHip != null && rightHip != null) {
            val shoulderMidY = (leftSh.y + rightSh.y) / 2f
            val hipMidY = (leftHip.y + rightHip.y) / 2f
            val torsoH = abs(hipMidY - shoulderMidY)
            val shoulderW = landmarkDistance(leftSh, rightSh)
            scale = max(0.08f, max(torsoH, shoulderW))
        } else if (leftSh != null && rightSh != null) {
            scale = max(0.08f, landmarkDistance(leftSh, rightSh))
        } else {
            scale = 0.35f
        }

        val normalized = mutableMapOf<Int, PoseLandmarkPoint>()
        for ((k, v) in landmarks) {
            normalized[k] = PoseLandmarkPoint(
                x = (v.x - centerX) / scale,
                y = (v.y - centerY) / scale,
                z = v.z / scale,
                likelihood = v.likelihood
            )
        }
        return normalized
    }

    /**
     * Euclidean distance between two landmark points.
     */
    fun landmarkDistance(a: PoseLandmarkPoint, b: PoseLandmarkPoint): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Calculates joint angle (ABC with vertex B) in degrees (0..180).
     */
    fun calculateJointAngle(a: PoseLandmarkPoint, b: PoseLandmarkPoint, c: PoseLandmarkPoint): Float {
        val v1x = a.x - b.x
        val v1y = a.y - b.y
        val v2x = c.x - b.x
        val v2y = c.y - b.y

        val mag1 = sqrt(v1x * v1x + v1y * v1y)
        val mag2 = sqrt(v2x * v2x + v2y * v2y)

        if (mag1 < 1e-4f || mag2 < 1e-4f) return 180f

        val dot = (v1x * v2x + v1y * v2y) / (mag1 * mag2)
        val clampedDot = dot.coerceIn(-1f, 1f)
        return Math.toDegrees(acos(clampedDot.toDouble())).toFloat()
    }

    /**
     * Calculates inclination angle of a 2D line segment in degrees.
     */
    private fun calculateLineAngle(a: PoseLandmarkPoint, b: PoseLandmarkPoint): Float {
        val dy = (b.y - a.y).toDouble()
        val dx = (b.x - a.x).toDouble()
        return Math.toDegrees(atan2(dy, dx)).toFloat()
    }
}
