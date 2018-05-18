package crupest.cruphysics.data.world

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [WorldRecord::class], version = 1)
abstract class WorldDatabase : RoomDatabase() {
    abstract fun worldRecordDao(): WorldRecordDao

    companion object {
        private const val DATABASE_NAME = "world_database"
        private var instance: WorldDatabase? = null

        fun getInstance(context: Context): WorldDatabase {
            if (instance != null)
                synchronized(this) {
                    if (instance != null)
                        instance = Room
                                .databaseBuilder(context, WorldDatabase::class.java, DATABASE_NAME)
                                .build()
                }
            return instance!!
        }
    }
}
