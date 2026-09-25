package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.HotDogRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Hot Dog Counter", appName)
    }

    @Test
    fun `test hot dog increment and reset`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repository = HotDogRepository(db.hotDogDao(), context)

        repository.reset()
        assertEquals(0, repository.getTodayCount())

        val countAfter1 = repository.increment()
        assertEquals(1, countAfter1)

        val countAfter2 = repository.increment()
        assertEquals(2, countAfter2)

        repository.reset()
        assertEquals(0, repository.getTodayCount())
    }
}
