package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CommunityFormula
import com.example.data.DropCampaign
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.example.viewmodel.LabEditorState

@Composable
fun DropDetailDialog(
    campaign: DropCampaign,
    onDismiss: () -> Unit,
    onVoteOption: (campaignId: String, optionId: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HoloBadge(text = campaign.dropNumber)
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = KikiTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = campaign.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = KikiPearl
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = campaign.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KikiCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = campaign.fullDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KikiTextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "VOTE ON OPTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = KikiLilac
                )

                Spacer(modifier = Modifier.height(10.dp))

                campaign.options.forEach { option ->
                    VotingOptionCard(
                        option = option,
                        totalVotesInCampaign = campaign.totalVotes,
                        isUserVote = campaign.userVotedOptionId == option.id,
                        onVote = { onVoteOption(campaign.id, option.id) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", color = KikiPearl, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun FormulaDetailDialog(
    formula: CommunityFormula,
    onDismiss: () -> Unit,
    onRemix: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HoloBadge(text = formula.category.displayName)
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = KikiTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                InteractiveLiquidSwatch(
                    category = formula.category,
                    primaryHex = formula.hexColor,
                    secondaryHex = formula.secondaryHex,
                    finish = formula.finish,
                    shimmerIntensity = 0.8f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = formula.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = KikiPearl
                )

                Text(
                    text = "Crafted by ${formula.creatorName} (${formula.creatorHandle})",
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HoloBadge(text = "Finish: ${formula.finish.displayName}")
                    HoloBadge(text = "Scent: ${formula.scentNote}")
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Community Tags",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiCyan
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    formula.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KikiSurfaceElevated
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiPearl,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRemix,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dialog_remix_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Remix In Beauty Lab (+75 Sparks)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PrototypeOrderDialog(
    labState: LabEditorState,
    userSparks: Int,
    onDismiss: () -> Unit,
    onConfirmOrder: (engravingText: String) -> Unit
) {
    var engravingText by remember { mutableStateOf("KIKI • ALCHEMIST") }
    var selectedShipping by remember { mutableStateOf("Standard Studio Lab (3-5 Days)") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HoloBadge(text = "PILOT LAB PROTOTYPE", containerColor = KikiSurfaceElevated)
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = KikiTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Order 5ml Sample Vial",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = KikiPearl
                )

                Text(
                    text = "A fresh, individually formulated tester batch prepared in our cleanroom.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Engraving on Bottle Cap
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KikiGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Custom Laser Cap Engraving",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = engravingText,
                            onValueChange = { if (it.length <= 18) engravingText = it },
                            placeholder = { Text("Your Name or Monogram (Max 18)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = KikiCyan,
                                unfocusedBorderColor = KikiSurfaceBorder,
                                focusedTextColor = KikiPearl,
                                unfocusedTextColor = KikiPearl
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Cap preview
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A263D)
                        ) {
                            Text(
                                text = "Preview: [ $engravingText ]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = KikiCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pricing & Sparks Redemption
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sample Formulation", color = KikiTextMuted, style = MaterialTheme.typography.bodyMedium)
                    Text("500 SPARKS", color = KikiGold, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Laser Engraving", color = KikiTextMuted, style = MaterialTheme.typography.bodyMedium)
                    Text("FREE (Tier 3)", color = KikiCyan, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onConfirmOrder(engravingText) },
                    enabled = userSparks >= 500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_prototype_order_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (userSparks >= 500) "Confirm Order (500 Sparks)" else "Need 500 Sparks",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun IngredientGlossaryDialog(
    ingredients: List<com.example.data.IngredientInfo>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HoloBadge(text = "CLEAN BEAUTY SCIENCE", containerColor = KikiSurfaceElevated)
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = KikiTextMuted
                        )
                    }
                }

                Text(
                    text = "Ingredient Glossary",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = KikiPearl
                )

                Text(
                    text = "All KIKI formulations adhere to strict clean-beauty standards: 100% vegan, cruelty-free, and EWG 1-rated actives.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                ingredients.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = KikiSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                HoloBadge(text = item.safetyScore, containerColor = KikiDarkBg, textColor = KikiCyan)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Category: ${item.category}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiLilac
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.benefit,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = KikiGold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got It", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
