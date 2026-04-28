package edu.gvsu.cis.multi_timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

class IOSCameraManager(
    private val onImageCaptured: (ByteArray?) -> Unit
) : CameraManager {
    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            picker.dismissViewControllerAnimated(true, null)

            if (image != null) {
                val imageData: NSData? = UIImageJPEGRepresentation(image, 0.8)
                val byteArray = imageData?.let { data ->
                    ByteArray(data.length.toInt()).apply {
                        @OptIn(ExperimentalForeignApi::class)
                        usePinned { pinned -> memcpy(pinned.addressOf(0), data.bytes, data.length) }
                    }
                }
                onImageCaptured(byteArray)
            } else {
                onImageCaptured(null)
            }
        }
        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            onImageCaptured(null)
        }
    }

    override fun launchCamera() {
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        picker.delegate = delegate
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            picker, animated = true, completion = null
        )
    }
}

@Composable
actual fun rememberCameraManager(onImageCaptured: (ByteArray?) -> Unit): CameraManager {
    return remember { IOSCameraManager(onImageCaptured) }
}