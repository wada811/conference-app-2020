package io.github.droidkaigi.confsched2020.data.db.di

import android.content.Context
import androidx.room.Room
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.CacheDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.RoomDatabase
import io.github.droidkaigi.confsched2020.data.db.internal.SessionFeedbackDatabase

class DbModule(private val context: Context) {
    private val cacheDatabase: CacheDatabase by lazy {
        Room.databaseBuilder(
            context,
            CacheDatabase::class.java,
            "droidkaigi.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val sessionFeedbackDatabase: SessionFeedbackDatabase by lazy {
        Room.databaseBuilder(
            context,
            SessionFeedbackDatabase::class.java,
            "session_feedback.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    private val roomDatabase: RoomDatabase by lazy {
        RoomDatabase(
            cacheDatabase,
            sessionFeedbackDatabase
        )
    }
    val sessionDatabase: SessionDatabase by lazy { roomDatabase }
    val staffDatabase: StaffDatabase by lazy { roomDatabase }
}
