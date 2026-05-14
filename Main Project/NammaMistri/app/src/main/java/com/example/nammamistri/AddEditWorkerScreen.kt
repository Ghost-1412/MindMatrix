package com.example.nammamistri

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nammamistri.ui.theme.OrangePrimary

@Composable
fun AddEditWorkerScreen(
    language: Language,
    worker: Worker?,
    onSave: (Worker) -> Unit,
    onDelete: (Worker) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(worker?.name ?: "") }
    var wage by remember { mutableStateOf(worker?.dailyWage?.toString() ?: "") }
    var days by remember { mutableStateOf(worker?.daysWorked?.toString() ?: "0") }
    var advance by remember { mutableStateOf(worker?.advancePaid?.toString() ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && worker != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(if (language == Language.ENGLISH) "Confirm Delete" else "ಅಳಿಸುವಿಕೆಯನ್ನು ಖಚಿತಪಡಿಸಿ")
            },
            text = {
                Text(if (language == Language.ENGLISH) "Are you sure you want to delete this worker?" else "ನೀವು ಈ ಕಾರ್ಮಿಕರನ್ನು ಅಳಿಸಲು ಖಚಿತವಾಗಿ ಬಯಸುವಿರಾ?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(worker)
                    }
                ) {
                    Text(if (language == Language.ENGLISH) "Delete" else "ಅಳಿಸಿ", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(if (language == Language.ENGLISH) "Cancel" else "ರದ್ದುಗೊಳಿಸಿ")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (worker == null) (if (language == Language.ENGLISH) "Add Worker" else "ಕಾರ್ಮಿಕರನ್ನು ಸೇರಿಸಿ")
                else (if (language == Language.ENGLISH) "Edit Worker" else "ಕಾರ್ಮಿಕರ ವಿವರ ಬದಲಿಸಿ"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (worker != null) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Worker", tint = Color.Red)
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (language == Language.ENGLISH) "Name" else "ಹೆಸರು") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = wage,
            onValueChange = { wage = it },
            label = { Text(if (language == Language.ENGLISH) "Daily Wage (₹)" else "ದೈನಂದಿನ ವೇತನ (₹)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = days,
            onValueChange = { /* Non-editable */ },
            label = { Text(if (language == Language.ENGLISH) "Days Worked (Auto-calculated)" else "ಕೆಲಸ ಮಾಡಿದ ದಿನಗಳು (ಸ್ವಯಂಚಾಲಿತ)") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false
        )
        OutlinedTextField(
            value = advance,
            onValueChange = { advance = it },
            label = { Text(if (language == Language.ENGLISH) "Advance Paid (₹)" else "ಮುಂಗಡ ಪಾವತಿ (₹)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f).height(56.dp)) {
                Text(if (language == Language.ENGLISH) "Cancel" else "ರದ್ದುಗೊಳಿಸಿ")
            }
            Button(
                onClick = {
                    onSave(
                        Worker(
                            id = worker?.id ?: 0, // 0 for new worker, Room will auto-generate
                            name = name,
                            role = worker?.role ?: "MASON",
                            dailyWage = wage.toDoubleOrNull() ?: 0.0,
                            daysWorked = days.toIntOrNull() ?: 0,
                            advancePaid = advance.toDoubleOrNull() ?: 0.0,
                            isPresentToday = worker?.isPresentToday
                        )
                    )
                },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(if (language == Language.ENGLISH) "Save" else "ಉಳಿಸಿ")
            }
        }
    }
}
