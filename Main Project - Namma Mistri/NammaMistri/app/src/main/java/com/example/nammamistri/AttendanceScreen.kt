package com.example.nammamistri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammamistri.ui.theme.OrangePrimary

@Composable
fun AttendanceScreen(
    language: Language,
    workers: List<Worker>,
    onUpdateAttendance: (List<Worker>) -> Unit
) {
    var attendanceList by remember { mutableStateOf(workers) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = if (language == Language.ENGLISH) "Daily Attendance" else "ದೈನಂದಿನ ಹಾಜರಾತಿ",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(attendanceList) { worker ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(worker.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(worker.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Present Button
                            IconButton(
                                onClick = {
                                    attendanceList = attendanceList.map { 
                                        if (it.id == worker.id) it.copy(isPresentToday = true) else it 
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (worker.isPresentToday == true) Color.Green else Color(0xFF2C2C2C))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Present", tint = if (worker.isPresentToday == true) Color.Black else Color.Gray)
                            }

                            // Absent Button
                            IconButton(
                                onClick = {
                                    attendanceList = attendanceList.map { 
                                        if (it.id == worker.id) it.copy(isPresentToday = false) else it 
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (worker.isPresentToday == false) Color.Red else Color(0xFF2C2C2C))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Absent", tint = if (worker.isPresentToday == false) Color.Black else Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onUpdateAttendance(attendanceList) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (language == Language.ENGLISH) "SUBMIT ATTENDANCE" else "ಹಾಜರಾತಿ ಸಲ್ಲಿಸಿ", fontWeight = FontWeight.Bold)
        }
    }
}
