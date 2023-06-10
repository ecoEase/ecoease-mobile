package com.bangkit.ecoease.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

fun Int.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)
fun Long.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDate(): String {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val formatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd'T'HH:mm'")
        val formattedDate = this.format(formatter)
        return formattedDate
    }else{
        return this
    }
}