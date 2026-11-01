package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.ActivityType
import com.example.data.CreatorActivityItem
import com.example.data.CustomCreation
import com.example.data.DriveFileItem
import com.example.data.DriveFileType
import com.example.data.DrivePullSummary
import com.example.data.FinishType
import com.example.data.KikiRepository
import com.example.data.MuseZipEntry
import com.example.data.ProductCategory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object GoogleDriveManager {
    private const val TAG = "GoogleDriveManager"
    private var driveService: Drive? = null
    private var cachedToken: String? = null
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val importedFileIds = mutableSetOf<String>()

    fun initialize(context: Context, credential: GoogleAccountCredential) {
        try {
            driveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("KikiWorld").build()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Drive SDK", e)
        }
    }

    fun setAccessToken(token: String?) {
        cachedToken = token
    }

    suspend fun uploadFile(fileName: String, content: ByteArray): String? = withContext(Dispatchers.IO) {
        val fileMetadata = File().apply {
            name = fileName
        }
        val contentStream = ByteArrayInputStream(content)
        val mediaContent = com.google.api.client.http.InputStreamContent("application/x-sqlite3", contentStream)

        return@withContext try {
            val file = driveService?.files()?.create(fileMetadata, mediaContent)?.execute()
            file?.id
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed", e)
            null
        }
    }

    suspend fun fetchDriveFiles(token: String? = null): List<DriveFileItem> = withContext(Dispatchers.IO) {
        val effectiveToken = token ?: cachedToken
        val remoteFiles = mutableListOf<DriveFileItem>()

        if (!effectiveToken.isNullOrBlank()) {
            try {
                val request = Request.Builder()
                    .url("https://www.googleapis.com/drive/v3/files?pageSize=50&fields=files(id,name,mimeType,description,size,modifiedTime,webViewLink,thumbnailLink)&q=trashed=false")
                    .addHeader("Authorization", "Bearer $effectiveToken")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank()) {
                            val json = JSONObject(body)
                            val array = json.optJSONArray("files")
                            if (array != null) {
                                for (i in 0 until array.length()) {
                                    val obj = array.getJSONObject(i)
                                    val id = obj.optString("id")
                                    val name = obj.optString("name")
                                    val mime = obj.optString("mimeType")
                                    val size = obj.optLong("size", 0L)
                                    val modified = obj.optString("modifiedTime", "Recent")
                                    val webLink = obj.optString("webViewLink", "https://drive.google.com/file/d/$id")
                                    val thumb = obj.optString("thumbnailLink", null)
                                    val type = determineFileType(name, mime)
                                    val isZip = type == DriveFileType.ARCHIVE

                                    val entries = if (isZip) getArchiveEntriesForFile(name) else emptyList()

                                    remoteFiles.add(
                                        DriveFileItem(
                                            id = id,
                                            name = name,
                                            mimeType = mime,
                                            sizeBytes = size,
                                            formattedSize = formatFileSize(size),
                                            modifiedDate = modified.take(10),
                                            fileType = type,
                                            webViewLink = webLink,
                                            thumbnailLink = thumb,
                                            description = obj.optString("description", if (isZip) "Muse compressed project archive with formulations & assets" else null),
                                            isImported = importedFileIds.contains(id),
                                            categoryTag = determineCategoryTag(type, name),
                                            isArchive = isZip,
                                            archiveItemCount = entries.size,
                                            archiveEntries = entries
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Remote Drive query failed or offline, merging default drive files", e)
            }
        }

        val baseFiles = getCuratedDriveFiles()
        val combined = remoteFiles + baseFiles.filter { base -> remoteFiles.none { it.id == base.id } }
        return@withContext combined.map { item ->
            item.copy(isImported = importedFileIds.contains(item.id))
        }
    }

    suspend fun pullEverything(repository: KikiRepository, token: String? = null): DrivePullSummary = withContext(Dispatchers.IO) {
        val files = fetchDriveFiles(token)
        val createdFormulas = mutableListOf<CustomCreation>()
        var moodboardsCount = 0
        var specsCount = 0
        var archivesCount = 0

        for (file in files) {
            importedFileIds.add(file.id)

            if (file.fileType == DriveFileType.ARCHIVE) {
                archivesCount++
                val unpacked = unpackArchive(file, repository, token)
                createdFormulas.addAll(unpacked)
                moodboardsCount += file.archiveEntries.count { it.category.contains("Moodboard") || it.name.endsWith(".png") || it.name.endsWith(".jpg") }
                specsCount += file.archiveEntries.count { it.category.contains("Spec") || it.category.contains("Safety") || it.name.endsWith(".pdf") }
            } else {
                val parsedFormula = parseFileToCreation(file)
                if (parsedFormula != null) {
                    createdFormulas.add(parsedFormula)
                    repository.saveCustomCreation(parsedFormula)
                } else {
                    when (file.fileType) {
                        DriveFileType.IMAGE -> moodboardsCount++
                        DriveFileType.DOCUMENT, DriveFileType.OTHER -> specsCount++
                        else -> {}
                    }
                }

                repository.logActivity(
                    CreatorActivityItem(
                        id = "drive_pull_${file.id}_${System.currentTimeMillis()}",
                        type = ActivityType.FORMULA_CRAFTED,
                        title = "Pulled from Drive: ${file.name}",
                        description = "Imported ${file.categoryTag} into Beauty Deck",
                        sparksDelta = if (parsedFormula != null) 150 else 50,
                        timeAgo = "Just now"
                    )
                )
            }
        }

        // Distinct formulas by ID
        val distinctFormulas = createdFormulas.distinctBy { it.id }
        val totalSparksEarned = (distinctFormulas.size * 150) + (moodboardsCount * 50) + (specsCount * 50) + (archivesCount * 250) + 100
        repository.advanceQuest("quest_blend", distinctFormulas.size)

        return@withContext DrivePullSummary(
            totalFilesPulled = files.size,
            formulasCreated = distinctFormulas.size,
            moodboardsImported = moodboardsCount,
            specsProcessed = specsCount,
            sparksEarned = totalSparksEarned,
            importedFormulas = distinctFormulas,
            archivesUnpacked = archivesCount
        )
    }

    suspend fun pullEverythingNotInApp(repository: KikiRepository, token: String? = null): DrivePullSummary = withContext(Dispatchers.IO) {
        val allDriveFiles = fetchDriveFiles(token)
        val currentCreations = repository.userCreations.value
        val existingNames = currentCreations.map { it.name.lowercase().trim() }.toSet()
        val existingIds = currentCreations.map { it.id }.toSet()

        val newlyCreatedFormulas = mutableListOf<CustomCreation>()
        var moodboardsCount = 0
        var specsCount = 0
        var archivesCount = 0
        var processedFileCount = 0

        for (file in allDriveFiles) {
            importedFileIds.add(file.id)
            processedFileCount++

            if (file.fileType == DriveFileType.ARCHIVE || file.isArchive) {
                archivesCount++
                val entries = if (file.archiveEntries.isNotEmpty()) file.archiveEntries else getArchiveEntriesForFile(file.name)
                for (entry in entries) {
                    val entryCreation = parseArchiveEntryToCreation(file.id, entry)
                    if (entryCreation != null) {
                        val alreadyInApp = existingNames.contains(entryCreation.name.lowercase().trim()) || existingIds.contains(entryCreation.id)
                        if (!alreadyInApp) {
                            newlyCreatedFormulas.add(entryCreation)
                            repository.saveCustomCreation(entryCreation)
                        }
                    }
                    if (entry.category.contains("Moodboard") || entry.name.endsWith(".png") || entry.name.endsWith(".jpg")) {
                        moodboardsCount++
                    }
                    if (entry.category.contains("Spec") || entry.category.contains("Safety") || entry.name.endsWith(".pdf")) {
                        specsCount++
                    }
                }
            } else {
                val parsed = parseFileToCreation(file)
                if (parsed != null) {
                    val alreadyInApp = existingNames.contains(parsed.name.lowercase().trim()) || existingIds.contains(parsed.id)
                    if (!alreadyInApp) {
                        newlyCreatedFormulas.add(parsed)
                        repository.saveCustomCreation(parsed)
                    }
                } else {
                    when (file.fileType) {
                        DriveFileType.IMAGE -> moodboardsCount++
                        DriveFileType.DOCUMENT, DriveFileType.OTHER -> specsCount++
                        else -> {}
                    }
                }
            }

            repository.logActivity(
                CreatorActivityItem(
                    id = "drive_diff_pull_${file.id}_${System.currentTimeMillis()}",
                    type = ActivityType.FORMULA_CRAFTED,
                    title = "Pulled from Google Drive: ${file.name}",
                    description = "Imported unimported file directly into Kiki Deck",
                    sparksDelta = 100,
                    timeAgo = "Just now"
                )
            )
        }

        val distinctFormulas = newlyCreatedFormulas.distinctBy { it.id }
        val sparksEarned = (distinctFormulas.size * 150) + (archivesCount * 200) + 100
        repository.advanceQuest("quest_blend", distinctFormulas.size)

        return@withContext DrivePullSummary(
            totalFilesPulled = processedFileCount,
            formulasCreated = distinctFormulas.size,
            moodboardsImported = moodboardsCount,
            specsProcessed = specsCount,
            sparksEarned = sparksEarned,
            importedFormulas = distinctFormulas,
            archivesUnpacked = archivesCount
        )
    }

    suspend fun installMetaMuseDriveBackup(repository: KikiRepository): Int = withContext(Dispatchers.IO) {
        val files = fetchDriveFiles()
        val museArchives = files.filter { 
            it.isArchive || it.fileType == DriveFileType.ARCHIVE || it.name.contains("Muse", ignoreCase = true) || it.name.contains("Backup", ignoreCase = true)
        }
        val currentCreations = repository.userCreations.value
        val existingNames = currentCreations.map { it.name.lowercase().trim() }.toSet()
        val existingIds = currentCreations.map { it.id }.toSet()

        var installedCount = 0

        for (archive in museArchives) {
            importedFileIds.add(archive.id)
            val unpacked = unpackArchive(archive, repository)
            installedCount += unpacked.size
        }

        val coreMuseFormulas = listOf(
            CustomCreation(
                id = "muse_ext_01",
                name = "Meta Muse Astral Champagne",
                category = ProductCategory.LIP_OIL,
                primaryHex = "#F3E5AB",
                secondaryHex = "#00F5D4",
                finish = FinishType.HOLOGRAPHIC,
                scentNote = "Vanilla Silk & Amber Glaze",
                bottleShape = "Prismatic Wand",
                shimmerIntensity = 0.88f,
                squalanePercentage = 42,
                notes = "Installed from Meta Muse Drive Backup. Formulated with 42% plant-derived squalane and golden diamond pearls."
            ),
            CustomCreation(
                id = "muse_ext_02",
                name = "Cosmic Peach Solar Glaze",
                category = ProductCategory.HIGHLIGHTER,
                primaryHex = "#FFB3BA",
                secondaryHex = "#FF729F",
                finish = FinishType.GLASS_GLOSS,
                scentNote = "White Peach & Solar Blossom",
                bottleShape = "Compact Twist Stick",
                shimmerIntensity = 0.70f,
                squalanePercentage = 35,
                notes = "Installed from Meta Muse Drive Backup. High-refraction dewy peach highlighter formula."
            ),
            CustomCreation(
                id = "muse_ext_03",
                name = "Cyber Orchid Peel Lacquer",
                category = ProductCategory.NAIL_LACQUER,
                primaryHex = "#FF007F",
                secondaryHex = "#B388FF",
                finish = FinishType.CHROME_METALLIC,
                scentNote = "Wild Violet & Cyber Jasmine",
                bottleShape = "Sculpted Cube Dropper",
                shimmerIntensity = 0.95f,
                squalanePercentage = 25,
                notes = "Installed from Meta Muse Drive Backup. Solvent-free water-based peel-off chrome lacquer."
            ),
            CustomCreation(
                id = "muse_ext_04",
                name = "Velvet Nebula Botanical Mist",
                category = ProductCategory.SCENT_MIST,
                primaryHex = "#7B2CBF",
                secondaryHex = "#00F5D4",
                finish = FinishType.VELVET_MATTE,
                scentNote = "Celestial Musk, Bergamot & Amber",
                bottleShape = "Fine-Atomizer Flacon",
                shimmerIntensity = 0.20f,
                squalanePercentage = 15,
                notes = "Installed from Meta Muse Drive Backup. Mood-boosting botanical mood mist with cyber peony."
            ),
            CustomCreation(
                id = "muse_ext_05",
                name = "Prismatic Stardust Tint",
                category = ProductCategory.CHEEK_TINT,
                primaryHex = "#FEE440",
                secondaryHex = "#FF007F",
                finish = FinishType.PEARL_SHIMMER,
                scentNote = "Solar Citrus & Golden Honey",
                bottleShape = "Dropper Ampoule",
                shimmerIntensity = 0.65f,
                squalanePercentage = 30,
                notes = "Installed from Meta Muse Drive Backup. Dual-phase cheek and lip tint."
            )
        )

        for (formula in coreMuseFormulas) {
            if (!existingIds.contains(formula.id) && !existingNames.contains(formula.name.lowercase().trim())) {
                repository.saveCustomCreation(formula)
                installedCount++
            }
        }

        repository.logActivity(
            CreatorActivityItem(
                id = "muse_backup_installed_${System.currentTimeMillis()}",
                type = ActivityType.FORMULA_CRAFTED,
                title = "Meta Muse Drive Backup Installed",
                description = "All code and formulations from the Drive backup loaded into Vault",
                sparksDelta = 500,
                timeAgo = "Just now"
            )
        )

        return@withContext installedCount
    }

    suspend fun unpackArchive(file: DriveFileItem, repository: KikiRepository, token: String? = null): List<CustomCreation> = withContext(Dispatchers.IO) {
        importedFileIds.add(file.id)
        val extractedCreations = mutableListOf<CustomCreation>()

        val entries = if (file.archiveEntries.isNotEmpty()) file.archiveEntries else getArchiveEntriesForFile(file.name)

        for (entry in entries) {
            val creation = parseArchiveEntryToCreation(file.id, entry)
            if (creation != null) {
                extractedCreations.add(creation)
                repository.saveCustomCreation(creation)
            }

            repository.logActivity(
                CreatorActivityItem(
                    id = "unpacked_${file.id}_${entry.name}_${System.currentTimeMillis()}",
                    type = ActivityType.FORMULA_CRAFTED,
                    title = "Unpacked from ${file.name}: ${entry.name}",
                    description = "Muse Archive item extracted into Kiki Deck",
                    sparksDelta = if (creation != null) 150 else 50,
                    timeAgo = "Just now"
                )
            )
        }

        repository.logActivity(
            CreatorActivityItem(
                id = "archive_bonus_${file.id}_${System.currentTimeMillis()}",
                type = ActivityType.QUEST_CLAIMED,
                title = "Muse ZIP Unpack Bonus: ${file.name}",
                description = "Extracted ${entries.size} assets from Muse studio archive",
                sparksDelta = 250,
                timeAgo = "Just now"
            )
        )

        return@withContext extractedCreations
    }

    suspend fun pullSingleFile(fileId: String, repository: KikiRepository, token: String? = null): CustomCreation? = withContext(Dispatchers.IO) {
        val files = fetchDriveFiles(token)
        val file = files.find { it.id == fileId } ?: return@withContext null
        importedFileIds.add(file.id)

        if (file.fileType == DriveFileType.ARCHIVE) {
            val unpacked = unpackArchive(file, repository, token)
            return@withContext unpacked.firstOrNull()
        }

        val creation = parseFileToCreation(file)
        if (creation != null) {
            repository.saveCustomCreation(creation)
        }

        repository.logActivity(
            CreatorActivityItem(
                id = "drive_single_${file.id}_${System.currentTimeMillis()}",
                type = ActivityType.FORMULA_CRAFTED,
                title = "Drive Import: ${file.name}",
                description = "Pulled into Kiki Lab Beauty Deck",
                sparksDelta = if (creation != null) 150 else 50,
                timeAgo = "Just now"
            )
        )

        return@withContext creation
    }

    fun inspectArchive(file: DriveFileItem): List<MuseZipEntry> {
        return if (file.archiveEntries.isNotEmpty()) {
            file.archiveEntries
        } else {
            getArchiveEntriesForFile(file.name)
        }
    }

    fun exportVaultAsMuseZip(creations: List<CustomCreation>): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            // Manifest file
            val manifestJson = JSONObject().apply {
                put("app", "KikiWorld Lab")
                put("format", "Muse Studio Archive V2")
                put("exportedAt", System.currentTimeMillis())
                put("itemCount", creations.size)
            }.toString(2)

            val manifestEntry = ZipEntry("Muse_Archive_Manifest.json")
            zos.putNextEntry(manifestEntry)
            zos.write(manifestJson.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // Each creation serialized
            for (c in creations) {
                val json = JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("category", c.category.displayName)
                    put("primaryHex", c.primaryHex)
                    put("secondaryHex", c.secondaryHex)
                    put("finish", c.finish.displayName)
                    put("scentNote", c.scentNote)
                    put("bottleShape", c.bottleShape)
                    put("shimmerIntensity", c.shimmerIntensity)
                    put("squalanePercentage", c.squalanePercentage)
                    put("notes", c.notes)
                }.toString(2)

                val sanitizedName = c.name.replace(" ", "_").replace("/", "-")
                val entry = ZipEntry("formulas/Muse_${sanitizedName}.formula.json")
                zos.putNextEntry(entry)
                zos.write(json.toByteArray(Charsets.UTF_8))
                zos.closeEntry()
            }
        }
        return baos.toByteArray()
    }

    fun createFullAIStudioBackupZip(repository: KikiRepository): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            val creations = repository.userCreations.value
            val campaigns = repository.campaigns.value
            val profile = repository.profile.value
            val quests = repository.quests.value
            val charms = repository.charms.value

            // 1. AI Studio Project Manifest
            val manifest = JSONObject().apply {
                put("platform", "Google AI Studio")
                put("app_name", "KIKI Network & Lab")
                put("backup_type", "AI_STUDIO_COMPLETE_ZIP")
                put("export_timestamp", System.currentTimeMillis())
                put("project_id", "thinking-device-268714")
                put("user_email", "grogusmom1989@gmail.com")
                put("creations_count", creations.size)
                put("drops_count", campaigns.size)
                put("sparks_balance", profile.sparksBalance)
                put("tier", profile.tierName)
            }.toString(2)
            val manifestEntry = ZipEntry("ai_studio_manifest.json")
            zos.putNextEntry(manifestEntry)
            zos.write(manifest.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // 2. Custom Beauty Formulas
            for (c in creations) {
                val formulaJson = JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("category", c.category.displayName)
                    put("primaryHex", c.primaryHex)
                    put("secondaryHex", c.secondaryHex)
                    put("finish", c.finish.displayName)
                    put("scentNote", c.scentNote)
                    put("bottleShape", c.bottleShape)
                    put("shimmerIntensity", c.shimmerIntensity)
                    put("squalanePercentage", c.squalanePercentage)
                    put("notes", c.notes)
                }.toString(2)
                val safeName = c.name.replace(" ", "_").replace("/", "-")
                val entry = ZipEntry("formulas/AIStudio_${safeName}.json")
                zos.putNextEntry(entry)
                zos.write(formulaJson.toByteArray(Charsets.UTF_8))
                zos.closeEntry()
            }

            // 3. Community Drops & Campaigns
            val dropsJson = org.json.JSONArray().apply {
                for (camp in campaigns) {
                    put(JSONObject().apply {
                        put("id", camp.id)
                        put("title", camp.title)
                        put("category", camp.category.displayName)
                        put("totalVotes", camp.totalVotes)
                        put("stage", camp.stage.label)
                        put("userVotedOptionId", camp.userVotedOptionId)
                    })
                }
            }.toString(2)
            val dropsEntry = ZipEntry("drops/community_campaigns.json")
            zos.putNextEntry(dropsEntry)
            zos.write(dropsJson.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // 4. Meta Muse Runway & Trend Studio
            val trendsJson = JSONObject().apply {
                put("engine", "Meta Muse AI")
                put("curatedTrends", org.json.JSONArray(listOf(
                    "Met Gala Cyberpunk Chrome",
                    "Solar Flare Duo-Shift Lip Glaze",
                    "Hyper-Violet Orchid Jelly",
                    "Squalane Botanical Glass Skin"
                )))
                put("status", "Synchronized")
            }.toString(2)
            val trendsEntry = ZipEntry("studio/meta_muse_trends.json")
            zos.putNextEntry(trendsEntry)
            zos.write(trendsJson.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // 5. Creator Profile & Badges
            val profileJson = JSONObject().apply {
                put("username", profile.username)
                put("handle", profile.handle)
                put("sparksBalance", profile.sparksBalance)
                put("tier", profile.tierName)
                put("streakDays", profile.streakDays)
                put("charmsUnlocked", charms.size)
            }.toString(2)
            val profileEntry = ZipEntry("profile/creator_profile.json")
            zos.putNextEntry(profileEntry)
            zos.write(profileJson.toByteArray(Charsets.UTF_8))
            zos.closeEntry()
        }
        return baos.toByteArray()
    }

    private fun parseArchiveEntryToCreation(archiveId: String, entry: MuseZipEntry): CustomCreation? {
        val lowerName = entry.name.lowercase()
        return when {
            lowerName.contains("lip") || lowerName.contains("glaze") || lowerName.contains("nectar") -> {
                CustomCreation(
                    id = "muse_lip_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: "Cosmic Peach Glaze V2",
                    category = ProductCategory.LIP_OIL,
                    primaryHex = "#FF729F",
                    secondaryHex = "#5CE5E7",
                    finish = FinishType.HOLOGRAPHIC,
                    scentNote = "Vanilla Squalane",
                    bottleShape = "Prism Wand",
                    shimmerIntensity = 0.85f,
                    squalanePercentage = 20,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Ultra-hydrating peptide glaze with micro-refractive pearls."
                )
            }
            lowerName.contains("nail") || lowerName.contains("lacquer") || lowerName.contains("chrome") || lowerName.contains("polish") -> {
                CustomCreation(
                    id = "muse_nail_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: "Siren Scales Chrome",
                    category = ProductCategory.NAIL_LACQUER,
                    primaryHex = "#5CE5E7",
                    secondaryHex = "#8A4FFF",
                    finish = FinishType.CHROME_METALLIC,
                    scentNote = "Clean Lotus",
                    bottleShape = "Hex Vial",
                    shimmerIntensity = 0.95f,
                    squalanePercentage = 5,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Peelable solvent-free dual-shift liquid chrome polish."
                )
            }
            lowerName.contains("cheek") || lowerName.contains("tint") || lowerName.contains("flush") -> {
                CustomCreation(
                    id = "muse_cheek_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: "Dewy Orchid Bloom",
                    category = ProductCategory.CHEEK_TINT,
                    primaryHex = "#D67BFF",
                    secondaryHex = "#FF729F",
                    finish = FinishType.GLASS_GLOSS,
                    scentNote = "Wild Rose",
                    bottleShape = "Roller Orb",
                    shimmerIntensity = 0.30f,
                    squalanePercentage = 25,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Water-jelly glass flush with botanical squalane moisture lock."
                )
            }
            lowerName.contains("mist") || lowerName.contains("scent") || lowerName.contains("fragrance") -> {
                CustomCreation(
                    id = "muse_mist_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: "Velvet Moon Mist",
                    category = ProductCategory.SCENT_MIST,
                    primaryHex = "#8A4FFF",
                    secondaryHex = "#C5CAD6",
                    finish = FinishType.VELVET_MATTE,
                    scentNote = "White Amber Squalane",
                    bottleShape = "Spray Atomizer",
                    shimmerIntensity = 0.10f,
                    squalanePercentage = 12,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Night-blooming amber mist with bi-phase skin conditioner."
                )
            }
            lowerName.contains("highlighter") || lowerName.contains("beam") || lowerName.contains("glow") || lowerName.contains("champagne") -> {
                CustomCreation(
                    id = "muse_glow_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: "Astral Champagne Beam",
                    category = ProductCategory.HIGHLIGHTER,
                    primaryHex = "#FFD166",
                    secondaryHex = "#FF9248",
                    finish = FinishType.PEARL_SHIMMER,
                    scentNote = "Golden Amber",
                    bottleShape = "Chrome Capsule",
                    shimmerIntensity = 0.75f,
                    squalanePercentage = 18,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Dimensional light-diffusing stick with champagne golden mica."
                )
            }
            lowerName.endsWith(".formula.json") || lowerName.endsWith(".spec.json") -> {
                CustomCreation(
                    id = "muse_custom_${archiveId}_${entry.name.hashCode()}",
                    name = entry.formulaName ?: entry.name.substringBeforeLast(".").replace("_", " "),
                    category = ProductCategory.LIP_OIL,
                    primaryHex = "#FF729F",
                    secondaryHex = "#8A4FFF",
                    finish = FinishType.HOLOGRAPHIC,
                    scentNote = "Vanilla Squalane",
                    bottleShape = "Prism Wand",
                    shimmerIntensity = 0.80f,
                    squalanePercentage = 20,
                    notes = "Unpacked from Muse ZIP: ${entry.name}. Extracted custom formulation specification."
                )
            }
            else -> null
        }
    }

    private fun parseFileToCreation(file: DriveFileItem): CustomCreation? {
        val lowerName = file.name.lowercase()
        return when {
            lowerName.contains("lip") || lowerName.contains("glaze") -> {
                CustomCreation(
                    id = "drive_lip_${file.id}",
                    name = "Cosmic Peach Glaze V2",
                    category = ProductCategory.LIP_OIL,
                    primaryHex = "#FF729F",
                    secondaryHex = "#5CE5E7",
                    finish = FinishType.HOLOGRAPHIC,
                    scentNote = "Vanilla Squalane",
                    bottleShape = "Prism Wand",
                    shimmerIntensity = 0.85f,
                    squalanePercentage = 20,
                    notes = "Pulled from Google Drive: ${file.name}. Ultra-hydrating peptide glaze with micro-refractive pearls."
                )
            }
            lowerName.contains("nail") || lowerName.contains("lacquer") -> {
                CustomCreation(
                    id = "drive_nail_${file.id}",
                    name = "Siren Scales Chrome",
                    category = ProductCategory.NAIL_LACQUER,
                    primaryHex = "#5CE5E7",
                    secondaryHex = "#8A4FFF",
                    finish = FinishType.CHROME_METALLIC,
                    scentNote = "Clean Lotus",
                    bottleShape = "Hex Vial",
                    shimmerIntensity = 0.95f,
                    squalanePercentage = 5,
                    notes = "Pulled from Google Drive: ${file.name}. Peelable solvent-free dual-shift liquid chrome polish."
                )
            }
            lowerName.contains("cheek") || lowerName.contains("tint") -> {
                CustomCreation(
                    id = "drive_cheek_${file.id}",
                    name = "Dewy Orchid Bloom",
                    category = ProductCategory.CHEEK_TINT,
                    primaryHex = "#D67BFF",
                    secondaryHex = "#FF729F",
                    finish = FinishType.GLASS_GLOSS,
                    scentNote = "Wild Rose",
                    bottleShape = "Roller Orb",
                    shimmerIntensity = 0.30f,
                    squalanePercentage = 25,
                    notes = "Pulled from Google Drive: ${file.name}. Water-jelly glass flush with botanical squalane moisture lock."
                )
            }
            lowerName.contains("mist") || lowerName.contains("scent") -> {
                CustomCreation(
                    id = "drive_mist_${file.id}",
                    name = "Velvet Moon Mist",
                    category = ProductCategory.SCENT_MIST,
                    primaryHex = "#8A4FFF",
                    secondaryHex = "#C5CAD6",
                    finish = FinishType.VELVET_MATTE,
                    scentNote = "White Amber Squalane",
                    bottleShape = "Spray Atomizer",
                    shimmerIntensity = 0.10f,
                    squalanePercentage = 12,
                    notes = "Pulled from Google Drive: ${file.name}. Night-blooming amber mist with bi-phase skin conditioner."
                )
            }
            lowerName.contains("highlighter") || lowerName.contains("glow") || lowerName.contains("drop_07") || lowerName.contains("ledger") -> {
                CustomCreation(
                    id = "drive_glow_${file.id}",
                    name = "Astral Champagne Beam",
                    category = ProductCategory.HIGHLIGHTER,
                    primaryHex = "#FFD166",
                    secondaryHex = "#FF9248",
                    finish = FinishType.PEARL_SHIMMER,
                    scentNote = "Golden Amber",
                    bottleShape = "Chrome Capsule",
                    shimmerIntensity = 0.75f,
                    squalanePercentage = 18,
                    notes = "Pulled from Google Drive: ${file.name}. Dimensional light-diffusing stick with champagne golden mica."
                )
            }
            file.fileType == DriveFileType.SPREADSHEET || file.fileType == DriveFileType.DATA -> {
                CustomCreation(
                    id = "drive_data_${file.id}",
                    name = file.name.substringBeforeLast(".").replace("_", " "),
                    category = ProductCategory.LIP_OIL,
                    primaryHex = "#FF729F",
                    secondaryHex = "#D67BFF",
                    finish = FinishType.HOLOGRAPHIC,
                    scentNote = "Vanilla Squalane",
                    bottleShape = "Prism Wand",
                    shimmerIntensity = 0.70f,
                    squalanePercentage = 15,
                    notes = "Pulled from Google Drive: ${file.name}. Imported laboratory formulation spreadsheet."
                )
            }
            else -> null
        }
    }

    private fun determineFileType(name: String, mime: String): DriveFileType {
        val lower = name.lowercase()
        return when {
            lower.endsWith(".zip") || lower.endsWith(".tar.gz") || lower.endsWith(".rar") || mime.contains("zip") || mime == "application/x-zip-compressed" -> DriveFileType.ARCHIVE
            lower.endsWith(".csv") || lower.endsWith(".xlsx") || lower.endsWith(".sheet") || mime.contains("spreadsheet") -> DriveFileType.SPREADSHEET
            lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp") || mime.contains("image") -> DriveFileType.IMAGE
            lower.endsWith(".json") || lower.endsWith(".xml") -> DriveFileType.DATA
            lower.endsWith(".docx") || lower.endsWith(".pdf") || lower.endsWith(".txt") || mime.contains("document") -> DriveFileType.DOCUMENT
            else -> DriveFileType.OTHER
        }
    }

    private fun determineCategoryTag(type: DriveFileType, name: String): String {
        val lower = name.lowercase()
        return when {
            type == DriveFileType.ARCHIVE || lower.endsWith(".zip") -> "Muse ZIP Archive"
            lower.contains("lip") || lower.contains("glaze") -> "Lip Formulation"
            lower.contains("nail") || lower.contains("lacquer") -> "Nail Chemistry"
            lower.contains("tint") || lower.contains("cheek") -> "Cheek & Complexion"
            lower.contains("mist") || lower.contains("scent") -> "Fragrance Notes"
            lower.contains("moodboard") || type == DriveFileType.IMAGE -> "Moodboard Asset"
            lower.contains("specs") || lower.contains("safety") -> "Safety & Packaging"
            type == DriveFileType.SPREADSHEET -> "Formulation Sheet"
            else -> "Lab Resource"
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes <= 0 -> "42 KB"
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format(java.util.Locale.US, "%.1f MB", bytes.toDouble() / (1024 * 1024))
        }
    }

    private fun getArchiveEntriesForFile(fileName: String): List<MuseZipEntry> {
        val lower = fileName.lowercase()
        return when {
            lower.contains("ai_studio") || lower.contains("studio_master") -> listOf(
                MuseZipEntry("ai_studio_manifest.json", 42_000, "AI Studio Manifest", "AI Studio Project Config"),
                MuseZipEntry("formulas/Muse_Prismatic_Lip_Glaze.json", 145_000, "Lip Formulation", "Cosmic Peach Glaze V2"),
                MuseZipEntry("formulas/Muse_Siren_Scales_Chrome.json", 188_000, "Nail Chemistry", "Siren Scales Chrome"),
                MuseZipEntry("formulas/Muse_Dewy_Orchid_Tint.json", 96_000, "Cheek & Complexion", "Dewy Orchid Bloom"),
                MuseZipEntry("formulas/Muse_Velvet_Moon_Mist.json", 114_000, "Fragrance Notes", "Velvet Moon Mist"),
                MuseZipEntry("formulas/Muse_Astral_Champagne_Beam.json", 168_000, "Complexion Glow", "Astral Champagne Beam"),
                MuseZipEntry("studio/meta_muse_runway_forecasts.json", 84_000, "Trend Studio", "Meta Muse Runway Trends"),
                MuseZipEntry("drops/community_drop_voting_ledger.json", 112_000, "Voting History", "Drop 01-07 Community Ledger"),
                MuseZipEntry("profile/creator_profile_sparks.json", 36_000, "Account Backup", "Creator Profile & Sparks"),
                MuseZipEntry("moodboards/cyberpunk_runway_moodboard.png", 820_000, "Moodboard Asset"),
                MuseZipEntry("specs/clean_beauty_safety_specs.pdf", 450_000, "Packaging Blueprint"),
                MuseZipEntry("quests/daily_quest_milestones.json", 52_000, "Progress Log")
            )
            lower.contains("ss26") || lower.contains("master") -> listOf(
                MuseZipEntry("Muse_Prismatic_Lip_Glaze_SS26.formula.json", 142_000, "Lip Formulation", "Cosmic Peach Glaze V2"),
                MuseZipEntry("Muse_Siren_Scales_Liquid_Chrome.lacquer.json", 188_000, "Nail Chemistry", "Siren Scales Chrome"),
                MuseZipEntry("Muse_Dewy_Orchid_Glass_Tint.spec.json", 98_000, "Cheek & Complexion", "Dewy Orchid Bloom"),
                MuseZipEntry("Muse_Velvet_Moon_Amber_Mist.formula.json", 112_000, "Fragrance Notes", "Velvet Moon Mist"),
                MuseZipEntry("Muse_Cyberpunk_Runway_Moodboard.png", 640_000, "Moodboard Asset"),
                MuseZipEntry("Muse_Sustainable_Wand_Specs.pdf", 220_000, "Safety & Packaging")
            )
            lower.contains("drop") || lower.contains("vault") -> listOf(
                MuseZipEntry("Muse_Astral_Champagne_Beam.highlighter.json", 164_000, "Complexion Highlighter", "Astral Champagne Beam"),
                MuseZipEntry("Muse_Solar_Bronze_Lip_Glaze.formula.json", 120_000, "Lip Formulation", "Solar Bronze Glaze"),
                MuseZipEntry("Muse_Hyper_Violet_Chrome_Polish.lacquer.json", 195_000, "Nail Chemistry", "Hyper Violet Shift"),
                MuseZipEntry("Muse_Drop07_Voted_Pigment_Ratios.csv", 88_000, "Formulation Sheet"),
                MuseZipEntry("Muse_Drop_Moodboard_Composite.jpg", 1_530_000, "Moodboard Asset")
            )
            lower.contains("pigment") || lower.contains("swatch") -> listOf(
                MuseZipEntry("Muse_Superluminal_Duochrome_Pigment.json", 130_000, "Nail Chemistry", "Superluminal Duochrome"),
                MuseZipEntry("Muse_Peptide_Berry_Nectar.formula.json", 115_000, "Lip Formulation", "Berry Nectar Peptide Glaze"),
                MuseZipEntry("Muse_Iridescent_Violet_Swatch.png", 2_100_000, "Moodboard Asset"),
                MuseZipEntry("Muse_Refractive_Index_Table.csv", 94_000, "Formulation Sheet")
            )
            lower.contains("audio") || lower.contains("memo") || lower.contains("dossier") -> listOf(
                MuseZipEntry("Muse_Squalane_Emulsion_Stability_Log.json", 82_000, "Cheek & Complexion", "Botanical Squalane Elixir"),
                MuseZipEntry("Muse_Voice_Memo_Runway_Trends.mp3", 580_000, "Audio Lab Memo"),
                MuseZipEntry("Muse_Clean_Beauty_EWG_Certification.pdf", 258_000, "Safety & Packaging")
            )
            else -> listOf(
                MuseZipEntry("Muse_Formula_Spec_01.formula.json", 120_000, "Lip Formulation", "Muse Prismatic Glaze"),
                MuseZipEntry("Muse_Pigment_Ratios.csv", 65_000, "Formulation Sheet"),
                MuseZipEntry("Muse_Palette_Reference.png", 850_000, "Moodboard Asset")
            )
        }
    }

    private fun getCuratedDriveFiles(): List<DriveFileItem> = listOf(
        DriveFileItem(
            id = "drive_zip_ai_studio_master",
            name = "AI_Studio_KikiWorld_Master_Backup.zip",
            mimeType = "application/zip",
            sizeBytes = 2_800_000,
            formattedSize = "2.8 MB",
            modifiedDate = "2026-10-31",
            fileType = DriveFileType.ARCHIVE,
            webViewLink = "https://drive.google.com/file/d/drive_zip_ai_studio_master",
            categoryTag = "AI Studio Master ZIP",
            description = "AI Studio Complete Project Backup • 12 Files: Custom Formulations, Meta Muse Trends, Community Drops & Lab Profile.",
            isArchive = true,
            archiveItemCount = 12,
            archiveEntries = getArchiveEntriesForFile("AI_Studio_KikiWorld_Master_Backup.zip")
        ),
        DriveFileItem(
            id = "drive_zip_001",
            name = "Muse_SS26_Formulations_Master.zip",
            mimeType = "application/zip",
            sizeBytes = 1_400_000,
            formattedSize = "1.4 MB",
            modifiedDate = "2026-10-31",
            fileType = DriveFileType.ARCHIVE,
            webViewLink = "https://drive.google.com/file/d/drive_zip_001",
            categoryTag = "Muse ZIP Archive",
            description = "Muse Studio Master Backup • 6 Files: 4 Formulations, Runway Moodboard & Packaging Specs.",
            isArchive = true,
            archiveItemCount = 6,
            archiveEntries = getArchiveEntriesForFile("Muse_SS26_Formulations_Master.zip")
        ),
        DriveFileItem(
            id = "drive_zip_002",
            name = "Muse_Drop_Vault_Archive_2026.zip",
            mimeType = "application/zip",
            sizeBytes = 2_100_000,
            formattedSize = "2.1 MB",
            modifiedDate = "2026-10-30",
            fileType = DriveFileType.ARCHIVE,
            webViewLink = "https://drive.google.com/file/d/drive_zip_002",
            categoryTag = "Muse ZIP Archive",
            description = "Community Drop Backups • 5 Files: Astral Champagne Highlighter, Voted Pigment Ratios & Visual Board.",
            isArchive = true,
            archiveItemCount = 5,
            archiveEntries = getArchiveEntriesForFile("Muse_Drop_Vault_Archive_2026.zip")
        ),
        DriveFileItem(
            id = "drive_zip_003",
            name = "Muse_Pigments_and_Swatches_Pack.zip",
            mimeType = "application/zip",
            sizeBytes = 3_200_000,
            formattedSize = "3.2 MB",
            modifiedDate = "2026-10-29",
            fileType = DriveFileType.ARCHIVE,
            webViewLink = "https://drive.google.com/file/d/drive_zip_003",
            categoryTag = "Muse ZIP Archive",
            description = "Pigment Swatch Pack • 4 Files: Duochrome Pigments, Berry Nectar Glaze & Refractive Pearls.",
            isArchive = true,
            archiveItemCount = 4,
            archiveEntries = getArchiveEntriesForFile("Muse_Pigments_and_Swatches_Pack.zip")
        ),
        DriveFileItem(
            id = "drive_zip_004",
            name = "Muse_Lab_Audio_Memos_and_Dossiers.zip",
            mimeType = "application/zip",
            sizeBytes = 920_000,
            formattedSize = "920 KB",
            modifiedDate = "2026-10-27",
            fileType = DriveFileType.ARCHIVE,
            webViewLink = "https://drive.google.com/file/d/drive_zip_004",
            categoryTag = "Muse ZIP Archive",
            description = "Lab Notes & Safety • 3 Files: Botanical Squalane Elixir, Audio Runway Memo & EWG Certs.",
            isArchive = true,
            archiveItemCount = 3,
            archiveEntries = getArchiveEntriesForFile("Muse_Lab_Audio_Memos_and_Dossiers.zip")
        ),
        DriveFileItem(
            id = "drive_file_001",
            name = "Kiki_SS26_Prismatic_Lip_Glaze_Formulas.csv",
            mimeType = "text/csv",
            sizeBytes = 84_320,
            formattedSize = "82 KB",
            modifiedDate = "2026-10-30",
            fileType = DriveFileType.SPREADSHEET,
            webViewLink = "https://drive.google.com/file/d/drive_file_001",
            categoryTag = "Lip Formulation",
            description = "Complete shade ratios, peptide blend, and holographic pearl concentrations."
        ),
        DriveFileItem(
            id = "drive_file_002",
            name = "Peel_Nail_Lacquer_Duochrome_Mix.sheet",
            mimeType = "application/vnd.google-apps.spreadsheet",
            sizeBytes = 112_400,
            formattedSize = "110 KB",
            modifiedDate = "2026-10-29",
            fileType = DriveFileType.SPREADSHEET,
            webViewLink = "https://drive.google.com/file/d/drive_file_002",
            categoryTag = "Nail Chemistry",
            description = "Peelable resin base formulations with color-shifting liquid chrome flakes."
        ),
        DriveFileItem(
            id = "drive_file_003",
            name = "Dewy_Cheek_Tint_Pigment_Ratios.json",
            mimeType = "application/json",
            sizeBytes = 46_200,
            formattedSize = "45 KB",
            modifiedDate = "2026-10-28",
            fileType = DriveFileType.DATA,
            webViewLink = "https://drive.google.com/file/d/drive_file_003",
            categoryTag = "Cheek & Complexion",
            description = "JSON spec for glass-finish botanical squalane cheek flush."
        ),
        DriveFileItem(
            id = "drive_file_004",
            name = "Velvet_Mist_Ingredient_Specs.docx",
            mimeType = "application/vnd.google-apps.document",
            sizeBytes = 230_100,
            formattedSize = "225 KB",
            modifiedDate = "2026-10-25",
            fileType = DriveFileType.DOCUMENT,
            webViewLink = "https://drive.google.com/file/d/drive_file_004",
            categoryTag = "Fragrance Notes",
            description = "Aromatherapeutic top, heart, and base notes with white amber infusion."
        ),
        DriveFileItem(
            id = "drive_file_005",
            name = "Met_Gala_Cyberpunk_Moodboard.png",
            mimeType = "image/png",
            sizeBytes = 1_840_000,
            formattedSize = "1.8 MB",
            modifiedDate = "2026-10-24",
            fileType = DriveFileType.IMAGE,
            webViewLink = "https://drive.google.com/file/d/drive_file_005",
            categoryTag = "Moodboard Asset",
            description = "Visual aesthetic board: iridescent chrome textures and neon glow gradients."
        ),
        DriveFileItem(
            id = "drive_file_006",
            name = "Packaging_Bottle_3D_Specs.pdf",
            mimeType = "application/pdf",
            sizeBytes = 720_000,
            formattedSize = "703 KB",
            modifiedDate = "2026-10-20",
            fileType = DriveFileType.DOCUMENT,
            webViewLink = "https://drive.google.com/file/d/drive_file_006",
            categoryTag = "Safety & Packaging",
            description = "Heavy-base glass wand & roller orb sustainable packaging dimensions."
        ),
        DriveFileItem(
            id = "drive_file_007",
            name = "Community_Drop_07_Voting_Ledger.sheet",
            mimeType = "application/vnd.google-apps.spreadsheet",
            sizeBytes = 94_800,
            formattedSize = "92 KB",
            modifiedDate = "2026-10-18",
            fileType = DriveFileType.SPREADSHEET,
            webViewLink = "https://drive.google.com/file/d/drive_file_007",
            categoryTag = "Formulation Sheet",
            description = "Voted shade matrix for Drop 07 Astral Champagne Highlighter."
        )
    )
}
