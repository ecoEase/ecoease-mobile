package com.bangkit.ecoease.data.dummy

import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.helper.generateUUID

object AddressDummy {
    val listSavedAddress = mutableListOf(
        Address(
            id = generateUUID(),
            name = "Alamat 1",
            detail = "Tanggulturus",
            district = "Besuki",
            city = "Tulungagung",
        ),
        Address(
            id = generateUUID(),
            name = "Alamat 2",
            detail = "Ambarawa",
            district = "Bendungan Sutami",
            city = "Malang",
        ),
    )
}