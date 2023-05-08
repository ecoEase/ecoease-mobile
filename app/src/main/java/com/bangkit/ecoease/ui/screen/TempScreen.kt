package com.bangkit.ecoease.ui.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
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
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.ObjectDetection
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.helper.getImageUriFromBitmap
import com.bangkit.ecoease.ui.component.FloatingButton
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage

fun convertToGrayscale(inputBitmap: Bitmap): Bitmap {
    val width = inputBitmap.width
    val height = inputBitmap.height

    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = inputBitmap.getPixel(x, y)

            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            val grayscale = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

            val grayPixel = Color.rgb(grayscale, grayscale, grayscale)

            outputBitmap.setPixel(x, y, grayPixel)
        }
    }

    return outputBitmap
}

@Composable
fun TempScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    filePath: String
){
    val context = LocalContext.current

    var imageUri by remember{
        mutableStateOf(filePath)
    }
    
    try {
        val uri = Uri.parse(filePath)

        val inputStream = context.contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(inputStream)

        val input = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        val newInput = convertToGrayscale(bitmap)
        val image = TensorImage(DataType.FLOAT32)
        image.load(input)
        val byteBuffer = image.buffer

        val resultBitmap = ObjectDetection.run(context, byteBuffer, bitmap)
        imageUri = getImageUriFromBitmap(context, resultBitmap).toString()

    }catch (e: Exception){
        Log.d("TAG", "TempScreen: $e")
    }

    Scaffold(
        floatingActionButton = {
            FloatingButton(description = "open camera", icon = Icons.Default.CameraAlt, onClick = {
                navController.navigate(Screen.Camera.route)
            })
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
            AsyncImage(
                model = imageUri,
                modifier = Modifier
                    .size(320.dp)
                    .clip(RoundedCornerShape(16.dp))
                ,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24),
            )
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewScreen(){
    EcoEaseTheme {
        TempScreen(filePath = "", navController = rememberNavController())
    }
}