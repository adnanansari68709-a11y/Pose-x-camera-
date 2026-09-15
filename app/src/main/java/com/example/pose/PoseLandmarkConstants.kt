package com.example.pose

object PoseLandmarkConstants {
    const val NOSE = 0
    const val LEFT_EYE_INNER = 1
    const val LEFT_EYE = 2
    const val LEFT_EYE_OUTER = 3
    const val RIGHT_EYE_INNER = 4
    const val RIGHT_EYE = 5
    const val RIGHT_EYE_OUTER = 6
    const val LEFT_EAR = 7
    const val RIGHT_EAR = 8
    const val MOUTH_LEFT = 9
    const val MOUTH_RIGHT = 10
    const val LEFT_SHOULDER = 11
    const val RIGHT_SHOULDER = 12
    const val LEFT_ELBOW = 13
    const val RIGHT_ELBOW = 14
    const val LEFT_WRIST = 15
    const val RIGHT_WRIST = 16
    const val LEFT_PINKY = 17
    const val RIGHT_PINKY = 18
    const val LEFT_INDEX = 19
    const val RIGHT_INDEX = 20
    const val LEFT_THUMB = 21
    const val RIGHT_THUMB = 22
    const val LEFT_HIP = 23
    const val RIGHT_HIP = 24
    const val LEFT_KNEE = 25
    const val RIGHT_KNEE = 26
    const val LEFT_ANKLE = 27
    const val RIGHT_ANKLE = 28
    const val LEFT_HEEL = 29
    const val RIGHT_HEEL = 30
    const val LEFT_FOOT_INDEX = 31
    const val RIGHT_FOOT_INDEX = 32

    /**
     * Standard human skeleton bone pairs (startLandmark to endLandmark).
     */
    val SKELETON_CONNECTIONS = listOf(
        // Head / Face
        Pair(LEFT_EAR, LEFT_EYE),
        Pair(LEFT_EYE, NOSE),
        Pair(NOSE, RIGHT_EYE),
        Pair(RIGHT_EYE, RIGHT_EAR),

        // Shoulders and Neck
        Pair(LEFT_SHOULDER, RIGHT_SHOULDER),
        Pair(NOSE, LEFT_SHOULDER),
        Pair(NOSE, RIGHT_SHOULDER),

        // Upper Body & Arms
        Pair(LEFT_SHOULDER, LEFT_ELBOW),
        Pair(LEFT_ELBOW, LEFT_WRIST),
        Pair(LEFT_WRIST, LEFT_INDEX),

        Pair(RIGHT_SHOULDER, RIGHT_ELBOW),
        Pair(RIGHT_ELBOW, RIGHT_WRIST),
        Pair(RIGHT_WRIST, RIGHT_INDEX),

        // Torso
        Pair(LEFT_SHOULDER, LEFT_HIP),
        Pair(RIGHT_SHOULDER, RIGHT_HIP),
        Pair(LEFT_HIP, RIGHT_HIP),

        // Lower Body & Legs
        Pair(LEFT_HIP, LEFT_KNEE),
        Pair(LEFT_KNEE, LEFT_ANKLE),
        Pair(LEFT_ANKLE, LEFT_FOOT_INDEX),

        Pair(RIGHT_HIP, RIGHT_KNEE),
        Pair(RIGHT_KNEE, RIGHT_ANKLE),
        Pair(RIGHT_ANKLE, RIGHT_FOOT_INDEX)
    )
}
