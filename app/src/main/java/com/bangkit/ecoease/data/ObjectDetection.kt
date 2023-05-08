package com.bangkit.ecoease.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import com.bangkit.ecoease.ml.SsdliteMobilenetV2
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer


object ObjectDetection{

    val label = listOf("Got Masked", "No Mask", "Wear Incorectly")

    fun run(context: Context, inputBuffer: ByteBuffer, image: Bitmap): Bitmap{
        val model = SsdliteMobilenetV2.newInstance(context)

// Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(inputBuffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val outputFeature1 = outputs.outputFeature1AsTensorBuffer
        val outputFeature2 = outputs.outputFeature2AsTensorBuffer
        val outputFeature3 = outputs.outputFeature3AsTensorBuffer

        val boundLocation = outputFeature0.floatArray
        val classType = outputFeature1.intArray
        val score = outputFeature2.floatArray
        val num = outputFeature3.intArray

        classType.forEach { Log.d("TAG", "run: $it") }

        val imageWithBound = drawRectBound(image = image, bounds = boundLocation, classType = classType[0], score = score[0])
// Releases model resources if no longer used.
        model.close()
        return imageWithBound
    }

    private fun drawRectBound(image: Bitmap, bounds: FloatArray, classType: Int, score: Float): Bitmap{

        var copyImage = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(copyImage)
        val paint = Paint()
        var colors = listOf<Int>(
            Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
            Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)

        val width = copyImage.width
        val height = copyImage.height
        val x = 0

        paint.color = colors[1]
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawRect(
            RectF(
                bounds[x+1] * width,
                bounds[x] * height,
                bounds[x+3] * width,
                bounds[x+2] * height),
            paint)//draw box
        paint.style = Paint.Style.FILL
        paint.textSize = 32f
        canvas.drawText("${label[classType]} ${score}", bounds[x+1] * width, bounds[x] * height - 32, paint)//draw label and score

        return copyImage
    }


    @Throws(IOException::class)
    fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
}
