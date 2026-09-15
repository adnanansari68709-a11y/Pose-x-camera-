package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CameraSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("posex_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<CameraSettings> = _settings.asStateFlow()

    private fun loadSettings(): CameraSettings {
        return CameraSettings(
            isFrontCamera = prefs.getBoolean(KEY_FRONT_CAMERA, true),
            flashMode = prefs.getInt(KEY_FLASH_MODE, 0),
            timerSeconds = prefs.getInt(KEY_TIMER_SECONDS, 3),
            overlayOpacity = prefs.getFloat(KEY_OVERLAY_OPACITY, 0.75f),
            overlayScale = prefs.getFloat(KEY_OVERLAY_SCALE, 1.0f),
            overlayOffsetX = prefs.getFloat(KEY_OFFSET_X, 0f),
            overlayOffsetY = prefs.getFloat(KEY_OFFSET_Y, 0f),
            isMirrored = prefs.getBoolean(KEY_IS_MIRRORED, false),
            hapticsEnabled = prefs.getBoolean(KEY_HAPTICS, true),
            countdownSoundEnabled = prefs.getBoolean(KEY_COUNTDOWN_SOUND, true),
            skeletonStyle = prefs.getString(KEY_SKELETON_STYLE, "Neon Violet") ?: "Neon Violet"
        )
    }

    fun updateFrontCamera(isFront: Boolean) {
        prefs.edit().putBoolean(KEY_FRONT_CAMERA, isFront).apply()
        _settings.value = _settings.value.copy(isFrontCamera = isFront)
    }

    fun updateFlashMode(flashMode: Int) {
        prefs.edit().putInt(KEY_FLASH_MODE, flashMode).apply()
        _settings.value = _settings.value.copy(flashMode = flashMode)
    }

    fun updateTimerSeconds(seconds: Int) {
        prefs.edit().putInt(KEY_TIMER_SECONDS, seconds).apply()
        _settings.value = _settings.value.copy(timerSeconds = seconds)
    }

    fun updateOverlayOpacity(opacity: Float) {
        prefs.edit().putFloat(KEY_OVERLAY_OPACITY, opacity).apply()
        _settings.value = _settings.value.copy(overlayOpacity = opacity)
    }

    fun updateOverlayTransform(scale: Float, offsetX: Float, offsetY: Float) {
        prefs.edit()
            .putFloat(KEY_OVERLAY_SCALE, scale)
            .putFloat(KEY_OFFSET_X, offsetX)
            .putFloat(KEY_OFFSET_Y, offsetY)
            .apply()
        _settings.value = _settings.value.copy(
            overlayScale = scale,
            overlayOffsetX = offsetX,
            overlayOffsetY = offsetY
        )
    }

    fun resetOverlayTransform() {
        updateOverlayTransform(1.0f, 0f, 0f)
        updateMirrored(false)
    }

    fun updateMirrored(mirrored: Boolean) {
        prefs.edit().putBoolean(KEY_IS_MIRRORED, mirrored).apply()
        _settings.value = _settings.value.copy(isMirrored = mirrored)
    }

    fun updateHaptics(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _settings.value = _settings.value.copy(hapticsEnabled = enabled)
    }

    fun updateCountdownSound(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_COUNTDOWN_SOUND, enabled).apply()
        _settings.value = _settings.value.copy(countdownSoundEnabled = enabled)
    }

    fun updateSkeletonStyle(style: String) {
        prefs.edit().putString(KEY_SKELETON_STYLE, style).apply()
        _settings.value = _settings.value.copy(skeletonStyle = style)
    }

    companion object {
        private const val KEY_FRONT_CAMERA = "key_front_camera"
        private const val KEY_FLASH_MODE = "key_flash_mode"
        private const val KEY_TIMER_SECONDS = "key_timer_seconds"
        private const val KEY_OVERLAY_OPACITY = "key_overlay_opacity"
        private const val KEY_OVERLAY_SCALE = "key_overlay_scale"
        private const val KEY_OFFSET_X = "key_offset_x"
        private const val KEY_OFFSET_Y = "key_offset_y"
        private const val KEY_IS_MIRRORED = "key_is_mirrored"
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_COUNTDOWN_SOUND = "key_countdown_sound"
        private const val KEY_SKELETON_STYLE = "key_skeleton_style"
    }
}
