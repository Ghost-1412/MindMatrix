package com.example.nammamistri

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository
    val allWorkers: Flow<List<Worker>>
    val allPhotos: Flow<List<SitePhoto>>
    private val sharedPreferences = application.getSharedPreferences("namma_mistri_prefs", Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.userDao(), database.workerDao(), database.sitePhotoDao())
        allWorkers = repository.allWorkers
        allPhotos = repository.allPhotos
        
        checkAndResetMonthlyAttendance()
        checkAndResetDailyAttendance()
    }

    private fun checkAndResetMonthlyAttendance() {
        val lastResetMonth = sharedPreferences.getInt("last_reset_month", -1)
        val lastResetYear = sharedPreferences.getInt("last_reset_year", -1)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        if (lastResetMonth != currentMonth || lastResetYear != currentYear) {
            viewModelScope.launch {
                repository.resetAllDaysWorked()
                sharedPreferences.edit().apply {
                    putInt("last_reset_month", currentMonth)
                    putInt("last_reset_year", currentYear)
                    apply()
                }
            }
        }
    }

    private fun checkAndResetDailyAttendance() {
        val lastAttendanceDay = sharedPreferences.getLong("last_attendance_day_timestamp", 0)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (lastAttendanceDay != today) {
            viewModelScope.launch {
                val database = AppDatabase.getDatabase(getApplication())
                database.workerDao().resetAllAttendance()
                sharedPreferences.edit().putLong("last_attendance_day_timestamp", today).apply()
            }
        }
    }

    // User operations
    suspend fun registerUser(user: User): Long {
        return repository.registerUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return repository.login(email, password)
    }

    suspend fun getUserByMobile(mobile: String): User? {
        return repository.getUserByMobile(mobile)
    }

    suspend fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    // Worker operations
    fun insertWorker(worker: Worker) = viewModelScope.launch {
        repository.insertWorker(worker)
    }

    fun updateWorker(worker: Worker) = viewModelScope.launch {
        repository.updateWorker(worker)
    }

    fun deleteWorker(worker: Worker) = viewModelScope.launch {
        repository.deleteWorker(worker)
    }

    fun updateAttendance(updatedWorkers: List<Worker>) = viewModelScope.launch {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val currentWorkers = allWorkers.first()
        
        val processedWorkers = updatedWorkers.map { updatedWorker ->
            val originalWorker = currentWorkers.find { it.id == updatedWorker.id }
            
            var newDaysWorked = updatedWorker.daysWorked
            var newLastAttendanceDate = updatedWorker.lastAttendanceDate

            if (updatedWorker.isPresentToday == true && originalWorker?.lastAttendanceDate != today) {
                newDaysWorked += 1
                newLastAttendanceDate = today
            } else if (updatedWorker.isPresentToday == false && originalWorker?.lastAttendanceDate == today) {
                newDaysWorked = (newDaysWorked - 1).coerceAtLeast(0)
                newLastAttendanceDate = 0
            }

            updatedWorker.copy(
                daysWorked = newDaysWorked,
                lastAttendanceDate = newLastAttendanceDate
            )
        }
        repository.updateAttendance(processedWorkers)
    }

    // Photo operations
    fun insertPhoto(photo: SitePhoto) = viewModelScope.launch {
        repository.insertPhoto(photo)
    }

    fun deletePhoto(photo: SitePhoto) = viewModelScope.launch {
        // Delete the file from internal storage first
        val file = File(photo.imagePath)
        if (file.exists()) {
            file.delete()
        }
        repository.deletePhoto(photo)
    }
}
