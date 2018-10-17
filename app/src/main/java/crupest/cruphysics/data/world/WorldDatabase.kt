package crupest.cruphysics.data.world

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [WorldRecordEntity::class], version = 1, exportSchema = false)
abstract class WorldDatabase : RoomDatabase() {
    abstract fun worldRecordDao(): WorldRecordDao

    companion object {
        private const val DATABASE_NAME = "world_database"
        private var instance: WorldDatabase? = null

        fun getInstance(context: Context): WorldDatabase =
                instance ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }

        private fun buildDatabase(context: Context): WorldDatabase {
            return Room.databaseBuilder(context, WorldDatabase::class.java, DATABASE_NAME).build()
        }
    }
}
