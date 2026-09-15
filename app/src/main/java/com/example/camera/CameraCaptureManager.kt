package com.example.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.model.PoseLandmarkPoint
import com.example.pose.PoseDetectorEngine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraCaptureManager(
    private val context: Context,
    private val poseDetectorEngine: PoseDetectorEngine
) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    var isFrontCamera: Boolean = true
        private set

    var isTorchOn: Boolean = false
        private set

    var currentZoom: Float = 1.0f
        private set

    fun initialize(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        startFrontCamera: Boolean = true,
        onPoseDetected: (Map<Int, PoseLandmarkPoint>?, Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        this.isFrontCamera = startFrontCamera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycleOwner, previewView, onPoseDetected, onError)
            } catch (e: Exception) {
                onError("Failed to initialize camera: ${e.localizedMessage ?: "Unknown error"}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onPoseDetected: (Map<Int, PoseLandmarkPoint>?, Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val provider = cameraProvider ?: return

        val preview = Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    poseDetectorEngine.processLiveFrame(
                        imageProxy = imageProxy,
                        isFrontCamera = isFrontCamera,
                        onPoseDetected = onPoseDetected
                    )
                }
            }

        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        try {
            provider.unbindAll()
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
            // Apply zoom if set
            camera?.cameraControl?.setZoomRatio(currentZoom)
        } catch (e: Exception) {
            onError("Camera binding error: ${e.localizedMessage ?: "Hardware camera unavailable"}")
        }
    }

    fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onPoseDetected: (Map<Int, PoseLandmarkPoint>?, Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        isFrontCamera = !isFrontCamera
        isTorchOn = false
        bindCameraUseCases(lifecycleOwner, previewView, onPoseDetected, onError)
    }

    fun toggleTorch(onError: (String) -> Unit) {
        val cam = camera ?: return
        if (cam.cameraInfo.hasFlashUnit()) {
            isTorchOn = !isTorchOn
            cam.cameraControl.enableTorch(isTorchOn)
        } else {
            onError("Flash is not available on this camera")
        }
    }

    fun setZoom(ratio: Float) {
        currentZoom = ratio.coerceIn(1.0f, 4.0f)
        camera?.cameraControl?.setZoomRatio(currentZoom)
    }

    fun capturePhoto(
        onSuccess: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val capture = imageCapture
        if (capture == null) {
            onError("Camera capture is not ready")
            return
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "POSEX_$timeStamp.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/POSEX")
            }
        }

        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using front camera so preview matches capture
            isReversedHorizontal = isFrontCamera
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).setMetadata(metadata).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    if (savedUri != null) {
                        onSuccess(savedUri)
                    } else {
                        // Fallback: query recently inserted file if savedUri is null
                        val fallbackUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        onSuccess(fallbackUri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError("Failed to save photo: ${exception.localizedMessage ?: "Storage write failed"}")
                }
            }
        )
    }

    fun shutdown() {
        try {
            cameraExecutor.shutdown()
            cameraProvider?.unbindAll()
        } catch (_: Exception) {
        }
    }
}
