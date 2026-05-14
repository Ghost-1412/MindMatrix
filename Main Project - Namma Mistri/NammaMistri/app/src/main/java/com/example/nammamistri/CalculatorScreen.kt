package com.example.nammamistri

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammamistri.ui.theme.OrangePrimary
import kotlin.math.ceil

@Composable
fun CalculatorScreen(language: Language) {
    // Input States
    var length by remember { mutableStateOf("10") }
    var height by remember { mutableStateOf("10") }
    var thickness by remember { mutableStateOf(9.0) }
    
    var rateBricks by remember { mutableStateOf("8.5") }
    var rateCement by remember { mutableStateOf("420") }
    var rateSand by remember { mutableStateOf("60") }
    
    // Result States
    var resultBricks by remember { mutableIntStateOf(0) }
    var resultCement by remember { mutableIntStateOf(0) }
    var resultSand by remember { mutableDoubleStateOf(0.0) }
    var resultTotal by remember { mutableDoubleStateOf(0.0) }

    var showThicknessDialog by remember { mutableStateOf(false) }
    val thicknessOptions = listOf(4.5, 5.0, 7.0, 9.0)

    fun calculate() {
        val l = length.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        val rb = rateBricks.toDoubleOrNull() ?: 0.0
        val rc = rateCement.toDoubleOrNull() ?: 0.0
        val rs = rateSand.toDoubleOrNull() ?: 0.0
        
        val volumeCft = l * h * (thickness / 12.0)
        resultBricks = ceil(volumeCft * 13.5).toInt()
        resultCement = ceil(volumeCft * 0.22).toInt() 
        resultSand = volumeCft * 0.45
        resultTotal = (resultBricks * rb) + (resultCement * rc) + (resultSand * rs)
    }

    if (showThicknessDialog) {
        AlertDialog(
            onDismissRequest = { showThicknessDialog = false },
            title = { Text(if (language == Language.ENGLISH) "Select Thickness" else "ದಪ್ಪವನ್ನು ಆಯ್ಕೆಮಾಡಿ") },
            text = {
                Column {
                    thicknessOptions.forEach { option ->
                        TextButton(
                            onClick = {
                                thickness = option
                                showThicknessDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$option inches")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThicknessDialog = false }) {
                    Text(if (language == Language.ENGLISH) "Close" else "ಮುಚ್ಚು")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(if (language == Language.ENGLISH) "Material Calculator" else "ವಸ್ತು ಕ್ಯಾಲ್ಕುಲೇಟರ್", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        }
        
        item {
            CalculatorInputCard(
                title = if (language == Language.ENGLISH) "WALL DIMENSIONS" else "ಗೋಡೆಯ ಆಯಾಮಗಳು",
                inputs = listOf(
                    CalculatorInputData(if (language == Language.ENGLISH) "LENGTH (FT)" else "ಉದ್ದ (ಅಡಿ)", length) { length = it },
                    CalculatorInputData(if (language == Language.ENGLISH) "HEIGHT (FT)" else "ಎತ್ತರ (ಅಡಿ)", height) { height = it }
                ),
                dropdownLabel = if (language == Language.ENGLISH) "THICKNESS" else "ದಪ್ಪ",
                dropdownValue = "$thickness inch",
                onDropdownClick = { showThicknessDialog = true }
            )
        }
        
        item {
            CalculatorInputCard(
                title = if (language == Language.ENGLISH) "UNIT RATES" else "ದರಗಳು",
                inputs = listOf(
                    CalculatorInputData(if (language == Language.ENGLISH) "BRICK (₹/PC)" else "ಇಟ್ಟಿಗೆ (₹/ಪೀಸ್)", rateBricks) { rateBricks = it },
                    CalculatorInputData(if (language == Language.ENGLISH) "CEMENT (₹/BAG)" else "ಸಿಮೆಂಟ್ (₹/ಬ್ಯಾಗ್)", rateCement) { rateCement = it },
                    CalculatorInputData(if (language == Language.ENGLISH) "SAND (₹/CFT)" else "ಮರಳು (₹/ಸಿಎಫ್‌ಟಿ)", rateSand) { rateSand = it }
                )
            )
        }
        
        item {
            Button(
                onClick = { calculate() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (language == Language.ENGLISH) "CALCULATE ESTIMATE" else "ಅಂದಾಜು ಲೆಕ್ಕ ಹಾಕಿ", fontWeight = FontWeight.Bold)
            }
        }
        
        if (resultTotal > 0) {
            item {
                ResultSection(language, resultBricks, resultCement, resultSand, resultTotal)
            }
        }
    }
}
