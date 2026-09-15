package com.example.pose

import com.example.model.PoseLandmarkPoint
import com.example.model.PoseTemplate

object PoseLibraryData {

    val CATEGORIES = listOf(
        "All",
        "Standing",
        "Sitting",
        "Mirror",
        "Street",
        "Fashion",
        "Casual",
        "Travel",
        "Indoor",
        "Outdoor",
        "Full Body",
        "Upper Body"
    )

    fun getStarterPoses(): List<PoseTemplate> {
        return listOf(
            createBossEnergyPose(),
            createCasualLeanPose(),
            createMirrorFlexPose(),
            createFashionWalkPose(),
            createUrbanStoopPose(),
            createCoffeeTablePose(),
            createHighFashionTwistPose(),
            createTravelHorizonPose(),
            createTokyoPeacePose(),
            createCleanFitCheckPose(),
            createFloorLoungePose(),
            createSummitReachPose(),
            createEditorialAnglePose(),
            createConfidentFoldPose()
        )
    }

    private fun createBossEnergyPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.16f)
        lm[PoseLandmarkConstants.LEFT_EYE] = PoseLandmarkPoint(0.48f, 0.14f)
        lm[PoseLandmarkConstants.RIGHT_EYE] = PoseLandmarkPoint(0.52f, 0.14f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.40f, 0.26f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.60f, 0.26f)
        // Hands on hips
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.31f, 0.36f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.41f, 0.46f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.69f, 0.36f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.59f, 0.46f)
        // Hips
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.44f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.56f, 0.48f)
        // Confident grounded legs
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.42f, 0.68f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.40f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.58f, 0.68f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.60f, 0.88f)

        return PoseTemplate(
            id = "standing_boss_energy",
            title = "Boss Energy",
            category = "Standing",
            difficulty = "Easy",
            description = "Grounded full-body power stance with hands placed firmly on hips and shoulders back.",
            instructions = listOf(
                "Plant feet shoulder-width apart.",
                "Place both hands firmly on your waist/hips with elbows flared out.",
                "Roll your shoulders back and tilt chin slightly upward."
            ),
            landmarks = lm,
            tags = listOf("Full Body", "Standing", "Outdoor", "Fashion"),
            isFullBody = true
        )
    }

    private fun createCasualLeanPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.48f, 0.17f)
        lm[PoseLandmarkConstants.LEFT_EYE] = PoseLandmarkPoint(0.46f, 0.15f)
        lm[PoseLandmarkConstants.RIGHT_EYE] = PoseLandmarkPoint(0.50f, 0.15f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.39f, 0.27f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.57f, 0.28f)
        // Left hand in pocket, right arm hanging naturally
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.35f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.41f, 0.49f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.60f, 0.40f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.62f, 0.54f)
        // Slight hip tilt
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.43f, 0.49f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.55f, 0.51f)
        // One leg bent forward
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.45f, 0.69f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.44f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.57f, 0.70f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.59f, 0.87f)

        return PoseTemplate(
            id = "casual_lean",
            title = "Casual Lean",
            category = "Casual",
            difficulty = "Easy",
            description = "Relaxed street-style posture with natural weight shift and hand in pocket.",
            instructions = listOf(
                "Shift 70% of your body weight to your right leg.",
                "Slide your left hand into your pocket or hook your thumb.",
                "Relax your right arm completely along your side."
            ),
            landmarks = lm,
            tags = listOf("Casual", "Street", "Standing", "Full Body", "Outdoor"),
            isFullBody = true
        )
    }

    private fun createMirrorFlexPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        // Upper body mirror selfie
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.47f, 0.22f)
        lm[PoseLandmarkConstants.LEFT_EYE] = PoseLandmarkPoint(0.45f, 0.20f)
        lm[PoseLandmarkConstants.RIGHT_EYE] = PoseLandmarkPoint(0.49f, 0.20f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.36f, 0.32f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.58f, 0.34f)
        // Right hand holding phone at chest/eye level
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.65f, 0.44f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.52f, 0.35f)
        // Left hand casually down or on hip
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.30f, 0.44f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.38f, 0.54f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.42f, 0.58f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.56f, 0.60f)

        return PoseTemplate(
            id = "mirror_selfie_flex",
            title = "Gen-Z Mirror Flex",
            category = "Mirror",
            difficulty = "Medium",
            description = "The definitive mirror fit-check pose with phone elevated and tilted torso.",
            instructions = listOf(
                "Hold your phone up at chest/chin level angled slightly down.",
                "Drop your left shoulder slightly to create aesthetic body lines.",
                "Pop your left hip outward for visual asymmetry."
            ),
            landmarks = lm,
            tags = listOf("Mirror", "Upper Body", "Indoor", "Casual"),
            isFullBody = false
        )
    }

    private fun createFashionWalkPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.52f, 0.15f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.43f, 0.25f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.61f, 0.26f)
        // Arm swinging in stride
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.38f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.36f, 0.50f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.67f, 0.36f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.70f, 0.46f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.46f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.58f, 0.48f)
        // Wide walking stride
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.39f, 0.67f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.35f, 0.86f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.63f, 0.69f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.67f, 0.88f)

        return PoseTemplate(
            id = "fashion_walk",
            title = "The Fashion Stride",
            category = "Fashion",
            difficulty = "Pro",
            description = "High-energy runway stride captured in motion for dynamic street photography.",
            instructions = listOf(
                "Take a deliberate step forward with your leading foot.",
                "Let your arms swing naturally in opposition to your stride.",
                "Keep your gaze locked directly into the camera lens."
            ),
            landmarks = lm,
            tags = listOf("Fashion", "Street", "Outdoor", "Full Body"),
            isFullBody = true
        )
    }

    private fun createUrbanStoopPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.26f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.41f, 0.36f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.59f, 0.36f)
        // Sitting forward, elbows resting on raised knees
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.36f, 0.52f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.45f, 0.50f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.64f, 0.52f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.55f, 0.50f)
        // Hips lower
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.43f, 0.60f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.57f, 0.60f)
        // Knees bent up
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.34f, 0.56f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.38f, 0.82f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.66f, 0.56f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.62f, 0.82f)

        return PoseTemplate(
            id = "urban_stoop",
            title = "Urban Stoop Chill",
            category = "Sitting",
            difficulty = "Medium",
            description = "Candid sitting posture with elbows anchored on knees, ideal for stairs and curb shots.",
            instructions = listOf(
                "Sit on stairs, ledge, or a low bench.",
                "Rest both forearms gently across your bent knees.",
                "Lean forward toward the camera with a relaxed chin."
            ),
            landmarks = lm,
            tags = listOf("Sitting", "Street", "Outdoor", "Casual"),
            isFullBody = true
        )
    }

    private fun createCoffeeTablePose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        // Upper body cafe table lean
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.51f, 0.22f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.38f, 0.34f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.62f, 0.34f)
        // Left hand supporting chin, right arm resting on table
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.37f, 0.50f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.48f, 0.28f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.66f, 0.50f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.58f, 0.52f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.42f, 0.66f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.58f, 0.66f)

        return PoseTemplate(
            id = "cafe_table_glow",
            title = "Coffee Table Glow",
            category = "Indoor",
            difficulty = "Easy",
            description = "Intimate portrait framing with one hand delicately supporting the cheek or chin.",
            instructions = listOf(
                "Rest your elbow on a table or flat surface.",
                "Gently rest your cheek or jaw against your fingertips.",
                "Tilt head 15 degrees toward your raised hand."
            ),
            landmarks = lm,
            tags = listOf("Indoor", "Sitting", "Upper Body", "Casual"),
            isFullBody = false
        )
    }

    private fun createHighFashionTwistPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.14f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.42f, 0.24f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.58f, 0.27f)
        // Dramatic asymmetrical arms
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.32f, 0.22f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.42f, 0.12f) // hand touching head
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.68f, 0.38f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.60f, 0.48f)
        // Torso twist
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.44f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.54f, 0.49f)
        // Crossed legs
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.51f, 0.68f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.48f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.46f, 0.70f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.56f, 0.88f)

        return PoseTemplate(
            id = "fashion_twist",
            title = "High-Fashion Twist",
            category = "Fashion",
            difficulty = "Pro",
            description = "Sculptural silhouette with hand in hair and cross-legged optical elongation.",
            instructions = listOf(
                "Run your left fingers softly through your hair.",
                "Cross your left leg over your right knee.",
                "Rotate your torso 30 degrees while facing forward."
            ),
            landmarks = lm,
            tags = listOf("Fashion", "Full Body", "Indoor", "Standing"),
            isFullBody = true
        )
    }

    private fun createTravelHorizonPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.53f, 0.16f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.44f, 0.26f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.62f, 0.25f)
        // Hand shading eyes looking away
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.70f, 0.24f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.56f, 0.15f)
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.38f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.40f, 0.50f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.46f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.58f, 0.48f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.44f, 0.68f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.42f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.58f, 0.68f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.60f, 0.88f)

        return PoseTemplate(
            id = "travel_horizon",
            title = "Looking at Horizon",
            category = "Travel",
            difficulty = "Medium",
            description = "Scenic landscape pose with hand shading eyes looking off-camera at landmarks.",
            instructions = listOf(
                "Turn body three-quarters toward your favorite scenery.",
                "Raise your right hand to gently shade your brow.",
                "Look naturally into the distance beyond the camera."
            ),
            landmarks = lm,
            tags = listOf("Travel", "Outdoor", "Full Body", "Standing"),
            isFullBody = true
        )
    }

    private fun createTokyoPeacePose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.48f, 0.20f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.38f, 0.32f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.58f, 0.33f)
        // Left hand peace sign near eye/cheek
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.30f, 0.32f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.40f, 0.20f)
        // Right arm down
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.64f, 0.44f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.64f, 0.58f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.43f, 0.56f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.55f, 0.57f)

        return PoseTemplate(
            id = "tokyo_peace",
            title = "Tokyo Peace Vibe",
            category = "Casual",
            difficulty = "Easy",
            description = "Playful street style signature with peace sign framed near the cheekbone.",
            instructions = listOf(
                "Bring your left hand up next to your cheek or eye.",
                "Flash a subtle peace sign angled toward the camera.",
                "Give a bright or smirking expression."
            ),
            landmarks = lm,
            tags = listOf("Casual", "Street", "Upper Body", "Outdoor"),
            isFullBody = false
        )
    }

    private fun createCleanFitCheckPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.16f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.41f, 0.26f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.59f, 0.26f)
        // Adjusting jacket lapel or collar with one hand
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.34f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.45f, 0.30f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.63f, 0.40f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.62f, 0.54f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.45f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.55f, 0.48f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.43f, 0.68f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.42f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.57f, 0.68f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.58f, 0.88f)

        return PoseTemplate(
            id = "clean_fit_check",
            title = "Clean Fit Check",
            category = "Mirror",
            difficulty = "Medium",
            description = "Crisp outfit showcase holding jacket lapel or collar with centered composure.",
            instructions = listOf(
                "Hold your jacket lapel or collar with your left hand.",
                "Keep shoulders squared and symmetrical.",
                "Ensure shoes and full outfit are visible in the frame."
            ),
            landmarks = lm,
            tags = listOf("Mirror", "Fashion", "Full Body", "Indoor"),
            isFullBody = true
        )
    }

    private fun createFloorLoungePose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.48f, 0.30f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.38f, 0.40f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.58f, 0.42f)
        // Hand planted on floor behind
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.68f, 0.52f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.72f, 0.66f)
        // Left arm draped over knee
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.32f, 0.50f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.42f, 0.58f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.42f, 0.64f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.56f, 0.64f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.36f, 0.60f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.46f, 0.78f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.62f, 0.72f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.54f, 0.82f)

        return PoseTemplate(
            id = "floor_lounge",
            title = "Floor Lounge Aesthetic",
            category = "Indoor",
            difficulty = "Medium",
            description = "Subtle low-angle sitting pose on carpet or hardwood floor.",
            instructions = listOf(
                "Sit on the floor with your right palm planted behind you.",
                "Bring one knee up and drape your left forearm over it.",
                "Angle camera from a low perspective looking slightly up."
            ),
            landmarks = lm,
            tags = listOf("Indoor", "Sitting", "Casual"),
            isFullBody = true
        )
    }

    private fun createSummitReachPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.20f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.42f, 0.29f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.58f, 0.29f)
        // Both arms raised high in victory
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.30f, 0.18f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.24f, 0.08f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.70f, 0.18f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.76f, 0.08f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.45f, 0.49f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.55f, 0.49f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.43f, 0.69f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.40f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.57f, 0.69f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.60f, 0.88f)

        return PoseTemplate(
            id = "summit_reach",
            title = "Peak Summit Reach",
            category = "Travel",
            difficulty = "Easy",
            description = "Triumphant outdoor landmark shot with both arms raised toward the sky.",
            instructions = listOf(
                "Raise both arms into an expansive V-shape above your head.",
                "Step feet slightly wide for a confident silhouette.",
                "Frame against open sky or wide panoramic landscapes."
            ),
            landmarks = lm,
            tags = listOf("Travel", "Outdoor", "Standing", "Full Body"),
            isFullBody = true
        )
    }

    private fun createEditorialAnglePose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.52f, 0.16f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.44f, 0.26f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.62f, 0.28f)
        // Arms forming angular frame around torso
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.36f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.50f, 0.42f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.70f, 0.34f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.66f, 0.22f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.46f, 0.48f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.56f, 0.50f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.44f, 0.68f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.43f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.59f, 0.69f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.62f, 0.88f)

        return PoseTemplate(
            id = "editorial_angle",
            title = "Editorial Dynamic Angle",
            category = "Fashion",
            difficulty = "Pro",
            description = "Geometric magazine editorial pose featuring contrasting elbow and wrist lines.",
            instructions = listOf(
                "Raise your right elbow high with hand near collarbone.",
                "Bring left hand across lower ribs.",
                "Turn face slightly toward the higher shoulder."
            ),
            landmarks = lm,
            tags = listOf("Fashion", "Standing", "Full Body"),
            isFullBody = true
        )
    }

    private fun createConfidentFoldPose(): PoseTemplate {
        val lm = mutableMapOf<Int, PoseLandmarkPoint>()
        lm[PoseLandmarkConstants.NOSE] = PoseLandmarkPoint(0.50f, 0.18f)
        lm[PoseLandmarkConstants.LEFT_SHOULDER] = PoseLandmarkPoint(0.40f, 0.28f)
        lm[PoseLandmarkConstants.RIGHT_SHOULDER] = PoseLandmarkPoint(0.60f, 0.28f)
        // Folded arms across chest
        lm[PoseLandmarkConstants.LEFT_ELBOW] = PoseLandmarkPoint(0.36f, 0.42f)
        lm[PoseLandmarkConstants.LEFT_WRIST] = PoseLandmarkPoint(0.55f, 0.38f)
        lm[PoseLandmarkConstants.RIGHT_ELBOW] = PoseLandmarkPoint(0.64f, 0.42f)
        lm[PoseLandmarkConstants.RIGHT_WRIST] = PoseLandmarkPoint(0.45f, 0.38f)
        lm[PoseLandmarkConstants.LEFT_HIP] = PoseLandmarkPoint(0.44f, 0.52f)
        lm[PoseLandmarkConstants.RIGHT_HIP] = PoseLandmarkPoint(0.56f, 0.52f)
        lm[PoseLandmarkConstants.LEFT_KNEE] = PoseLandmarkPoint(0.43f, 0.70f)
        lm[PoseLandmarkConstants.LEFT_ANKLE] = PoseLandmarkPoint(0.42f, 0.88f)
        lm[PoseLandmarkConstants.RIGHT_KNEE] = PoseLandmarkPoint(0.57f, 0.70f)
        lm[PoseLandmarkConstants.RIGHT_ANKLE] = PoseLandmarkPoint(0.58f, 0.88f)

        return PoseTemplate(
            id = "confident_fold",
            title = "Confident Stance",
            category = "Standing",
            difficulty = "Easy",
            description = "Classic folded arms with relaxed neck and shoulders, great for professional or personal headshots.",
            instructions = listOf(
                "Cross your arms loosely across upper chest.",
                "Keep hands tucked softly under biceps rather than clenched.",
                "Relax your shoulders down away from your ears."
            ),
            landmarks = lm,
            tags = listOf("Standing", "Casual", "Upper Body", "Full Body"),
            isFullBody = true
        )
    }
}
