package com.example.nammamistri

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nammamistri.ui.theme.OrangePrimary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SitePhotosScreen(
    language: Language,
    photos: List<SitePhoto>,
    onAddPhoto: (SitePhoto) -> Unit,
    onDeletePhoto: (SitePhoto) -> Unit
) {
    val context = LocalContext.current
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val path = saveImageToInternalStorage(context, it)
                if (path != null) {
                    onAddPhoto(
                        SitePhoto(
                            label = if (language == Language.ENGLISH) "Progress Update" else "ಪ್ರಗತಿ ಅಪ್‌ಡೇಟ್",
                            imagePath = path,
                            dateAdded = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    )

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
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
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
                        PhotoCard(photo, onDeletePhoto, Modifier.weight(1f))
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
fun PhotoCard(photo: SitePhoto, onDelete: (SitePhoto) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            AsyncImage(
                model = photo.imagePath,
                contentDescription = photo.label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = null // Or a placeholder
            )
            
            // Delete Button at top right
            IconButton(
                onClick = { onDelete(photo) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
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

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "site_photo_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
