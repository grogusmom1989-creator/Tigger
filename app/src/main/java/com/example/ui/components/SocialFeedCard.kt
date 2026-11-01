package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.SocialFeedItem
import com.example.ui.theme.*

@Composable
fun SocialFeedCard(
    item: SocialFeedItem,
    onLikeClick: () -> Unit,
    onDropClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.authorName, fontWeight = FontWeight.Bold, color = KikiPearl)
                Text(text = item.timestamp, style = MaterialTheme.typography.bodySmall, color = KikiTextMuted)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.content, style = MaterialTheme.typography.bodyMedium, color = KikiPearl)
            
            item.imageUrl?.let { url ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (item.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (item.isLiked) Color.Red else KikiTextMuted
                    )
                }
                Text(text = "${item.likeCount}", color = KikiTextMuted)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                item.dropId?.let { dropId ->
                    TextButton(onClick = { onDropClick(dropId) }) {
                        Text("View Drop", color = KikiLilac)
                    }
                }
            }
        }
    }
}
