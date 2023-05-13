package com.bangkit.ecoease.data.model
import android.net.Uri

data class ImageCaptured(
    val uri: Uri,
    val isBackCam: Boolean
)