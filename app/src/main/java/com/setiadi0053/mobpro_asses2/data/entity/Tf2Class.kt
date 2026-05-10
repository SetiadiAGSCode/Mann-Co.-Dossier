package com.setiadi0053.mobpro_asses2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tf2_classes")
data class Tf2Class(
    @PrimaryKey val id: Int,
    val name: String,
    val teamColor: String, // Hex code (e.g. #BD3B3B for RED, #5885A2 for BLU)
    val iconRes: Int? = null
)
