package com.example.nammamistri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammamistri.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SitePhotosScreen(
    language: Language,
    photos: List<SitePhoto>,
    onAddPhoto: (SitePhoto) -> Unit
) {
    // Group photos by Month
    val groupedPhotos = photos.groupBy { photo ->
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthFormat.format(Date(photo.dateAdded))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (language == Language.ENGLISH) "Site Progress Gallery" else "ಸೈಟ್ ಪ್ರಗತಿ ಗ್ಯಾಲರಿ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Button(
                onClick = {
                    onAddPhoto(SitePhoto(label = "New Progress Update", dateAdded = System.currentTimeMillis()))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (language == Language.ENGLISH) "Upload Progress Photo" else "ಪ್ರಗತಿ ಫೋಟೋ ಅಪ್‌ಲೋಡ್ ಮಾಡಿ")
            }
        }

        // Sectioned Photo Preview
        groupedPhotos.forEach { (month, monthPhotos) ->
            item {
                Text(
                    text = month.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = OrangePrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.Gray)
            }

            items(monthPhotos.chunked(2)) { rowPhotos ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowPhotos.forEach { photo ->
                        PhotoCard(photo, Modifier.weight(1f))
                    }
                    if (rowPhotos.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoCard(photo: SitePhoto, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Gray)
            }
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(photo.label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                    Text(dateFormat.format(Date(photo.dateAdded)), color = Color.LightGray, fontSize = 8.sp)
                }
            }
        }
    }
}
