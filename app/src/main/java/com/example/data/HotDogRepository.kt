package com.example.data

import android.content.Context
import com.example.widget.HotDogWidgetProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HotDogRepository(
    private val dao: HotDogDao,
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("hotdog_prefs", Context.MODE_PRIVATE)

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    val countFlow: Flow<Int> = dao.getRecordFlow().map { record ->
        val today = getTodayDateString()
        if (record == null || record.dateString != today) {
            0
        } else {
            record.count
        }
    }.distinctUntilChanged()

    suspend fun getTodayCount(): Int {
        val today = getTodayDateString()
        val record = dao.getRecord()
        return if (record == null || record.dateString != today) {
            0
        } else {
            record.count
        }
    }

    suspend fun increment(): Int {
        val today = getTodayDateString()
        val currentRecord = dao.getRecord()
        val currentCount = if (currentRecord == null || currentRecord.dateString != today) {
            0
        } else {
            currentRecord.count
        }

        val newCount = currentCount + 1
        val newRecord = HotDogRecord(
            id = 1,
            dateString = today,
            count = newCount,
            lastUpdated = System.currentTimeMillis()
        )
        dao.insertOrUpdate(newRecord)

        // Save to SharedPreferences for fast widget synchronous reads
        sharedPrefs.edit().putInt("today_count", newCount).apply()

        // Notify home screen widget
        HotDogWidgetProvider.updateAllWidgets(context, newCount)

        return newCount
    }

    suspend fun reset() {
        val today = getTodayDateString()
        val resetRecord = HotDogRecord(
            id = 1,
            dateString = today,
            count = 0,
            lastUpdated = System.currentTimeMillis()
        )
        dao.insertOrUpdate(resetRecord)

        sharedPrefs.edit().putInt("today_count", 0).apply()
        HotDogWidgetProvider.updateAllWidgets(context, 0)
    }

    fun syncWidgetWithCurrentCount(count: Int) {
        sharedPrefs.edit().putInt("today_count", count).apply()
        HotDogWidgetProvider.updateAllWidgets(context, count)
    }
}
