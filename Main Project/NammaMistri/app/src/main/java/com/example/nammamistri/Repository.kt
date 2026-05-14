package com.example.nammamistri

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AppRepository(
    private val userDao: UserDao,
    private val workerDao: WorkerDao, 
    private val sitePhotoDao: SitePhotoDao,
    private val attendanceDao: AttendanceDao
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

    suspend fun getWorkerById(id: Long) = workerDao.getWorkerById(id)

    suspend fun resetMonthlyData() {
        workerDao.resetMonthlyData()
    }

    suspend fun resetDailyAttendance() {
        workerDao.resetDailyAttendance()
    }

    // Attendance operations
    suspend fun insertOrUpdateAttendance(record: AttendanceRecord) {
        attendanceDao.insertOrUpdateAttendance(record)
    }

    suspend fun getAttendance(workerId: Long, date: Long) = attendanceDao.getAttendance(workerId, date)

    fun getAttendanceForMonth(workerId: Long, startDate: Long, endDate: Long) = 
        attendanceDao.getAttendanceForMonth(workerId, startDate, endDate)

    fun getAttendanceForAllWorkers(date: Long) = attendanceDao.getAttendanceForAllWorkers(date)
    
    suspend fun deleteAttendance(record: AttendanceRecord) {
        attendanceDao.deleteAttendance(record)
    }

    // Photo operations
    suspend fun insertPhoto(photo: SitePhoto) {
        sitePhotoDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(photo: SitePhoto) {
        sitePhotoDao.deletePhoto(photo)
    }
}
