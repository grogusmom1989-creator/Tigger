package com.example.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.PlayArrow

enum class ServiceCategory {
    AI_SERVICE,
    APP_PORTAL,
    CONTENT_PORTAL
}

data class AIHubService(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val icon: ImageVector,
    val category: ServiceCategory = ServiceCategory.AI_SERVICE
)

object AIHubServices {
    val services = listOf(
        AIHubService(
            id = "meta_muse",
            name = "Meta Muse",
            description = "AI trend curation & avant-garde vision.",
            url = "https://example.com/meta-muse", // Placeholder
            icon = Icons.Default.AutoAwesome,
            category = ServiceCategory.AI_SERVICE
        ),
        AIHubService(
            id = "shade_synth",
            name = "Shade Synth",
            description = "Intelligent pigment & formula blending.",
            url = "https://example.com/shade-synth", // Placeholder
            icon = Icons.Default.Palette,
            category = ServiceCategory.AI_SERVICE
        ),
        AIHubService(
            id = "mind_mesh",
            name = "Mind Mesh",
            description = "Collaborative creative ideation.",
            url = "https://example.com/mind-mesh", // Placeholder
            icon = Icons.Default.Psychology,
            category = ServiceCategory.AI_SERVICE
        ),
        AIHubService(
            id = "vision_lab",
            name = "Vision Lab",
            description = "Advanced visual asset generation.",
            url = "https://example.com/vision-lab", // Placeholder
            icon = Icons.Default.Brush,
            category = ServiceCategory.AI_SERVICE
        ),
        AIHubService(
            id = "reiki_guide",
            name = "Reiki Guide",
            description = "Personalized energy balancing & healing intent.",
            url = "https://example.com/reiki-guide", // Placeholder
            icon = Icons.Default.Psychology,
            category = ServiceCategory.AI_SERVICE
        ),
        AIHubService(
            id = "neocities_pwa",
            name = "Neocities Portal",
            description = "Manage your PWA site.",
            url = "https://queen1989.neocities.org/",
            icon = Icons.Default.Web,
            category = ServiceCategory.CONTENT_PORTAL
        ),
        AIHubService(
            id = "games_portal",
            name = "Games Portal",
            description = "Access all your favorite interactive games.",
            url = "about:blank", // Placeholder as it's a native feature
            icon = Icons.Default.PlayArrow,
            category = ServiceCategory.APP_PORTAL
        )
    )
}
