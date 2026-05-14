package com.example.nammamistri

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): User?

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface WorkerDao {
    @Query("SELECT * FROM workers")
    fun getAllWorkers(): Flow<List<Worker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: Worker)

    @Update
    suspend fun updateWorker(worker: Worker)

    @Delete
    suspend fun deleteWorker(worker: Worker)

    @Query("DELETE FROM workers")
    suspend fun deleteAllWorkers()

    @Query("UPDATE workers SET daysWorked = 0")
    suspend fun resetAllDaysWorked()

    @Query("UPDATE workers SET isPresentToday = NULL")
    suspend fun resetAllAttendance()
}

@Dao
interface SitePhotoDao {
    @Query("SELECT * FROM site_photos ORDER BY dateAdded DESC")
    fun getAllPhotos(): Flow<List<SitePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: SitePhoto)

    @Delete
    suspend fun deletePhoto(photo: SitePhoto)
}
