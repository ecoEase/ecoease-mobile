package com.bangkit.ecoease.ui.screen

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.ObjectDetection
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.helper.getImageUriFromBitmap
import com.bangkit.ecoease.helper.getImageUriFromTempBitmap
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.FloatingButton
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

@Composable
fun TempScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    filePath: String,
    imageCapturedState: StateFlow<UiState<ImageCaptured>>,
    onLoadingImageState: () -> Unit,
    openCamera: () -> Unit
){
    val context = LocalContext.current
    var predictedImgUri: Uri? by rememberSaveable {
        mutableStateOf(null)
    }
    var loadingPrediction by rememberSaveable{ mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingButton(
                description = "open camera",
                icon = Icons.Default.CameraAlt,
                onClick = {
                    openCamera()
                }
            )
        }
    ) {paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("predict state", "TempScreen: $predictedImgUri")
            imageCapturedState.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when(uiState){
                    is UiState.Success -> {
                        LaunchedEffect(uiState.data) {
                            loadingPrediction = true
                            predictedImgUri = getImageUriPrediction(context, uiState.data.uri, uiState.data.isBackCam)
                            loadingPrediction = false
                        }
                    }
                    is UiState.Error ->{
                        predictedImgUri = null
                    }
                    is UiState.Loading -> {
                        onLoadingImageState()
                    }
                }
            }

            if(loadingPrediction) LoadingScanAnim()
            else AsyncImage(
                model = predictedImgUri ?: "",
                modifier = Modifier
                    .size(320.dp)
                    .clip(RoundedCornerShape(16.dp)),
                onLoading = {loadingState ->
                    Log.d("TAG", "TempScreen: loading")
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24),
            )
        }
    }
}

@Composable
fun LoadingScanAnim(
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_image_scan_load)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        modifier = modifier
            .size(320.dp)
            .clip(RectangleShape),
        composition = composition,
        progress = { progress }
    )
}

suspend fun getImageUriPrediction(context: Context, uri: Uri, isBackCam: Boolean): Uri = withContext(Dispatchers.IO){
    // TODO: fix image prediction result, not rotated 
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(inputStream)

        val resultBitmap = ObjectDetection.run(context, bitmap, isBackCam)
        //SAVE TO GALLERY
//        return@withContext getImageUriFromBitmap(
//            context = context,
//            bitmap = resultBitmap
//        )
        //SAVE TO TEMPORARY FILE
        return@withContext getImageUriFromTempBitmap(
            context = context,
            bitmap = resultBitmap,
//            rotate = if (isBackCam) 90f else -90f
            rotate = 0f
        )
    }catch (e: Exception){
        Log.d("TAG", "TempScreen: $e")
        throw e
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewScreen(){
    EcoEaseTheme {
        TempScreen(
            filePath = "",
            navController = rememberNavController(),
            openCamera = {},
            onLoadingImageState = {},
            imageCapturedState = MutableStateFlow(UiState.Loading)
        )
    }
}