package com.example.nammamistri

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository
    val allWorkers: Flow<List<Worker>>
    val allPhotos: Flow<List<SitePhoto>>
    private val sharedPreferences = application.getSharedPreferences("namma_mistri_prefs", Context.MODE_PRIVATE)

    private val _attendanceForSelectedDate = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceForSelectedDate: StateFlow<List<AttendanceRecord>> = _attendanceForSelectedDate.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.userDao(), database.workerDao(), database.sitePhotoDao(), database.attendanceDao())
        allWorkers = repository.allWorkers
        allPhotos = repository.allPhotos
        
        checkAndResetMonthlyData()
        checkAndResetDailyAttendance()
    }

    private fun checkAndResetMonthlyData() {
        val lastResetMonth = sharedPreferences.getInt("last_reset_month", -1)
        val lastResetYear = sharedPreferences.getInt("last_reset_year", -1)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        if (lastResetMonth != currentMonth || lastResetYear != currentYear) {
            viewModelScope.launch {
                repository.resetMonthlyData()
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
        val today = getStartOfDay(System.currentTimeMillis())

        if (lastAttendanceDay != today) {
            viewModelScope.launch {
                repository.resetDailyAttendance()
                sharedPreferences.edit().putLong("last_attendance_day_timestamp", today).apply()
            }
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun loadAttendanceForDate(timestamp: Long) = viewModelScope.launch {
        val date = getStartOfDay(timestamp)
        repository.getAttendanceForAllWorkers(date).collectLatest {
            _attendanceForSelectedDate.value = it
        }
    }

    // User operations
    suspend fun registerUser(user: User): Long = repository.registerUser(user)
    suspend fun login(email: String, password: String): User? = repository.login(email, password)
    suspend fun getUserByMobile(mobile: String): User? = repository.getUserByMobile(mobile)
    suspend fun updateUser(user: User) = viewModelScope.launch { repository.updateUser(user) }

    // Worker operations
    fun insertWorker(worker: Worker) = viewModelScope.launch { repository.insertWorker(worker) }
    fun updateWorker(worker: Worker) = viewModelScope.launch { repository.updateWorker(worker) }
    fun deleteWorker(worker: Worker) = viewModelScope.launch { repository.deleteWorker(worker) }

    fun updateAttendance(worker: Worker, timestamp: Long, isPresent: Boolean?, dailyWage: Double) = viewModelScope.launch {
        val date = getStartOfDay(timestamp)
        val existingRecord = repository.getAttendance(worker.id, date)
        
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = date
        val recordMonth = calendar.get(Calendar.MONTH)
        val recordYear = calendar.get(Calendar.YEAR)
        
        val isCurrentMonth = currentMonth == recordMonth && currentYear == recordYear

        // Logic to update worker's monthly stats if record is in current month
        if (isCurrentMonth) {
            var newMonthlyEarnings = worker.monthlyTotalEarnings
            var newDaysWorked = worker.daysWorked

            // Revert old record impact
            if (existingRecord != null && existingRecord.isPresent) {
                newMonthlyEarnings -= existingRecord.dailyWage
                newDaysWorked -= 1
            }

            // Apply new record impact
            if (isPresent == true) {
                newMonthlyEarnings += dailyWage
                newDaysWorked += 1
            }

            val today = getStartOfDay(System.currentTimeMillis())
            val updatedWorker = worker.copy(
                monthlyTotalEarnings = newMonthlyEarnings,
                daysWorked = newDaysWorked,
                isPresentToday = if (date == today) isPresent else worker.isPresentToday,
                lastAttendanceDate = if (date == today && isPresent == true) today else worker.lastAttendanceDate
            )
            repository.updateWorker(updatedWorker)
        }

        // Update attendance record
        if (isPresent == null) {
            existingRecord?.let { repository.deleteAttendance(it) }
        } else {
            repository.insertOrUpdateAttendance(AttendanceRecord(worker.id, date, isPresent, dailyWage))
        }
    }

    // Photo operations
    fun insertPhoto(photo: SitePhoto) = viewModelScope.launch { repository.insertPhoto(photo) }
    fun deletePhoto(photo: SitePhoto) = viewModelScope.launch { repository.deletePhoto(photo) }
}
