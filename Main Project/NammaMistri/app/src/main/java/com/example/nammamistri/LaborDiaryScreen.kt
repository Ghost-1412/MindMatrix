package com.example.nammamistri

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Person
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
fun LaborDiaryScreen(
    language: Language,
    workers: List<Worker>,
    onEditWorker: (Worker) -> Unit,
    onNavigateToAttendance: () -> Unit
) {
    val totalBalance = workers.sumOf { it.balanceDue }
    var selectedWorkerForStatus by remember { mutableStateOf<Worker?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Attendance Status Header (Shows when a worker is clicked)
        selectedWorkerForStatus?.let { worker ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = OrangePrimary.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (worker.isPresentToday == true) Icons.Default.CheckCircle else if (worker.isPresentToday == false) Icons.Default.Cancel else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (worker.isPresentToday == true) Color.Green else if (worker.isPresentToday == false) Color.Red else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${worker.name}: " + when(worker.isPresentToday) {
                                true -> if (language == Language.ENGLISH) "PRESENT TODAY" else "ಇಂದು ಹಾಜರಿದ್ದಾರೆ"
                                false -> if (language == Language.ENGLISH) "ABSENT TODAY" else "ಇಂದು ಗೈರುಹಾಜರಾಗಿದ್ದಾರೆ"
                                else -> if (language == Language.ENGLISH) "Attendance not marked" else "ಹಾಜರಾತಿ ಗುರುತಿಸಲಾಗಿಲ್ಲ"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = { selectedWorkerForStatus = null }) {
                        Icon(Icons.Default.Cancel, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(if (language == Language.ENGLISH) "BALANCE DUE (TOTAL)" else "ಒಟ್ಟು ಬಾಕಿ ಇರುವ ಹಣ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("₹${"%,.0f".format(totalBalance)}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
            }
        }

        // Attendance Button
        Button(
            onClick = onNavigateToAttendance,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OrangePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (language == Language.ENGLISH) "TAKE TODAY'S ATTENDANCE" else "ಇಂದಿನ ಹಾಜರಾತಿ ತೆಗೆದುಕೊಳ್ಳಿ")
        }

        Text(if (language == Language.ENGLISH) "Team Directory" else "ತಂಡದ ವಿವರ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(workers) { worker ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedWorkerForStatus = worker },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(worker.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text("DUE: ₹${"%,.0f".format(worker.balanceDue)}", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { onEditWorker(worker) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Edit", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
