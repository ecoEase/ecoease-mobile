package com.bangkit.ecoease.data.room.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

//this is junction table between order and garbage tables
@Parcelize
@Entity(primaryKeys = ["orderId", "garbageId"])
data class CrossOrderGarbage(
    val orderId: String,
    @ColumnInfo(index = true)
    val garbageId: String,
) : Parcelable
