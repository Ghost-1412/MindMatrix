package com.example.nammamistri

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Calendar

enum class Language {
    ENGLISH, KANNADA
}

enum class Screen {
    LOGIN, REGISTER, FORGOT_PASSWORD, RESET_PASSWORD, HOME, CALCULATOR, LABOR, PHOTOS, ADD_EDIT_WORKER, ATTENDANCE, CALENDAR
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val password: String,
    val mobile: String
)

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val role: String = "MASON",
    val dailyWage: Double = 0.0,
    val daysWorked: Int = 0,
    val advancePaid: Double = 0.0,
    val isPresentToday: Boolean? = null, // null: not marked, true: present, false: absent
    val lastAttendanceDate: Long = 0, // To prevent double counting in a single day
    val monthlyTotalEarnings: Double = 0.0 // Track earnings based on daily wages
) {
    val totalEarnings: Double get() = monthlyTotalEarnings
    val balanceDue: Double get() = totalEarnings - advancePaid
}

@Entity(tableName = "attendance_records", primaryKeys = ["workerId", "date"])
data class AttendanceRecord(
    val workerId: Long,
    val date: Long, // Start of the day timestamp
    val isPresent: Boolean,
    val dailyWage: Double
)

@Entity(tableName = "site_photos")
data class SitePhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String = "Progress Image",
    val imagePath: String, // Path to the image in internal storage
    val dateAdded: Long = System.currentTimeMillis() // Store as Long for Room
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        }
    }

    @TypeConverter
    fun dateToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }
}
