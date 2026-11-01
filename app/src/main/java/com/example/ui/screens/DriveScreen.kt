package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DriveFileItem
import com.example.data.DriveFileType
import com.example.data.MuseZipEntry
import com.example.ui.components.parseHexColor
import com.example.ui.theme.HolographicGradient
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
import com.example.viewmodel.KikiDriveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveScreen(
    onNavigateToLab: () -> Unit = {},
    onNavigateToVault: () -> Unit = {},
    viewModel: KikiDriveViewModel = viewModel()
) {
    val files by viewModel.driveFiles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPullingAll by viewModel.isPullingAll.collectAsState()
    val isExportingZip by viewModel.isExportingZip.collectAsState()
    val pullProgressText by viewModel.pullProgressText.collectAsState()
    val pullSummary by viewModel.pullSummary.collectAsState()
    val selectedArchiveForInspect by viewModel.selectedArchiveForInspect.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val currentPath by viewModel.currentPath.collectAsState()
    val userCreations by viewModel.userCreations.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    val filterOptions = listOf("All", "AI Studio & Muse ZIPs (📦)", "Formulas & Sheets", "Moodboards", "Docs & Specs")

    val filteredFiles = remember(files, searchQuery, selectedFilter) {
        files.filter { file ->
            val matchesSearch = searchQuery.isBlank() ||
                file.name.contains(searchQuery, ignoreCase = true) ||
                file.categoryTag.contains(searchQuery, ignoreCase = true) ||
                file.archiveEntries.any { it.name.contains(searchQuery, ignoreCase = true) || (it.formulaName?.contains(searchQuery, ignoreCase = true) == true) }

            val matchesFilter = when (selectedFilter) {
                "AI Studio & Muse ZIPs (📦)" -> file.fileType == DriveFileType.ARCHIVE || file.isArchive
                "Formulas & Sheets" -> file.fileType == DriveFileType.SPREADSHEET || file.fileType == DriveFileType.DATA
                "Moodboards" -> file.fileType == DriveFileType.IMAGE
                "Docs & Specs" -> file.fileType == DriveFileType.DOCUMENT || file.fileType == DriveFileType.OTHER
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val totalZipArchives = remember(files) { files.count { it.fileType == DriveFileType.ARCHIVE || it.isArchive } }
    val totalImported = remember(files) { files.count { it.isImported } }

    Scaffold(
        containerColor = KikiDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with OAuth Status & Drive Branding
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KikiLilac.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Google Drive",
                                    tint = KikiLilac,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Google Drive",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                        }
                        Text(
                            text = "AI Studio & Muse Backups • OAuth Connected",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan,
                            modifier = Modifier.padding(start = 44.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1B5E20).copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OAuth Linked",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF81C784)
                            )
                        }
                    }
                }
            }

            // Universal Flow Music & All Google Apps Importer ("Import Everything From Everywhere")
            item {
                com.example.ui.components.FlowMusicGoogleImporterSection(
                    onTrackImportedToJukebox = { _ -> },
                    onBlendImportedTrackInLab = { _ ->
                        onNavigateToLab()
                    },
                    onImportAllEverywhere = { _ ->
                        viewModel.pullEverything()
                    }
                )
            }

            // AI Studio ZIP Backup Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, HolographicGradient, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(KikiGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Archive, contentDescription = null, tint = KikiGold, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Studio ZIP Backup",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "Back up all formulas, Meta Muse trends, community drops & profile into a .zip archive in Drive.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.createAIStudioBackupZip() },
                                enabled = !isExportingZip,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("backup_ai_studio_zip_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KikiGold)
                            ) {
                                if (isExportingZip) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Packaging ZIP...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Backup AI Studio (.zip)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.exportVaultToMuseZip() },
                                enabled = !isExportingZip,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("export_muse_zip_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KikiCyan)
                            ) {
                                Icon(Icons.Default.Archive, contentDescription = null, tint = KikiCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Vault ZIP", color = KikiCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Pull Everything Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pull & Unpack Everything",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Automatically unpacks all Muse & AI Studio ZIP archives directly into KIKI Lab & Vault.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiTextMuted
                                )
                            }
                            IconButton(
                                onClick = { viewModel.loadDriveFiles() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Drive Files",
                                    tint = KikiLilac
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Pull Everything Button
                        Button(
                            onClick = { viewModel.pullEverything() },
                            enabled = !isPullingAll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("pull_everything_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = KikiSurfaceElevated
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (isPullingAll) Brush.horizontalGradient(listOf(KikiSurfaceElevated, KikiSurfaceDark))
                                        else HolographicGradient
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPullingAll) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = KikiLilac
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = pullProgressText,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = KikiPearl,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Unarchive,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Unpack & Pull All Drive Files",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        if (isPullingAll) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = KikiLilac,
                                trackColor = KikiSurfaceBorder
                            )
                        }

                        val unimportedCount = remember(files) { files.count { !it.isImported } }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.pullEverythingNotInApp() },
                            enabled = !isPullingAll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pull_everything_not_in_app_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pull Everything in Drive Not in App ($unimportedCount pending)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(KikiSurfaceDark)
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatColumn(title = "ZIP Archives", value = "$totalZipArchives", icon = Icons.Default.Archive, tint = KikiCyan)
                            StatColumn(title = "Drive Files", value = "${files.size}", icon = Icons.Default.Folder, tint = KikiRose)
                            StatColumn(title = "In Lab", value = "$totalImported", icon = Icons.Default.Science, tint = KikiLilac)
                            StatColumn(title = "Unpack Bonus", value = "+1250 ⚡", icon = Icons.Default.AutoAwesome, tint = KikiGold)
                        }
                    }
                }
            }

            // Search & Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search AI Studio & Muse ZIP archives, formulas...", color = KikiTextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = KikiLilac)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drive_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KikiLilac,
                            unfocusedBorderColor = KikiSurfaceBorder,
                            focusedTextColor = KikiPearl,
                            unfocusedTextColor = KikiPearl,
                            focusedContainerColor = KikiSurfaceDark,
                            unfocusedContainerColor = KikiSurfaceDark
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterOptions) { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedFilter(filter) },
                                label = {
                                    Text(
                                        text = filter,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (filter.contains("ZIP")) KikiCyan else KikiLilac,
                                    selectedLabelColor = Color.Black,
                                    containerColor = KikiSurfaceDark,
                                    labelColor = KikiPearl
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) (if (filter.contains("ZIP")) KikiCyan else KikiLilac) else KikiSurfaceBorder
                                )
                            )
                        }
                    }
                }
            }

            // Drive File List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drive Files (${filteredFiles.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Text(
                        text = currentPath,
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isLoading && files.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KikiLilac)
                    }
                }
            } else if (filteredFiles.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, tint = KikiTextMuted, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Drive files matching criteria", color = KikiPearl, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap refresh or clear filters to view all cloud assets.", color = KikiTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(filteredFiles, key = { it.id }) { file ->
                    DriveFileCard(
                        file = file,
                        onPullClicked = { viewModel.pullSingleFile(file) },
                        onInspectClicked = { viewModel.inspectZipArchive(file) }
                    )
                }
            }
        }
    }

    // Modal Sheet on Pull Complete
    pullSummary?.let { summary ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissSummary() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = KikiSurfaceDark,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(KikiSurfaceBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(KikiGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = KikiGold, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Drive Pull & Unpack Complete!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = KikiPearl
                        )
                        Text(
                            text = "${summary.archivesUnpacked} ZIP archives unpacked • ${summary.totalFilesPulled} files processed",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(KikiSurfaceElevated)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${summary.formulasCreated}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = KikiLilac)
                        Text(text = "Lab Formulas", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${summary.moodboardsImported + summary.specsProcessed}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = KikiCyan)
                        Text(text = "Vault Assets", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "+${summary.sparksEarned}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = KikiGold)
                        Text(text = "Sparks Earned", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Imported Formulations:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(summary.importedFormulas) { creation ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(KikiSurfaceElevated)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(creation.primaryHex))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = creation.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "${creation.category.displayName} • ${creation.finish.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = KikiLilac.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "+150 ⚡",
                                    color = KikiLilac,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.dismissSummary()
                            onNavigateToLab()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("go_to_lab_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KikiLilac)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open in Lab", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.dismissSummary()
                            onNavigateToVault()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("go_to_vault_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
                    ) {
                        Text("View in Vault", color = KikiPearl, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Inspect Archive Modal Sheet
    selectedArchiveForInspect?.let { archive ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeInspectArchive() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = KikiSurfaceDark,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(KikiSurfaceBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KikiCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = KikiCyan, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = archive.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${archive.categoryTag} • ${archive.formattedSize} • ${archive.archiveEntries.size} Files Inside",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Archive Contents (${archive.archiveEntries.size} Items):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(archive.archiveEntries) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(KikiSurfaceElevated)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KikiLilac.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (entry.name.endsWith(".png") || entry.name.endsWith(".jpg")) Icons.Default.Image
                                    else if (entry.name.endsWith(".pdf") || entry.name.endsWith(".mp3")) Icons.Default.Description
                                    else Icons.Default.Science,
                                    contentDescription = null,
                                    tint = KikiLilac,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.formulaName ?: entry.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${entry.category} • ${entry.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KikiSurfaceDark
                            ) {
                                Text(
                                    text = "${entry.sizeBytes / 1024} KB",
                                    color = KikiTextMuted,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.closeInspectArchive()
                        viewModel.pullSingleFile(archive)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unpack_archive_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiCyan)
                ) {
                    Icon(Icons.Default.Unarchive, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unpack All Items into Lab (+250 ⚡ Bonus)", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DriveFileCard(
    file: DriveFileItem,
    onPullClicked: () -> Unit,
    onInspectClicked: () -> Unit
) {
    val isArchive = file.fileType == DriveFileType.ARCHIVE || file.isArchive

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (file.isImported) (if (isArchive) KikiCyan.copy(alpha = 0.6f) else KikiLilac.copy(alpha = 0.5f))
                else if (isArchive) KikiCyan.copy(alpha = 0.4f)
                else KikiSurfaceBorder,
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // File Type Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(getFileColor(file.fileType).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getFileIcon(file.fileType),
                        contentDescription = null,
                        tint = getFileColor(file.fileType),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${file.categoryTag} • ${file.formattedSize}",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${file.modifiedDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (file.isImported) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = if (isArchive) KikiCyan.copy(alpha = 0.2f) else KikiLilac.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isArchive) KikiCyan.copy(alpha = 0.4f) else KikiLilac.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isArchive) KikiCyan else KikiLilac,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArchive) "Unpacked" else "In Lab",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isArchive) KikiCyan else KikiLilac
                            )
                        }
                    }
                } else if (!isArchive) {
                    OutlinedButton(
                        onClick = onPullClicked,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("pull_file_${file.id}"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = KikiLilac,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pull",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiLilac
                        )
                    }
                }
            }

            // Additional details row for archives
            if (isArchive) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(KikiSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📦 ${file.archiveItemCount} files packed inside archive",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiCyan
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onInspectClicked,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("inspect_archive_${file.id}"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = KikiPearl, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inspect", style = MaterialTheme.typography.labelSmall, color = KikiPearl)
                        }

                        if (!file.isImported) {
                            Button(
                                onClick = onPullClicked,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("unpack_archive_${file.id}"),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KikiCyan)
                            ) {
                                Icon(Icons.Default.Unarchive, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unpack", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = tint)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
    }
}

private fun getFileIcon(type: DriveFileType): ImageVector {
    return when (type) {
        DriveFileType.ARCHIVE -> Icons.Default.Archive
        DriveFileType.SPREADSHEET -> Icons.Default.TableChart
        DriveFileType.IMAGE -> Icons.Default.Image
        DriveFileType.DOCUMENT -> Icons.Default.Description
        DriveFileType.DATA -> Icons.Default.Science
        DriveFileType.OTHER -> Icons.Default.InsertDriveFile
    }
}

private fun getFileColor(type: DriveFileType): Color {
    return when (type) {
        DriveFileType.ARCHIVE -> KikiCyan
        DriveFileType.SPREADSHEET -> Color(0xFF4CAF50)
        DriveFileType.IMAGE -> KikiRose
        DriveFileType.DOCUMENT -> KikiCyan
        DriveFileType.DATA -> KikiGold
        DriveFileType.OTHER -> KikiLilac
    }
}
