package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CustomCreation
import com.example.data.DriveFileItem
import com.example.data.DriveFileType
import com.example.data.DrivePullSummary
import com.example.data.KikiRepository
import com.example.util.GoogleDriveManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KikiDriveViewModel(
    private val repository: KikiRepository = KikiRepository.getInstance()
) : ViewModel() {

    private val _currentPath = MutableStateFlow("/Google Drive/Muse Lab Backups")
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val _driveFiles = MutableStateFlow<List<DriveFileItem>>(emptyList())
    val driveFiles: StateFlow<List<DriveFileItem>> = _driveFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPullingAll = MutableStateFlow(false)
    val isPullingAll: StateFlow<Boolean> = _isPullingAll.asStateFlow()

    private val _isExportingZip = MutableStateFlow(false)
    val isExportingZip: StateFlow<Boolean> = _isExportingZip.asStateFlow()

    private val _pullProgressText = MutableStateFlow("Connecting to Google Drive...")
    val pullProgressText: StateFlow<String> = _pullProgressText.asStateFlow()

    private val _pullSummary = MutableStateFlow<DrivePullSummary?>(null)
    val pullSummary: StateFlow<DrivePullSummary?> = _pullSummary.asStateFlow()

    private val _selectedArchiveForInspect = MutableStateFlow<DriveFileItem?>(null)
    val selectedArchiveForInspect: StateFlow<DriveFileItem?> = _selectedArchiveForInspect.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    val userCreations: StateFlow<List<CustomCreation>> = repository.userCreations
    val profile = repository.profile

    init {
        loadDriveFiles()
    }

    fun loadDriveFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val files = GoogleDriveManager.fetchDriveFiles()
                _driveFiles.value = files
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load Google Drive files: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pullEverything() {
        viewModelScope.launch {
            _isPullingAll.value = true
            _pullProgressText.value = "Connecting to Google Drive (OAuth: drive.readonly)..."
            delay(300)
            _pullProgressText.value = "Detecting Muse ZIP archives & formulation sheets..."
            delay(350)
            _pullProgressText.value = "Unpacking Muse ZIP archives (extracting formulas & swatches)..."
            delay(400)
            _pullProgressText.value = "Parsing pigments, holographic finishes & EWG safety ratings..."
            delay(350)
            _pullProgressText.value = "Importing formulations into Kiki Lab & Vault Deck..."
            delay(300)

            try {
                val summary = GoogleDriveManager.pullEverything(repository)
                _pullSummary.value = summary
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                _statusMessage.value = "📦 Unpacked ${summary.archivesUnpacked} Muse ZIPs & pulled ${summary.totalFilesPulled} files! +${summary.sparksEarned} Sparks added."
            } catch (e: Exception) {
                _statusMessage.value = "Drive pull error: ${e.localizedMessage}"
            } finally {
                _isPullingAll.value = false
            }
        }
    }

    fun pullEverythingNotInApp() {
        viewModelScope.launch {
            _isPullingAll.value = true
            _pullProgressText.value = "Scanning Google Drive vs in-app Beauty Vault..."
            delay(300)
            _pullProgressText.value = "Finding recipes, formulas and swatch assets not yet in app..."
            delay(350)
            _pullProgressText.value = "Unpacking and ingesting missing Google Drive files..."
            delay(400)
            _pullProgressText.value = "Adding new formulations & awarding sparks..."
            delay(300)

            try {
                val summary = GoogleDriveManager.pullEverythingNotInApp(repository)
                _pullSummary.value = summary
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                _statusMessage.value = if (summary.formulasCreated > 0) {
                    "📥 Successfully pulled ${summary.formulasCreated} new formulations from Google Drive! +${summary.sparksEarned} Sparks earned!"
                } else {
                    "✨ Your app is fully up to date with Google Drive! All ${summary.totalFilesPulled} files already synced."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Google Drive sync error: ${e.localizedMessage}"
            } finally {
                _isPullingAll.value = false
            }
        }
    }

    fun unpackZipArchive(file: DriveFileItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val extracted = GoogleDriveManager.unpackArchive(file, repository)
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                val count = extracted.size
                _statusMessage.value = "📦 Unpacked '${file.name}'! Extracted $count formulations & assets into Lab (+${count * 150 + 250} Sparks)!"
            } catch (e: Exception) {
                _statusMessage.value = "Error unpacking ZIP archive: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pullSingleFile(file: DriveFileItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (file.fileType == DriveFileType.ARCHIVE || file.isArchive) {
                    unpackZipArchive(file)
                    return@launch
                }

                val creation = GoogleDriveManager.pullSingleFile(file.id, repository)
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                if (creation != null) {
                    _statusMessage.value = "🧪 Pulled '${creation.name}' into Kiki Lab! (+150 Sparks)"
                } else {
                    _statusMessage.value = "📁 Pulled '${file.name}' into Kiki Vault! (+50 Sparks)"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error pulling file: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun inspectZipArchive(file: DriveFileItem) {
        _selectedArchiveForInspect.value = file
    }

    fun closeInspectArchive() {
        _selectedArchiveForInspect.value = null
    }

    fun exportVaultToMuseZip() {
        viewModelScope.launch {
            _isExportingZip.value = true
            try {
                val creations = repository.userCreations.value
                val zipBytes = GoogleDriveManager.exportVaultAsMuseZip(creations)
                val fileName = "Muse_KikiVault_Backup_${System.currentTimeMillis() / 1000}.zip"
                GoogleDriveManager.uploadFile(fileName, zipBytes)
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                _statusMessage.value = "📦 Successfully created '${fileName}' (${zipBytes.size / 1024} KB) and backed up to Google Drive!"
            } catch (e: Exception) {
                _statusMessage.value = "Error exporting Muse ZIP backup: ${e.localizedMessage}"
            } finally {
                _isExportingZip.value = false
            }
        }
    }

    fun createAIStudioBackupZip() {
        viewModelScope.launch {
            _isExportingZip.value = true
            try {
                val zipBytes = GoogleDriveManager.createFullAIStudioBackupZip(repository)
                val fileName = "AI_Studio_KikiWorld_Master_Backup_${System.currentTimeMillis() / 1000}.zip"
                GoogleDriveManager.uploadFile(fileName, zipBytes)
                _driveFiles.value = GoogleDriveManager.fetchDriveFiles()
                _statusMessage.value = "✨ Backed up AI Studio to Google Drive: '${fileName}' (${zipBytes.size / 1024} KB) with all formulas & studio assets!"
            } catch (e: Exception) {
                _statusMessage.value = "Error creating AI Studio ZIP backup: ${e.localizedMessage}"
            } finally {
                _isExportingZip.value = false
            }
        }
    }

    fun navigateTo(path: String) {
        _currentPath.value = path
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun dismissSummary() {
        _pullSummary.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
