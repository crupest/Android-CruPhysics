package crupest.cruphysics.data

import android.content.Context

class WorldRepository(context: Context) {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private var cache: List<WorldRecord> = dao.getRecords()

    private fun invalidateCache() {
        cache = dao.getRecords()
    }

    val recordCount: Int
        get() = cache.size

    fun getRecord(position: Int) = cache[position]

    fun addRecord(record: WorldRecord) {
        dao.insert(record)
        invalidateCache()
    }
}
