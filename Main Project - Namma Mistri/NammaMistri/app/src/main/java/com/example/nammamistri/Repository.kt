package com.example.nammamistri

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val workerDao: WorkerDao, 
    private val sitePhotoDao: SitePhotoDao
) {
    val allWorkers: Flow<List<Worker>> = workerDao.getAllWorkers()
    val allPhotos: Flow<List<SitePhoto>> = sitePhotoDao.getAllPhotos()

    // User operations
    suspend fun registerUser(user: User): Long = userDao.registerUser(user)
    suspend fun login(email: String, password: String): User? = userDao.login(email, password)
    suspend fun getUserByMobile(mobile: String): User? = userDao.getUserByMobile(mobile)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Worker operations
    suspend fun insertWorker(worker: Worker) {
        workerDao.insertWorker(worker)
    }

    suspend fun updateWorker(worker: Worker) {
        workerDao.updateWorker(worker)
    }

    suspend fun deleteWorker(worker: Worker) {
        workerDao.deleteWorker(worker)
    }

    suspend fun updateAttendance(updatedWorkers: List<Worker>) {
        updatedWorkers.forEach { workerDao.updateWorker(it) }
    }

    suspend fun resetAllDaysWorked() {
        workerDao.resetAllDaysWorked()
    }

    suspend fun resetAllAttendance() {
        workerDao.resetAllAttendance()
    }

    // Photo operations
    suspend fun insertPhoto(photo: SitePhoto) {
        sitePhotoDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(photo: SitePhoto) {
        sitePhotoDao.deletePhoto(photo)
    }
}
