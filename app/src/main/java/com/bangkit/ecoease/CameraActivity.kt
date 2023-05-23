package com.bangkit.ecoease

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.bangkit.ecoease.config.ViewModelFactory
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.helper.createImageCaptureUseCase
import com.bangkit.ecoease.helper.getOutputDirectory
import com.bangkit.ecoease.helper.takePhoto
import com.bangkit.ecoease.ui.component.FloatingButton
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.bangkit.ecoease.ui.theme.LightTosca
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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

    val cameraViewModel: CameraViewModel by viewModels {
        ViewModelFactory(Injection.provideInjection(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

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
                        onSavedImage = { imageUri, isBackCam ->
                            val intent = Intent()
                            intent.putExtra("picture", imageUri.toString())
                            intent.putExtra("cam-facing", isBackCam)
                            setResult(CAMERA_X_RESULT, intent)
                            finish()
                        },
                        // TODO: FIX open gallery through camera bug, data already set but on scan screen its not refreshed 
                        openGallery = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_GET_CONTENT
                                type = "image/*"
                            }
                            val chooser = Intent.createChooser(intent, "Choose a picture")
                            launcherIntentGallery.launch(chooser)
                        }
                    )
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("TAG", "onContextItemSelected: ${item.itemId}")
        when(item.itemId ){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectedImage = result.data?.data as Uri
            selectedImage?.let { uri ->
                cameraViewModel.setImage(
                    ImageCaptured(uri = uri, isBackCam = true)
                )
                finish()
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    executor: Executor,
    openGallery: () -> Unit,
    onSavedImage: (Uri, Boolean) -> Unit
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
        CameraScreenContent(context = context, lifecycleOwner = lifecycleOwner, onSavedImage = onSavedImage, executor = executor, openGallery = openGallery)
    }
}

@Composable
fun CameraScreenContent(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    openGallery: () -> Unit,
    onSavedImage: (Uri, Boolean) -> Unit,
    executor: Executor,
) {
    val previewView = remember { PreviewView(context) }
    var isBackCamera: Boolean by rememberSaveable{ mutableStateOf(true) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(previewView, isBackCamera){
        imageCapture = context.createImageCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = if(isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA,
            previewView = previewView,
        )
    }

    Box(modifier = modifier.fillMaxSize()){
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .offset(y = (-32).dp)
            .padding(horizontal = 48.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){

            FloatingButton(
                backgroundColor = LightTosca,
                iconColor = Color.Black,
                description = "swap camera",
                icon = Icons.Default.FlipCameraAndroid,
                modifier = Modifier,
                onClick = { isBackCamera = !isBackCamera }
            )

            FloatingButton(
                description = "take picture",
                icon = Icons.Default.CameraAlt,
                modifier = Modifier
                    .size(64.dp),
                onClick = {
                    takePhoto(
                        context = context,
                        filename = "yyyy-MM-dd-HH-mm-ss-SSS",
                        imageCapture = imageCapture!!,
                        outpuDirectory = getOutputDirectory(context),
                        executor = executor,
                        onImageCapture = { imageUri ->
                            onSavedImage(imageUri, isBackCamera)
                        },
                        onError = { Log.d("Camera", "CameraScreenContent: $it") }
                    )
                }
            )

            // TODO: make open gallery functionality
            FloatingButton(
                backgroundColor = LightTosca,
                iconColor = Color.Black,
                description = "gallery",
                icon = Icons.Default.Image,
                modifier = Modifier,
                onClick = { openGallery() }
            )
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewCameraScreen() {
    EcoEaseTheme {
        CameraScreen(
            executor = ContextCompat.getMainExecutor(LocalContext.current),
            openGallery = {},
            onSavedImage = { _, _ -> }
        )
    }
}
