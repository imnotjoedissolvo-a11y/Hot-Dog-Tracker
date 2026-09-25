package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hot_dog_counts")
data class HotDogRecord(
    @PrimaryKey
    val id: Int = 1,
    val dateString: String,
    val count: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
