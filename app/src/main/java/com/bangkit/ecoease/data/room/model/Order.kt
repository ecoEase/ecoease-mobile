package com.bangkit.ecoease.data.room.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import kotlinx.parcelize.Parcelize

enum class StatusOrderItem{
    NOT_TAKEN, ON_PROCESS, TAKEN, CANCELED
}

@Parcelize
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "status")
    val status: StatusOrderItem,

    @ColumnInfo(name = "total_transaction")
    val totalTransaction: Int,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "mitra_id")
    val mitraId: String,

    @ColumnInfo(name = "location_id")
    val locationId: String,

    @ColumnInfo(name = "address_id")
    val addressId: String,

    @ColumnInfo(name = "created")
    val created: String,
) : Parcelable

data class OrderWithGarbage(
    @Embedded
    val order: Order,

    @Relation(
        parentColumn = "id",
        entity = Garbage::class,
        entityColumn = "id",
        associateBy = Junction(
            value = CrossOrderGarbage::class,
            parentColumn = "orderId",
            entityColumn = "garbageId"
        )
    )
    val garbage: List<Garbage>
)
