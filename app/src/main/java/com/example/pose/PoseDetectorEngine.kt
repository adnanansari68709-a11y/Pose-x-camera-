package com.example.pose

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.model.PoseLandmarkPoint
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.atomic.AtomicBoolean

class PoseDetectorEngine {

    // Stream mode detector for live CameraX frames
    private val streamDetector: PoseDetector by lazy {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    // Single image mode detector for Gallery images
    private val staticImageDetector: PoseDetector by lazy {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    private val isProcessingFrame = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    fun processLiveFrame(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean,
        onPoseDetected: (Map<Int, PoseLandmarkPoint>?, Boolean) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        // Throttle frame processing if already busy
        if (!isProcessingFrame.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        val imageWidth = if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageProxy.height.toFloat()
        } else {
            imageProxy.width.toFloat()
        }

        val imageHeight = if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageProxy.width.toFloat()
        } else {
            imageProxy.height.toFloat()
        }

        streamDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                val landmarkMap = extractLandmarks(pose, imageWidth, imageHeight, isFrontCamera)
                val hasPerson = landmarkMap.isNotEmpty()
                onPoseDetected(if (hasPerson) landmarkMap else null, false)
            }
            .addOnFailureListener {
                onPoseDetected(null, false)
            }
            .addOnCompleteListener {
                isProcessingFrame.set(false)
                imageProxy.close()
            }
    }

    fun detectPoseFromBitmap(
        bitmap: Bitmap,
        onResult: (Map<Int, PoseLandmarkPoint>?, String?) -> Unit
    ) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        staticImageDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                val landmarkMap = extractLandmarks(pose, imageWidth, imageHeight, isFrontCamera = false)
                if (landmarkMap.size >= 6) { // Ensure sufficient landmarks found
                    onResult(landmarkMap, null)
                } else {
                    onResult(null, "No clear person pose found. Try another photo.")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, "Pose detection failed: ${e.localizedMessage ?: "Unknown error"}")
            }
    }

    private fun extractLandmarks(
        pose: Pose,
        width: Float,
        height: Float,
        isFrontCamera: Boolean
    ): Map<Int, PoseLandmarkPoint> {
        val allLandmarks = pose.allPoseLandmarks
        if (allLandmarks.isEmpty() || width <= 0 || height <= 0) {
            return emptyMap()
        }

        val result = mutableMapOf<Int, PoseLandmarkPoint>()

        for (landmark in allLandmarks) {
            val type = landmark.landmarkType
            val position = landmark.position3D

            // Normalize coordinates to [0f..1f]
            var normX = (position.x / width).coerceIn(0f, 1f)
            val normY = (position.y / height).coerceIn(0f, 1f)

            // Invert X coordinate for front camera mirroring if needed
            if (isFrontCamera) {
                normX = 1.0f - normX
            }

            // Only consider landmarks with reasonable in-frame confidence
            if (landmark.inFrameLikelihood >= 0.25f) {
                result[type] = PoseLandmarkPoint(
                    x = normX,
                    y = normY,
                    z = position.z / width,
                    likelihood = landmark.inFrameLikelihood
                )
            }
        }

        return result
    }

    fun close() {
        try {
            streamDetector.close()
            staticImageDetector.close()
        } catch (_: Exception) {
        }
    }
}
