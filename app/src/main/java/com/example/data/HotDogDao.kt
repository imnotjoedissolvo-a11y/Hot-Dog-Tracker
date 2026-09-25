package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HotDogDao {
    @Query("SELECT * FROM hot_dog_counts WHERE id = 1 LIMIT 1")
    fun getRecordFlow(): Flow<HotDogRecord?>

    @Query("SELECT * FROM hot_dog_counts WHERE id = 1 LIMIT 1")
    suspend fun getRecord(): HotDogRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: HotDogRecord)

    @Query("UPDATE hot_dog_counts SET count = 0, lastUpdated = :timestamp WHERE id = 1")
    suspend fun resetCount(timestamp: Long = System.currentTimeMillis())
}
