package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface AsteroidDatabaseDao {

    @Query("SELECT * from AsteroidEntity WHERE closeApproachDate BETWEEN :startDate and :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroids(startDate: String, endDate: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * from AsteroidEntity WHERE closeApproachDate = :startDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsToday(startDate: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * from AsteroidEntity WHERE closeApproachDate BETWEEN :startDate and :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsThisWeek(startDate: String, endDate: String): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsteroids(vararg asteroid: AsteroidEntity)

}

@Database(entities = [AsteroidEntity::class], version = 2, exportSchema = false)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDatabaseDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}