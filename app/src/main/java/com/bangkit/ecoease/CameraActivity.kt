package com.bangkit.ecoease

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecoease.config.ViewModelFactory
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.helper.createImageCaptureUseCase
import com.bangkit.ecoease.helper.getOutputDirectory
import com.bangkit.ecoease.helper.takePhoto
import com.bangkit.ecoease.ui.component.FloatingButton
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val permissionsSdk28Below = listOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
val permissionsSdk28Above= listOf(
    Manifest.permission.CAMERA,
)

class CameraActivity : AppCompatActivity() {
    companion object {
        val CAMERA_X_RESULT = 200
    }

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcoEaseTheme {
                cameraExecutor = Executors.newSingleThreadExecutor()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CameraScreen(
                        executor = cameraExecutor,
                        onSavedImage = { imageUri ->
                            val intent = Intent()
                            intent.putExtra("picture", imageUri.toString())
                            setResult(CAMERA_X_RESULT, intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    executor: Executor,
    onSavedImage: (Uri) -> Unit
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = if(Build.VERSION.SDK_INT > 28) permissionsSdk28Above else permissionsSdk28Below
    )
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit){
        permissionsState.launchMultiplePermissionRequest()
    }

    PermissionsRequired(
        multiplePermissionsState = permissionsState,
        permissionsNotGrantedContent = {
            Log.d("TAG", "CameraScreen: permission error not not granted")
        },
        permissionsNotAvailableContent = {
            Log.d("TAG", "CameraScreen: permission error not available")
        }) {
        CameraScreenContent(context = context, lifecycleOwner = lifecycleOwner, onSavedImage = onSavedImage, executor = executor)
    }
}

@Composable
fun CameraScreenContent(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onSavedImage: (Uri) -> Unit,
    executor: Executor,
) {
    val previewView = remember { PreviewView(context) }
    var cameraSelector: CameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(previewView){
        imageCapture = context.createImageCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector,
            previewView = previewView,
        )
    }

    DisposableEffect(Unit){

        onDispose {

        }
    }

    Box(modifier = modifier.fillMaxSize()){
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        FloatingButton(
            description = "take picture",
            icon = Icons.Default.CameraAlt,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp)
                .size(64.dp),
            onClick = {
                takePhoto(
                    context = context,
                    filename = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture!!,
                    outpuDirectory = getOutputDirectory(context),
                    executor = executor,
                    onImageCapture = { imageUri ->
                        Log.d("Camera", "CameraScreenContent: $imageUri")
                        onSavedImage(imageUri)
                    },
                    onError = { Log.d("Camera", "CameraScreenContent: $it") }
                )
            }
        )
        FloatingButton(
            description = "gallery",
            icon = Icons.Default.BrowseGallery,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-32).dp)
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewCameraScreen() {
    EcoEaseTheme {
        CameraScreen(
            executor = ContextCompat.getMainExecutor(LocalContext.current),
            onSavedImage = {}
        )
    }
}
