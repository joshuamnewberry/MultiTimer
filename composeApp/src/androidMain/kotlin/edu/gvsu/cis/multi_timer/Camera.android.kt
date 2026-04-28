package edu.gvsu.cis.multi_timer

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.ByteArrayOutputStream

class AndroidCameraManager(
    private val launch: () -> Unit
) : CameraManager {
    override fun launchCamera() = launch()
}

@Composable
actual fun rememberCameraManager(onImageCaptured: (ByteArray?) -> Unit): CameraManager {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            // Compress the bitmap to JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            onImageCaptured(stream.toByteArray())
        } else {
            onImageCaptured(null)
        }
    }

    return remember { AndroidCameraManager(launch = { launcher.launch(null) }) }
}