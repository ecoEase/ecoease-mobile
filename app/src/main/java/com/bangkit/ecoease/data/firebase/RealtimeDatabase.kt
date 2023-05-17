package com.bangkit.ecoease.data.firebase

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FireBaseRealtimeDatabase{
    private val db: FirebaseDatabase = Firebase.database("https://ecoease-67140-default-rtdb.asia-southeast1.firebasedatabase.app")
    fun createMessageRef(ref: String): DatabaseReference = db.reference.child(ref)
}