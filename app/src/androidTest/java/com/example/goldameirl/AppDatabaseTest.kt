package com.example.goldameirl

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.goldameirl.model.Notification
import com.example.goldameirl.model.db.DB
import com.example.goldameirl.model.db.NotificationDAO
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var notificationDao: NotificationDAO
    private lateinit var db: DB

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, DB::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        notificationDao = db.notificationDAO
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNotification() {
        val notification = Notification(title = "Title", content = "Content")
        notificationDao.insert(notification)
        val lastNotification = notificationDao.getLastNotification()
        assertEquals(lastNotification?.title, "Title")
    }
}