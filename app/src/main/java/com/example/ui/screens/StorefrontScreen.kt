package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import com.example.data.CustomCreation
import com.example.ui.components.MetaMuseExtractorDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MuseStoreProduct
import com.example.ui.components.HoloBadge
import com.example.ui.components.parseHexColor
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiMagenta
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

val defaultMuseStoreProducts = listOf(
    MuseStoreProduct(
        id = "muse_prod_01",
        title = "Astral Champagne Beam",
        category = "Lip Oil & Gloss",
        priceUsd = 28.00,
        sparksPrice = 450,
        hexColor = "#F3E5AB",
        secondaryHex = "#00F5D4",
        finish = "Prismatic Holo Glaze",
        description = "Co-created with Meta Muse runway trend signals. Infused with plant-derived squalane and golden diamond pearls.",
        stockUnits = 42,
        tags = listOf("Meta Muse Trend", "Runway Drop", "Google Drive Backed")
    ),
    MuseStoreProduct(
        id = "muse_prod_02",
        title = "Cosmic Peach Glaze",
        category = "Glow Stick",
        priceUsd = 32.00,
        sparksPrice = 520,
        hexColor = "#FFB3BA",
        secondaryHex = "#FF729F",
        finish = "Mirror Gloss",
        description = "A warm radiant peach highlighter stick synced directly from the Gemini Color Intelligence formulation.",
        stockUnits = 18,
        tags = listOf("Gemini AI Formulated", "Best Seller")
    ),
    MuseStoreProduct(
        id = "muse_prod_03",
        title = "Siren Scales Chrome",
        category = "Peel Lacquer",
        priceUsd = 22.00,
        sparksPrice = 380,
        hexColor = "#00F5D4",
        secondaryHex = "#8A4FFF",
        finish = "Liquid Chrome",
        description = "Peelable metallic nail lacquer featuring duo-chrome shifting hues inspired by aquatic cyberpunk aesthetics.",
        stockUnits = 29,
        tags = listOf("Peelable", "No Acetone Needed", "Zero Toxins")
    ),
    MuseStoreProduct(
        id = "muse_prod_04",
        title = "Meta Muse Discovery Sample Kit",
        category = "Discovery Kit",
        priceUsd = 18.00,
        sparksPrice = 250,
        hexColor = "#B388FF",
        secondaryHex = "#FEE440",
        finish = "Multi-Finish Discovery (4 Vials)",
        description = "4 mini lab vials containing fresh lab-blended samples from the latest Meta ⇄ Google collaborative drops. No invite code required.",
        stockUnits = 65,
        tags = listOf("Sample Kit", "Free Shipping", "Zero Code Required"),
        isSampleKit = true
    ),
    MuseStoreProduct(
        id = "muse_prod_05",
        title = "Velvet Nebula Mist",
        category = "Velvet Mist",
        priceUsd = 36.00,
        sparksPrice = 600,
        hexColor = "#7B2CBF",
        secondaryHex = "#FF007F",
        finish = "Cloud Matte Scent",
        description = "Hydrating botanical mood mist with notes of white musk, cyber peony, and celestial amber.",
        stockUnits = 15,
        tags = listOf("Aromatherapy", "Meta Muse Scent")
    )
)

@Composable
fun StorefrontScreen(
    userSparksBalance: Int = 1850,
    onPurchaseSuccess: (MuseStoreProduct, Int) -> Unit = { _, _ -> },
    onFormulasExtracted: (List<CustomCreation>) -> Unit = {},
    onOpenVault: () -> Unit = {},
    onOpenDrive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Lip Oil & Gloss", "Glow Stick", "Peel Lacquer", "Discovery Kit", "Velvet Mist")
    var selectedProductForCheckout by remember { mutableStateOf<MuseStoreProduct?>(null) }
    var bridgeSyncing by remember { mutableStateOf(false) }
    var bridgeLatency by remember { mutableStateOf(18) }
    var showBridgeInfoDialog by remember { mutableStateOf(false) }
    var showExtractorDialog by remember { mutableStateOf(false) }

    val filteredProducts = remember(selectedCategory) {
        if (selectedCategory == "All") defaultMuseStoreProducts
        else defaultMuseStoreProducts.filter { it.category == selectedCategory }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikiDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meta Muse ⇄ Google Bridge Status Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .testTag("muse_bridge_status_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(HolographicGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Hub,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Meta Muse ⇄ Google Bridge",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = KikiPearl
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (bridgeSyncing) KikiGold else KikiCyan)
                                        )
                                    }
                                    Text(
                                        text = if (bridgeSyncing) "Syncing Meta Muse to Drive..." else "Live • ${bridgeLatency}ms • Zero Code Required",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (bridgeSyncing) KikiGold else KikiCyan
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    bridgeSyncing = true
                                    bridgeLatency = (14..24).random()
                                    bridgeSyncing = false
                                },
                                modifier = Modifier.testTag("refresh_bridge_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync Bridge",
                                    tint = KikiPearl
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Direct automated bridge connects Meta Muse trends, Google Drive ZIP archives, and Gemini AI formula reasoning without needing any access codes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HoloBadge(text = "Autonomous Sync Enabled")
                            OutlinedButton(
                                onClick = { showBridgeInfoDialog = true },
                                modifier = Modifier.height(34.dp).testTag("bridge_details_button"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = KikiLilac, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Connector Info", fontSize = 11.sp, color = KikiPearl)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showExtractorDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("pull_everything_meta_muse_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pull Everything Out of Meta Muse",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("storefront_category_row")
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KikiLilac,
                                selectedLabelColor = Color.Black,
                                containerColor = KikiSurfaceDark,
                                labelColor = KikiPearl
                            ),
                            modifier = Modifier.testTag("store_category_$cat")
                        )
                    }
                }
            }

            // Product Cards List
            items(filteredProducts, key = { it.id }) { product ->
                StoreProductCard(
                    product = product,
                    userSparks = userSparksBalance,
                    onOrderClick = { selectedProductForCheckout = product }
                )
            }
        }
    }

    // Checkout Confirmation Dialog
    if (selectedProductForCheckout != null) {
        val prod = selectedProductForCheckout!!
        var checkoutSuccess by remember { mutableStateOf(false) }
        var payWithSparks by remember { mutableStateOf(userSparksBalance >= prod.sparksPrice) }

        Dialog(onDismissRequest = { selectedProductForCheckout = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = KikiSurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp))
                    .testTag("checkout_dialog")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (checkoutSuccess) "Order Confirmed!" else "Prototype Dispatch",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        IconButton(onClick = { selectedProductForCheckout = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = KikiTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!checkoutSuccess) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(parseHexColor(prod.hexColor))
                                    .border(2.dp, parseHexColor(prod.secondaryHex), RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(prod.title, style = MaterialTheme.typography.titleMedium, color = KikiPearl, fontWeight = FontWeight.Bold)
                                Text(prod.finish, style = MaterialTheme.typography.bodySmall, color = KikiLilac)
                                Text(prod.category, style = MaterialTheme.typography.bodySmall, color = KikiTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Payment Method:",
                            style = MaterialTheme.typography.labelMedium,
                            color = KikiPearl
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { payWithSparks = true },
                                modifier = Modifier.weight(1f).testTag("pay_sparks_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (payWithSparks) KikiGold else KikiSurfaceElevated
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${prod.sparksPrice} Sparks",
                                        fontWeight = FontWeight.Bold,
                                        color = if (payWithSparks) Color.Black else KikiPearl
                                    )
                                    Text(
                                        "Balance: $userSparksBalance",
                                        fontSize = 10.sp,
                                        color = if (payWithSparks) Color.Black.copy(alpha = 0.7f) else KikiTextMuted
                                    )
                                }
                            }

                            Button(
                                onClick = { payWithSparks = false },
                                modifier = Modifier.weight(1f).testTag("pay_card_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!payWithSparks) KikiCyan else KikiSurfaceElevated
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$${String.format("%.2f", prod.priceUsd)}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (!payWithSparks) Color.Black else KikiPearl
                                    )
                                    Text(
                                        "Credit / GPay",
                                        fontSize = 10.sp,
                                        color = if (!payWithSparks) Color.Black.copy(alpha = 0.7f) else KikiTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = KikiSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = KikiCyan)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Free Lab Priority Shipping", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KikiPearl)
                                    Text("Dispatched directly to your verified address within 48h.", fontSize = 11.sp, color = KikiTextMuted)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                checkoutSuccess = true
                                onPurchaseSuccess(prod, if (payWithSparks) prod.sparksPrice else 0)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("confirm_checkout_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (payWithSparks) "Redeem with ${prod.sparksPrice} Sparks" else "Complete Order ($${String.format("%.2f", prod.priceUsd)})",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Success View
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KikiCyan,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Order #KW-${(1000..9999).random()} Placed!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${prod.title} is queued for lab batch formulation. Synced to your Google Drive backup archive.",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { selectedProductForCheckout = null },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Back to Storefront", color = KikiPearl)
                            }
                        }
                    }
                }
            }
        }
    }

    // Connector Info Dialog (Explains zero code needed)
    if (showBridgeInfoDialog) {
        Dialog(onDismissRequest = { showBridgeInfoDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = KikiSurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(20.dp))
                    .testTag("bridge_info_dialog")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Meta Muse ⇄ Google Bridge Architecture",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• No Code Needed: Connection is pre-wired to bridge Meta Muse trends directly into Google Drive ZIP files.\n\n" +
                                "• Gemini AI Reasoning: Trends are translated into shade formulas, finish types, and squalane ratios automatically.\n\n" +
                                "• Backup on Demand: Say 'backup' to your AI Copilot anytime to dump all Muse assets into your Google Drive master archive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showBridgeInfoDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Understood", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showExtractorDialog) {
        MetaMuseExtractorDialog(
            onDismiss = { showExtractorDialog = false },
            onFormulasExtracted = { list ->
                onFormulasExtracted(list)
            },
            onOpenVault = {
                showExtractorDialog = false
                onOpenVault()
            },
            onOpenDrive = {
                showExtractorDialog = false
                onOpenDrive()
            }
        )
    }
}

@Composable
private fun StoreProductCard(
    product: MuseStoreProduct,
    userSparks: Int,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(20.dp))
            .testTag("store_product_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(parseHexColor(product.hexColor))
                            .border(2.dp, parseHexColor(product.secondaryHex), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (product.isSampleKit) {
                            Text("📦", fontSize = 28.sp)
                        } else {
                            Text("✨", fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.finish,
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiLilac
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", product.priceUsd)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = KikiPearl
                    )
                    Text(
                        text = "or ${product.sparksPrice} Sparks",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall,
                color = KikiTextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                product.tags.take(3).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = KikiSurfaceElevated
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = KikiPearl
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.stockUnits} units left in batch",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (product.stockUnits < 20) KikiMagenta else KikiTextMuted
                )

                Button(
                    onClick = onOrderClick,
                    modifier = Modifier.height(40.dp).testTag("order_product_${product.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Order Prototype",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
