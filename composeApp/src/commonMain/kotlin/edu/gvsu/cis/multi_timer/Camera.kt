package edu.gvsu.cis.multi_timer

import androidx.compose.runtime.Composable

interface CameraManager {
    fun launchCamera()
}

@Composable
expect fun rememberCameraManager(onImageCaptured: (ByteArray?) -> Unit): CameraManager