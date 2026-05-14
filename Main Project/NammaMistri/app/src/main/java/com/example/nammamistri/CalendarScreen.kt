package com.example.nammamistri

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammamistri.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    language: Language,
    workers: List<Worker>,
    attendanceRecords: List<AttendanceRecord>,
    onDateSelected: (Long) -> Unit,
    onUpdateAttendance: (Worker, Long, Boolean?, Double) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedWorkerId by remember { mutableStateOf<Long?>(null) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
            override fun isSelectableYear(year: Int): Boolean {
                return year <= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    )

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val selectedWorker = workers.find { it.id == selectedWorkerId }
    val recordForSelectedWorker = attendanceRecords.find { it.workerId == selectedWorkerId }

    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate.timeInMillis)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Date Selection Header
        Card(
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (language == Language.ENGLISH) "Selected Date" else "ಆಯ್ದ ದಿನಾಂಕ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = dateFormatter.format(selectedDate.time),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                    )
                }
                Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date", tint = OrangePrimary)
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Calendar.getInstance().apply { timeInMillis = it }
                        }
                        showDatePicker = false
                        selectedWorkerId = null
                    }) {
                        Text("OK", color = OrangePrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Worker Details Section
        selectedWorker?.let { worker ->
            val status = recordForSelectedWorker?.isPresent
            var dailyWageInput by remember(worker.id, selectedDate.timeInMillis) { 
                mutableStateOf(recordForSelectedWorker?.dailyWage?.toString() ?: worker.dailyWage.toString()) 
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(OrangePrimary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(worker.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = if (language == Language.ENGLISH) "Monthly Days: ${worker.daysWorked}" else "ತಿಂಗಳ ದಿನಗಳು: ${worker.daysWorked}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    // Daily Wage Field
                    OutlinedTextField(
                        value = dailyWageInput,
                        onValueChange = { dailyWageInput = it },
                        label = { Text(if (language == Language.ENGLISH) "Daily Wage for this day" else "ಈ ದಿನದ ದೈನಂದಿನ ಕೂಲಿ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.ENGLISH) "Attendance Status" else "ಹಾಜರಾತಿ ಸ್ಥಿತಿ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Present Button
                            IconButton(
                                onClick = {
                                    val wage = dailyWageInput.toDoubleOrNull() ?: worker.dailyWage
                                    onUpdateAttendance(worker, selectedDate.timeInMillis, true, wage)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (status == true) Color.Green else Color(0xFF1E1E1E))
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Present", tint = if (status == true) Color.Black else Color.Gray, modifier = Modifier.size(20.dp))
                            }

                            // Absent Button
                            IconButton(
                                onClick = {
                                    val wage = dailyWageInput.toDoubleOrNull() ?: worker.dailyWage
                                    onUpdateAttendance(worker, selectedDate.timeInMillis, false, wage)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (status == false) Color.Red else Color(0xFF1E1E1E))
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Absent", tint = if (status == false) Color.Black else Color.Gray, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = if (language == Language.ENGLISH) "Select Worker" else "ಕಾರ್ಮಿಕರನ್ನು ಆಯ್ಕೆ ಮಾಡಿ",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(workers) { worker ->
                val isSelected = selectedWorkerId == worker.id
                val record = attendanceRecords.find { it.workerId == worker.id }
                
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedWorkerId = worker.id },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.1f) else Color(0xFF1E1E1E)
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary) else null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) OrangePrimary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(worker.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(worker.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        
                        if (record != null) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (record.isPresent) Color.Green else Color.Red)
                            )
                        }
                    }
                }
            }
        }
    }
}
