package com.example.data

import android.content.Context
import com.example.data.room.OneDirectionSparkleDatabase
import com.example.data.room.SparkleDao
import com.example.data.room.SparkleItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repository for offline-cached 1D World sparkle items.
 * Pre-populates default sparkle collectibles if Room cache is empty so fans have an immediate offline catalog.
 */
class SparkleRepository(
    context: Context,
    private val sparkleDao: SparkleDao = OneDirectionSparkleDatabase.getDatabase(context).sparkleDao(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val allSparkles: Flow<List<SparkleItemEntity>> = sparkleDao.getAllSparkles()
    val collectedSparkles: Flow<List<SparkleItemEntity>> = sparkleDao.getCollectedSparkles()
    val collectedCount: Flow<Int> = sparkleDao.getCollectedCount()
    val totalPoints: Flow<Int?> = sparkleDao.getTotalCollectedSparklePoints()

    init {
        scope.launch {
            if (sparkleDao.getItemCount() == 0) {
                sparkleDao.insertAll(initialSparkleSeedData)
            }
        }
    }

    fun getSparklesByMember(memberName: String): Flow<List<SparkleItemEntity>> {
        return sparkleDao.getSparklesByMember(memberName)
    }

    suspend fun toggleCollected(id: String, currentStatus: Boolean) {
        val newStatus = !currentStatus
        val timestamp = if (newStatus) System.currentTimeMillis() else 0L
        sparkleDao.updateCollectedStatus(id, newStatus, timestamp)
    }

    suspend fun collectItem(id: String) {
        sparkleDao.updateCollectedStatus(id, true, System.currentTimeMillis())
    }

    suspend fun addCustomSparkle(item: SparkleItemEntity) {
        sparkleDao.insertSparkle(item)
    }

    companion object {
        val initialSparkleSeedData = listOf(
            // The Band (Together)
            SparkleItemEntity(
                id = "sp_band_wembley",
                title = "Wembley Starlight Microphone",
                memberName = "Band",
                rarity = "Legendary",
                iconEmoji = "🎤",
                era = "Where We Are Tour (2014)",
                colorHex = "#E50914",
                description = "Gleaming stadium chrome microphone infused with 90,000 fan voices singing What Makes You Beautiful under the London summer sky.",
                sparkleValue = 250,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 86400000L
            ),
            SparkleItemEntity(
                id = "sp_band_stairs",
                title = "Spiral Stairs Golden Diary",
                memberName = "Band",
                rarity = "Super Rare",
                iconEmoji = "🪜",
                era = "X Factor Video Diaries (2010)",
                colorHex = "#FF4071",
                description = "Cozy parchment journal commemorating weekly laughs, silly jokes, and spontaneous friendships formed on the London studio stairs.",
                sparkleValue = 180,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 43200000L
            ),
            SparkleItemEntity(
                id = "sp_band_san_siro",
                title = "San Siro Sunset Starlight Glass",
                memberName = "Band",
                rarity = "Rare",
                iconEmoji = "🌅",
                era = "Live From San Siro Milan (2014)",
                colorHex = "#FFA07A",
                description = "Prismatic crystal bottle holding the golden sunset glow over the Milan stadium during the opening chords of Right Now.",
                sparkleValue = 120,
                isCollected = false
            ),

            // Liam Payne
            SparkleItemEntity(
                id = "sp_liam_harmonies",
                title = "Payno Golden Harmony Arrow",
                memberName = "Liam",
                rarity = "Legendary",
                iconEmoji = "🕊️",
                era = "Midnight Memories (2013)",
                colorHex = "#64B5F6",
                description = "Gilded angel arrow symbolizing Liam's rock-solid foundation, heartfelt falsettos, and gentle guidance for every Directioner.",
                sparkleValue = 240,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 72000000L
            ),
            SparkleItemEntity(
                id = "sp_liam_beatbox",
                title = "Wolverhampton Beatbox Mic",
                memberName = "Liam",
                rarity = "Super Rare",
                iconEmoji = "🎙️",
                era = "Up All Night Tour (2012)",
                colorHex = "#4FC3F7",
                description = "Compact vintage microphone with acoustic reverberation celebrating Liam's legendary live beatboxing battles on tour.",
                sparkleValue = 160,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 25000000L
            ),
            SparkleItemEntity(
                id = "sp_liam_batman",
                title = "Playful Batman Cape Pin",
                memberName = "Liam",
                rarity = "Common",
                iconEmoji = "🦇",
                era = "Early Video Diaries (2011)",
                colorHex = "#81D4FA",
                description = "Cute miniature enamel superhero pin honoring Liam's playful antics and warm laughs with hotel balcony fans.",
                sparkleValue = 80,
                isCollected = false
            ),

            // Harry Styles
            SparkleItemEntity(
                id = "sp_harry_tpwk",
                title = "TPWK Pastel Kindness Locket",
                memberName = "Harry",
                rarity = "Legendary",
                iconEmoji = "🌸",
                era = "Treat People With Kindness (Forever)",
                colorHex = "#FF80AB",
                description = "Rose quartz heart locket engraved with 'Treat People With Kindness', radiating warmth, dimples, and pure unconditional love.",
                sparkleValue = 250,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 95000000L
            ),
            SparkleItemEntity(
                id = "sp_harry_bakery",
                title = "Holmes Chapel Mandeville Apron",
                memberName = "Harry",
                rarity = "Super Rare",
                iconEmoji = "🥐",
                era = "Pre-Debut Era (2010)",
                colorHex = "#FFB6C1",
                description = "Miniature fabric apron charm celebrating the early morning bread aroma and sweet singing at the Cheshire bakery.",
                sparkleValue = 175,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 36000000L
            ),
            SparkleItemEntity(
                id = "sp_harry_curls",
                title = "Starlight Dimple Swirl Brooch",
                memberName = "Harry",
                rarity = "Rare",
                iconEmoji = "🍓",
                era = "Take Me Home Tour (2013)",
                colorHex = "#FF4081",
                description = "Sparkling strawberry charm with silver spiral swirls celebrating iconic concert hair-flips under stadium wind machines.",
                sparkleValue = 130,
                isCollected = false
            ),

            // Louis Tomlinson
            SparkleItemEntity(
                id = "sp_louis_doncaster",
                title = "Doncaster Songwriting Quill",
                memberName = "Louis",
                rarity = "Legendary",
                iconEmoji = "🥕",
                era = "Made in the A.M. (2015)",
                colorHex = "#69F0AE",
                description = "Mint-green feather quill crafted in tribute to Louis co-writing 37 historic 1D anthems including History and No Control.",
                sparkleValue = 240,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 60000000L
            ),
            SparkleItemEntity(
                id = "sp_louis_kevin",
                title = "Kevin The Pigeon Plushie Charm",
                memberName = "Louis",
                rarity = "Super Rare",
                iconEmoji = "🐦",
                era = "Video Diaries (2010)",
                colorHex = "#B9F6CA",
                description = "Legendary plastic bird mascot adopted by Louis during video diary recordings that became the most famous bird in pop history.",
                sparkleValue = 190,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 15000000L
            ),
            SparkleItemEntity(
                id = "sp_louis_stripes",
                title = "Iconic Breton Striped Tee Pin",
                memberName = "Louis",
                rarity = "Common",
                iconEmoji = "⚓",
                era = "Up All Night (2011)",
                colorHex = "#A7FFEB",
                description = "Navy and white nautical striped enamel badge honoring Louis's quintessential boyish charm and bright red braces.",
                sparkleValue = 75,
                isCollected = false
            ),

            // Niall Horan
            SparkleItemEntity(
                id = "sp_niall_guitar",
                title = "Mullingar Acoustic Gold Pick",
                memberName = "Niall",
                rarity = "Legendary",
                iconEmoji = "🎸",
                era = "Midnight Memories (2013)",
                colorHex = "#FFD54F",
                description = "Solid gold guitar plectrum etched with an Irish clover, celebrating Niall strumming live acoustic chords for 80,000 fans.",
                sparkleValue = 245,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 50000000L
            ),
            SparkleItemEntity(
                id = "sp_niall_laughter",
                title = "Irish Sunshine Laugh Prism",
                memberName = "Niall",
                rarity = "Super Rare",
                iconEmoji = "☘️",
                era = "FOUR Era (2014)",
                colorHex = "#FFE082",
                description = "Bright yellow crystal that twinkles like Niall's infectious laugh, warm guitar riffs, and gentle Irish banter.",
                sparkleValue = 170,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 12000000L
            ),
            SparkleItemEntity(
                id = "sp_niall_croke",
                title = "Croke Park Emerald Medal",
                memberName = "Niall",
                rarity = "Rare",
                iconEmoji = "🥇",
                era = "Where We Are Dublin (2014)",
                colorHex = "#FFF59D",
                description = "Commemorative green-gold medal celebrating Niall returning home to Dublin to play 3 sold-out nights at Croke Park.",
                sparkleValue = 135,
                isCollected = false
            ),

            // Zayn Malik
            SparkleItemEntity(
                id = "sp_zayn_highnote",
                title = "You & I Cosmic High Note Crystal",
                memberName = "Zayn",
                rarity = "Legendary",
                iconEmoji = "🌙",
                era = "Midnight Memories (2013)",
                colorHex = "#B388FF",
                description = "Deep amethyst starlight crystal capturing Zayn's breathtaking octave-soaring high note in You & I that stunned stadiums.",
                sparkleValue = 250,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 80000000L
            ),
            SparkleItemEntity(
                id = "sp_zayn_vas",
                title = "Vas Happenin' Golden Neon Badge",
                memberName = "Zayn",
                rarity = "Super Rare",
                iconEmoji = "⚡",
                era = "Video Diaries (2010)",
                colorHex = "#D1C4E9",
                description = "Glowing lavender neon badge immortalizing Zayn's world-famous catchphrase from the early X Factor staircase mornings.",
                sparkleValue = 185,
                isCollected = true,
                collectedTimestamp = System.currentTimeMillis() - 20000000L
            ),
            SparkleItemEntity(
                id = "sp_zayn_graffiti",
                title = "Bradford Art Sketchbook Leaf",
                memberName = "Zayn",
                rarity = "Common",
                iconEmoji = "🎨",
                era = "Take Me Home (2012)",
                colorHex = "#EDE7F6",
                description = "Textured pastel page filled with Zayn's gentle doodle art, comic sketches, and mural designs drawn backstage on tour.",
                sparkleValue = 85,
                isCollected = false
            )
        )
    }
}
