package com.bangkit.ecoease.data.dummy

import com.bangkit.ecoease.data.room.model.User
import com.bangkit.ecoease.helper.generateUUID

object UserDummy {
    fun get(): User = User(
        id = generateUUID(),
        firstName = "Septa",
        lastName = "Alfauzan",
        email = "xyz@xyz.com",
        phoneNumber = "000",
        password = "*****",
        urlPhotoProfile = "",
    )
}