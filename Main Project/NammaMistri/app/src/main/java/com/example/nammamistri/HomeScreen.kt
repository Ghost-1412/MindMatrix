package com.example.nammamistri

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammamistri.ui.theme.OrangePrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(language: Language, workers: List<Worker>, onNavigate: (Screen) -> Unit) {
    val presentWorkers = workers.filter { it.isPresentToday == true }
    val laborCount = presentWorkers.size
    val totalCostToday = presentWorkers.sumOf { it.dailyWage }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val formattedCost = currencyFormatter.format(totalCostToday)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (language == Language.ENGLISH) "Namaste, Site Supervisor" else "ನಮಸ್ತೆ, ಸೈಟ್ ಮೇಲ್ವಿಚಾರಕರೆ",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (language == Language.ENGLISH) "Project: Prestige Sunrise Block A" else "ಯೋಜನೆ: ಪ್ರೆಸ್ಟೀಜ್ ಸನ್‌ರೈಸ್ ಬ್ಲಾಕ್ ಎ",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            if (language == Language.ENGLISH) "TODAY'S COST" else "ಇಂದಿನ ವೆಚ್ಚ",
                            formattedCost,
                            Modifier.weight(1f),
                            Color(0xFF00BFA5)
                        )
                        StatCard(
                            if (language == Language.ENGLISH) "LABOR COUNT" else "ಕಾರ್ಮಿಕರ ಸಂಖ್ಯೆ",
                            laborCount.toString(),
                            Modifier.weight(1f),
                            OrangePrimary
                        )
                    }
                }
            }
        }

        item {
            ActionCard(
                icon = Icons.Default.CalendarMonth,
                title = if (language == Language.ENGLISH) "Attendance Calendar" else "ಹಾಜರಾತಿ ಕ್ಯಾಲೆಂಡರ್",
                desc = if (language == Language.ENGLISH) "View and manage historical attendance records by date." else "ದಿನಾಂಕದ ಮೂಲಕ ಐತಿಹಾಸಿಕ ಹಾಜರಾತಿ ದಾಖಲೆಗಳನ್ನು ವೀಕ್ಷಿಸಿ ಮತ್ತು ನಿರ್ವಹಿಸಿ.",
                actionText = if (language == Language.ENGLISH) "OPEN CALENDAR →" else "ಕ್ಯಾಲೆಂಡರ್ ತೆರೆಯಿರಿ →",
                onClick = { onNavigate(Screen.CALENDAR) }
            )
        }
        
        item {
            ActionCard(
                icon = Icons.Default.Calculate,
                title = if (language == Language.ENGLISH) "Material Calculator" else "ವಸ್ತು ಕ್ಯಾಲ್ಕುಲೇಟರ್",
                desc = if (language == Language.ENGLISH) "Estimate cement, sand, and bricks for any wall or slab area." else "ಯಾವುದೇ ಗೋಡೆ ಅಥವಾ ಸ್ಲ್ಯಾಬ್ ಪ್ರದೇಶಕ್ಕೆ ಸಿಮೆಂಟ್, ಮರಳು ಮತ್ತು ಇಟ್ಟಿಗೆಗಳನ್ನು ಅಂದಾಜು ಮಾಡಿ.",
                actionText = if (language == Language.ENGLISH) "OPEN TOOL →" else "ಟೂಲ್ ತೆರೆಯಿರಿ →",
                onClick = { onNavigate(Screen.CALCULATOR) }
            )
        }
        
        item {
            ActionCard(
                icon = Icons.Default.People,
                title = if (language == Language.ENGLISH) "Labor Diary" else "ಕಾರ್ಮಿಕರ ದಿನಚರಿ",
                desc = if (language == Language.ENGLISH) "Track attendance, daily wages, and team performance logs." else "ಹಾಜರಾತಿ, ದೈನಂದಿನ ವೇತನ ಮತ್ತು ತಂಡದ ಕಾರ್ಯಕ್ಷಮತೆಯ ದಾಖಲೆಗಳನ್ನು ಟ್ರ್ಯಾಕ್ ಮಾಡಿ.",
                actionText = if (language == Language.ENGLISH) "MANAGE TEAM →" else "ತಂಡವನ್ನು ನಿರ್ವಹಿಸಿ →",
                onClick = { onNavigate(Screen.LABOR) }
            )
        }
        
        item {
            ActionCard(
                icon = Icons.Default.PhotoLibrary,
                title = if (language == Language.ENGLISH) "Site Photos" else "ಸೈಟ್ ಫೋಟೋಗಳು",
                desc = if (language == Language.ENGLISH) "Visual documentation of project progress and daily site snapshots." else "ಯೋಜನೆಯ ಪ್ರಗತಿ ಮತ್ತು ದೈನಂದಿನ ಸೈಟ್ ಸ್ನ್ಯಾಪ್‌ಶಾಟ್‌ಗಳ ದೃಶ್ಯ ದಾಖಲೀಕರಣ.",
                actionText = if (language == Language.ENGLISH) "VIEW GALLERY →" else "ಗ್ಯಾಲರಿ ವೀಕ್ಷಿಸಿ →",
                onClick = { onNavigate(Screen.PHOTOS) }
            )
        }
    }
}
