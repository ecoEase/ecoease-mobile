package com.bangkit.ecoease.data.firebase

import android.util.Log
import com.bangkit.ecoease.BuildConfig
import com.bangkit.ecoease.data.model.Chatroom
import com.bangkit.ecoease.data.model.Message
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

object FireBaseRealtimeDatabase{
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance (BuildConfig.firebase_realtime_db_url)
    private val listRooms = mutableListOf<String>()
    fun createMessageRef(ref: String): DatabaseReference = db.reference.child(ref)
    fun createRoomRef(): DatabaseReference = db.reference

    fun createNewRoom(roomId: String){
        db.reference.push().setValue(roomId)
    }
    fun DatabaseReference.getAllRoomsKey(): Task<List<String>>{
        val taskCompletionSource = TaskCompletionSource<List<String>>()
        this.get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val result = task.result
                result?.let {
                    val final = result.children.map { snapshot ->
                        snapshot.key ?: ""
                    }
                    taskCompletionSource.setResult(final)
                }
            }
            if(task.isCanceled){
                Log.d("UsersChat", "getAllRooms: ${task.exception?.message}")
                task.exception?.let {
                    taskCompletionSource.setException(it)
                }
            }
        }
        return taskCompletionSource.task
    }


    fun DatabaseReference.getCurrentChats(): Task<MutableList<Message>>{
        val taskCompletionSource = TaskCompletionSource<MutableList<Message>>()
        this.get().addOnCompleteListener{task ->
            if(task.isSuccessful){
                val result = task.result
                result?.let {
                    val final = result.children.map { snapshot ->
                        snapshot.getValue(Message::class.java)!!
                    }
                    taskCompletionSource.setResult(final as MutableList<Message>)
                }
            }
            if(task.isCanceled){
                task.exception?.let {
                    taskCompletionSource.setException(it)
                }
            }
        }
        return taskCompletionSource.task
    }

    fun chatChildEventListener(onChildAdded: (Message) -> Unit) = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            // Handle new child node added
            val childData = dataSnapshot.getValue(Message::class.java)
            childData?.let{
                onChildAdded(it)
            }
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }
    fun roomChildEventListener(onChildAdded: (Chatroom) -> Unit, onChildRemoved: (Chatroom) -> Unit) = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val childData = Chatroom(key = snapshot.key, value = snapshot.value.toString())
            childData?.let {
                onChildAdded(it)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val childData = Chatroom(key = snapshot.key, value = snapshot.value.toString())
            childData?.let {
                onChildRemoved(it)
            }
        }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }
    fun listenRoomKeys(): Task<List<String?>>{
        val taskCompletionSource = TaskCompletionSource<List<String?>>()
        db.reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.key?.let {
                    listRooms.add(it)
                }
                taskCompletionSource.setResult(listRooms)
            }

            override fun onCancelled(error: DatabaseError) {
                taskCompletionSource.setException(error.toException())
            }
        })
        return taskCompletionSource.task
    }
}