package com.bangkit.ecoease.data.room.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "detail_transactions")
data class DetailTransaction(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "order_id")
    val orderId: String,

    @ColumnInfo(name = "garbage_id")
    val garbageId: String,

    @ColumnInfo(name = "qty")
    val qty: Long,

    @ColumnInfo(name = "total")
    val total: Long,
): Parcelable
