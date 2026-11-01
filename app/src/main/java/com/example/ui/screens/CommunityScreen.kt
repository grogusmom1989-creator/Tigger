package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Challenge
import com.example.data.CommunityFormula
import com.example.data.ProductCategory
import com.example.ui.components.CommunityFormulaCard
import com.example.ui.components.HoloBadge
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiTextMuted
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

@Composable
fun CommunityScreen(
    formulas: List<CommunityFormula>,
    challenges: List<Challenge>,
    categoryFilter: ProductCategory?,
    searchQuery: String,
    onCategoryFilterChanged: (ProductCategory?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRemixFormula: (CommunityFormula) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenFormulaDetail: (CommunityFormula) -> Unit,
    spotlightEntry: com.example.data.SpotlightEntry? = null,
    modifier: Modifier = Modifier
) {
    val filteredFormulas = remember(formulas, categoryFilter, searchQuery) {
        formulas.filter { f ->
            val matchCategory = categoryFilter == null || f.category == categoryFilter
            val matchSearch = searchQuery.isBlank() ||
                f.name.contains(searchQuery, ignoreCase = true) ||
                f.creatorName.contains(searchQuery, ignoreCase = true) ||
                f.creatorHandle.contains(searchQuery, ignoreCase = true) ||
                f.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchCategory && matchSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Community Feed Header
        item {
            Column {
                if (spotlightEntry != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = KikiLilac)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("SPOTLIGHT", style = MaterialTheme.typography.labelLarge, color = Color.Black)
                            Text("Featured: ${spotlightEntry.creationId}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                            Text(spotlightEntry.curatorNote, style = MaterialTheme.typography.bodySmall, color = Color.Black)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMMUNITY FEED",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = KikiPearl
                    )
                    HoloBadge(text = "${filteredFormulas.size} RECIPES")
                }
                Text(
                    text = "Explore & remix creations submitted by the global KIKI community.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KikiTextMuted
                )
            }
        }

        // Active Challenges
        if (challenges.isNotEmpty()) {
            item {
                Text("ACTIVE CHALLENGES", style = MaterialTheme.typography.titleSmall, color = KikiLilac, modifier = Modifier.padding(top = 8.dp))
            }
            items(challenges) { challenge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(challenge.title, style = MaterialTheme.typography.titleMedium, color = KikiPearl)
                        Text(challenge.description, style = MaterialTheme.typography.bodySmall, color = KikiTextMuted)
                        Text("Reward: ${challenge.rewardSparks} Sparks", style = MaterialTheme.typography.labelSmall, color = KikiLilac)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search shades, finishes, tags...", color = KikiTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = KikiTextMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = KikiTextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_community_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KikiLilac,
                    unfocusedBorderColor = KikiSurfaceBorder,
                    focusedTextColor = KikiPearl,
                    unfocusedTextColor = KikiPearl,
                    focusedContainerColor = KikiSurfaceDark,
                    unfocusedContainerColor = KikiSurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = categoryFilter == null,
                        onClick = { onCategoryFilterChanged(null) },
                        label = { Text("All Categories") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiLilac,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceDark,
                            labelColor = KikiPearl
                        )
                    )
                }
                items(ProductCategory.values()) { category ->
                    val isSelected = categoryFilter == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChanged(category) },
                        label = { Text("${category.icon} ${category.displayName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiLilac,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceDark,
                            labelColor = KikiPearl
                        )
                    )
                }
            }
        }

        // Community Formula Cards
        if (filteredFormulas.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No formulas found matching your filter",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KikiTextMuted
                    )
                }
            }
        } else {
            items(filteredFormulas) { formula ->
                CommunityFormulaCard(
                    formula = formula,
                    onRemix = { onRemixFormula(formula) },
                    onToggleLike = { onToggleLike(formula.id) },
                    onOpenDetail = { onOpenFormulaDetail(formula) }
                )
            }
        }
    }
}
