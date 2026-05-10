package com.setiadi0053.mobpro_asses2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = Tf2Class::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classId: Int,
    val name: String,
    val description: String,
    val dateObtained: Long,
    val isFavorite: Boolean = false,
    val isDeleted: Boolean = false, // Novelty feature: Recycle Bin
    val notes: String = ""
)
