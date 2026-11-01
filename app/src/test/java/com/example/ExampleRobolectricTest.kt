package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CustomCreation
import com.example.data.FinishType
import com.example.data.KikiRepository
import com.example.data.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("1D World", appName)
  }

  @Test
  fun `voting on campaign awards sparks and updates total`() {
    val repository = KikiRepository()
    val initialCampaign = repository.campaigns.value.first()
    val initialVotes = initialCampaign.totalVotes
    val initialSparks = repository.profile.value.sparksBalance

    val optionId = initialCampaign.options.first().id
    val success = repository.voteOnCampaign(initialCampaign.id, optionId)

    assertTrue(success)
    val updatedCampaign = repository.campaigns.value.first()
    assertEquals(initialVotes + 1, updatedCampaign.totalVotes)
    assertEquals(initialSparks + 50, repository.profile.value.sparksBalance)
    assertEquals(optionId, updatedCampaign.userVotedOptionId)
  }

  @Test
  fun `saving custom formula in lab awards sparks and updates deck`() {
    val repository = KikiRepository()
    val initialCreationsCount = repository.userCreations.value.size
    val initialSparks = repository.profile.value.sparksBalance

    val creation = CustomCreation(
      id = "test_custom_1",
      name = "Holo Mermaid Glaze",
      category = ProductCategory.LIP_OIL,
      primaryHex = "#5CE5E7",
      secondaryHex = "#D67BFF",
      finish = FinishType.HOLOGRAPHIC,
      scentNote = "Vanilla Squalane",
      bottleShape = "Prism Wand",
      shimmerIntensity = 0.9f,
      squalanePercentage = 20,
      notes = "Test lab creation"
    )

    repository.saveCustomCreation(creation)

    assertEquals(initialCreationsCount + 1, repository.userCreations.value.size)
    assertEquals(initialSparks + 150, repository.profile.value.sparksBalance)
    assertNotNull(repository.userCreations.value.find { it.id == "test_custom_1" })
  }

  @Test
  fun `actions log entries to activity history`() {
    val repository = KikiRepository()
    val initialHistoryCount = repository.activityHistory.value.size

    val creation = CustomCreation(
      id = "test_custom_act",
      name = "Solar Glow Balm",
      category = ProductCategory.HIGHLIGHTER,
      primaryHex = "#FFD166",
      secondaryHex = "#FF729F",
      finish = FinishType.PEARL_SHIMMER,
      scentNote = "Japanese Yuzu",
      bottleShape = "Chrome Capsule",
      shimmerIntensity = 0.8f,
      squalanePercentage = 15,
      notes = "Activity history test"
    )

    repository.saveCustomCreation(creation)
    val updatedHistory = repository.activityHistory.value
    assertEquals(initialHistoryCount + 1, updatedHistory.size)
    assertEquals("Blended Solar Glow Balm", updatedHistory.first().title)
    assertEquals(150, updatedHistory.first().sparksDelta)
  }

  @Test
  fun `google drive manager fetches files and contains beauty formula spreadsheets`() = kotlinx.coroutines.runBlocking {
    val files = com.example.util.GoogleDriveManager.fetchDriveFiles()
    assertTrue(files.isNotEmpty())
    val lipFormula = files.find { it.name.contains("Lip_Glaze") }
    assertNotNull(lipFormula)
  }

  @Test
  fun `pulling everything from google drive imports creations and awards sparks`() = kotlinx.coroutines.runBlocking {
    val repository = KikiRepository()
    val initialCreationsCount = repository.userCreations.value.size
    val initialSparks = repository.profile.value.sparksBalance

    val summary = com.example.util.GoogleDriveManager.pullEverything(repository)

    assertTrue(summary.totalFilesPulled > 0)
    assertTrue(summary.formulasCreated > 0)
    assertTrue(summary.sparksEarned > 0)
    assertTrue(summary.archivesUnpacked > 0)
    assertTrue(repository.userCreations.value.size > initialCreationsCount)
    assertTrue(repository.profile.value.sparksBalance > initialSparks)
  }

  @Test
  fun `google drive manager detects zip archives and unpacks muse and ai studio content`() = kotlinx.coroutines.runBlocking {
    val repository = KikiRepository()
    val initialCreationsCount = repository.userCreations.value.size
    val files = com.example.util.GoogleDriveManager.fetchDriveFiles()

    val aiStudioMasterZip = files.find { it.name == "AI_Studio_KikiWorld_Master_Backup.zip" }
    assertNotNull(aiStudioMasterZip)
    assertEquals(com.example.data.DriveFileType.ARCHIVE, aiStudioMasterZip!!.fileType)
    assertTrue(aiStudioMasterZip.archiveEntries.isNotEmpty())

    val unpackedCreations = com.example.util.GoogleDriveManager.unpackArchive(aiStudioMasterZip, repository)
    assertTrue(unpackedCreations.isNotEmpty())
    assertTrue(repository.userCreations.value.size > initialCreationsCount)
  }

  @Test
  fun `ai studio full project zip export creates valid decompressed zip archive`() {
    val repository = KikiRepository()
    val zipBytes = com.example.util.GoogleDriveManager.createFullAIStudioBackupZip(repository)
    assertTrue(zipBytes.isNotEmpty())

    val entryNames = mutableListOf<String>()
    java.util.zip.ZipInputStream(java.io.ByteArrayInputStream(zipBytes)).use { zis ->
      var entry = zis.nextEntry
      while (entry != null) {
        entryNames.add(entry.name)
        zis.closeEntry()
        entry = zis.nextEntry
      }
    }

    assertTrue(entryNames.contains("ai_studio_manifest.json"))
    assertTrue(entryNames.contains("drops/community_campaigns.json"))
    assertTrue(entryNames.contains("studio/meta_muse_trends.json"))
    assertTrue(entryNames.contains("profile/creator_profile.json"))
  }

  @Test
  fun `avatar evolution stage advances based on formulas and quests`() {
    val noviceProfile = com.example.data.CreatorProfile(totalFormulasCreated = 0, totalVotesCast = 0)
    val noviceState = com.example.util.AvatarEvolutionHelper.calculateAvatarState(noviceProfile)
    assertEquals(com.example.data.AvatarEvolutionStage.NOVICE_ALCHEMIST, noviceState.stage)
    assertEquals("Alchemist Pipette", noviceState.currentAccessory)

    val weaverProfile = com.example.data.CreatorProfile(totalFormulasCreated = 4, totalVotesCast = 6)
    val weaverState = com.example.util.AvatarEvolutionHelper.calculateAvatarState(weaverProfile)
    assertEquals(com.example.data.AvatarEvolutionStage.CHROMATIC_WEAVER, weaverState.stage)
    assertEquals("Prismatic Visor", weaverState.currentAccessory)

    val masterProfile = com.example.data.CreatorProfile(totalFormulasCreated = 22, totalVotesCast = 45)
    val masterState = com.example.util.AvatarEvolutionHelper.calculateAvatarState(masterProfile)
    assertEquals(com.example.data.AvatarEvolutionStage.ASTRAL_ARCHON, masterState.stage)
  }

  @Test
  fun `multi-store platforms support Samsung, Microsoft, Apple and Google Play`() {
    val platforms = com.example.ui.screens.supportedStorePlatforms
    assertTrue(platforms.size >= 5)
    val platformIds = platforms.map { it.id }
    assertTrue(platformIds.contains("google_play"))
    assertTrue(platformIds.contains("samsung_galaxy"))
    assertTrue(platformIds.contains("apple_store"))
    assertTrue(platformIds.contains("microsoft_windows"))
    assertTrue(platformIds.contains("amazon_appstore"))

    val msStore = platforms.first { it.id == "microsoft_windows" }
    assertEquals("Microsoft Store (Windows)", msStore.name)
    assertEquals("Microsoft Corporation", msStore.company)
  }

  @Test
  fun `pullEverythingNotInApp pulls unimported Drive files and prevents duplicates`() = kotlinx.coroutines.runBlocking {
    val repository = KikiRepository()
    val initialCreationsCount = repository.userCreations.value.size

    val summary = com.example.util.GoogleDriveManager.pullEverythingNotInApp(repository)
    assertTrue(summary.totalFilesPulled > 0)
    assertTrue(repository.userCreations.value.size >= initialCreationsCount)

    // Second pull should not create duplicate entries
    val secondSummary = com.example.util.GoogleDriveManager.pullEverythingNotInApp(repository)
    assertEquals(0, secondSummary.formulasCreated)
  }

  @Test
  fun `meta muse extraction produces valid custom creations with squalane and finishes`() {
    val extracted = com.example.ui.components.defaultExtractedFormulas
    assertTrue(extracted.isNotEmpty())
    val astral = extracted.find { it.name.contains("Astral") }
    assertNotNull(astral)
    assertTrue(astral!!.squalanePercentage > 0)
    assertEquals(FinishType.HOLOGRAPHIC, astral.finish)
  }

  @Test
  fun `one direction tribute collection has all 5 members with signature formulations`() {
    val members = com.example.ui.screens.defaultOneDirectionMembers
    assertEquals(5, members.size)
    val names = members.map { it.memberName }
    assertTrue(names.contains("Harry Styles"))
    assertTrue(names.contains("Niall Horan"))
    assertTrue(names.contains("Zayn Malik"))
    assertTrue(names.contains("Liam Payne"))
    assertTrue(names.contains("Louis Tomlinson"))

    val liamRed = members.first { it.memberName == "Liam Payne" }
    assertEquals("#E50914", liamRed.primaryHex)
    assertEquals(FinishType.HOLOGRAPHIC, liamRed.finish)
  }

  @Test
  fun `one direction album eras covers all five studio albums`() {
    val eras = com.example.ui.screens.oneDirectionAlbumEras
    assertEquals(5, eras.size)
    val titles = eras.map { it.title }
    assertTrue(titles.contains("Up All Night"))
    assertTrue(titles.contains("Take Me Home"))
    assertTrue(titles.contains("Midnight Memories"))
    assertTrue(titles.contains("FOUR"))
    assertTrue(titles.contains("Made in the A.M."))
  }

  @Test
  fun `one direction tracks and world tours are populated with classic hits`() {
    val tracks = com.example.ui.screens.oneDirectionTracks
    assertTrue(tracks.size >= 8)
    val songTitles = tracks.map { it.title }
    assertTrue(songTitles.contains("What Makes You Beautiful"))
    assertTrue(songTitles.contains("Night Changes"))
    assertTrue(songTitles.contains("Story of My Life"))

    val tours = com.example.ui.screens.oneDirectionTours
    assertEquals(4, tours.size)
    val tourNames = tours.map { it.name }
    assertTrue(tourNames.contains("Where We Are Tour"))

    val trivia = com.example.ui.screens.oneDirectionTriviaQuestions
    assertEquals(5, trivia.size)
  }
}

