package com.example.nammamistri

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammamistri.ui.theme.OrangePrimary

@Composable
fun TopBar(language: Language, onLanguageToggle: () -> Unit, showBack: Boolean = false, onBack: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Engineering,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NAMMA-MISTRI",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = OrangePrimary,
                    letterSpacing = 0.5.sp
                )
            )
        }
        
        Button(
            onClick = onLanguageToggle,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(36.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (language == Language.ENGLISH) "Language/ಭಾಷೆ" else "ಭಾಷೆ/Language",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun BottomNav(currentScreen: Screen, onScreenSelected: (Screen) -> Unit, language: Language) {
    NavigationBar(
        containerColor = Color(0xFF121212),
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(Screen.HOME, Icons.Default.Home, if (language == Language.ENGLISH) "HOME" else "ಮನೆ"),
            Triple(Screen.CALCULATOR, Icons.Default.Calculate, if (language == Language.ENGLISH) "CALC" else "ಲೆಕ್ಕ"),
            Triple(Screen.LABOR, Icons.Default.People, if (language == Language.ENGLISH) "TEAM" else "ತಂಡ"),
            Triple(Screen.PHOTOS, Icons.Default.PhotoLibrary, if (language == Language.ENGLISH) "PHOTOS" else "ಚಿತ್ರಗಳು")
        )
        
        items.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen || (currentScreen == Screen.ADD_EDIT_WORKER && screen == Screen.LABOR) || (currentScreen == Screen.ATTENDANCE && screen == Screen.LABOR)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    selectedTextColor = OrangePrimary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun ActionCard(icon: ImageVector, title: String, desc: String, actionText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(actionText, color = OrangePrimary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

data class CalculatorInputData(val label: String, val value: String, val onValueChange: (String) -> Unit)

@Composable
fun CalculatorInputCard(title: String, inputs: List<CalculatorInputData>, dropdownLabel: String? = null, dropdownValue: String? = null, onDropdownClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = OrangePrimary)
            inputs.forEach { input ->
                Column {
                    Text(input.label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    OutlinedTextField(
                        value = input.value,
                        onValueChange = input.onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2C2C2C),
                            unfocusedContainerColor = Color(0xFF2C2C2C),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
            if (dropdownLabel != null) {
                Column {
                    Text(dropdownLabel, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Box(modifier = Modifier.fillMaxWidth().clickable { onDropdownClick() }) {
                        OutlinedTextField(
                            value = dropdownValue ?: "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = Color(0xFF2C2C2C),
                                disabledBorderColor = Color.Transparent,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultSection(language: Language, bricks: Int, cement: Int, sand: Double, total: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (language == Language.ENGLISH) "Estimated Requirements" else "ಅಂದಾಜು ಅಗತ್ಯತೆಗಳು", style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(if (language == Language.ENGLISH) "BRICKS: $bricks pcs" else "ಇಟ್ಟಿಗೆಗಳು: $bricks ಪೀಸ್", fontWeight = FontWeight.Bold)
                Text(if (language == Language.ENGLISH) "CEMENT: $cement Bags" else "ಸಿಮೆಂಟ್: $cement ಬ್ಯಾಗ್", fontWeight = FontWeight.Bold)
                Text(if (language == Language.ENGLISH) "SAND: ${"%.1f".format(sand)} cft" else "ಮರಳು: ${"%.1f".format(sand)} ಸಿಎಫ್‌ಟಿ", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(if (language == Language.ENGLISH) "TOTAL: ₹${"%,.0f".format(total)}" else "ಒಟ್ಟು: ₹${"%,.0f".format(total)}", style = MaterialTheme.typography.titleLarge, color = OrangePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
