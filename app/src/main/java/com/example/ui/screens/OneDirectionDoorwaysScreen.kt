package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import com.example.util.HapticsUtil
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.KikiViewModel

// Pastel Palette for Gentle 1D Aesthetic
val PastelPink = Color(0xFFFFD1DC)
val PastelLavender = Color(0xFFE8D7FF)
val PastelSky = Color(0xFFD0F0FD)
val PastelMint = Color(0xFFD1F2D9)
val PastelButtercup = Color(0xFFFFF2B2)
val PastelPeach = Color(0xFFFFDFBA)
val PastelRose = Color(0xFFFFC6D9)
val PastelDeepBg = Color(0xFF13111C)
val PastelCardBg = Color(0xFF1E1A2B)
val PastelBorder = Color(0x33FFFFFF)

enum class DoorwayId {
    BAND,
    LIAM,
    HARRY,
    LOUIS,
    NIALL,
    ZAYN
}

data class OneDirectionDoorway(
    val id: DoorwayId,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val symbolBadge: String,
    val primaryPastel: Color,
    val secondaryPastel: Color,
    val hometown: String,
    val birthday: String,
    val gentleRole: String,
    val doorPlaque: String,
    val welcomingGreeting: String,
    val gentleBio: String,
    val iconicQuotes: List<DoorwayQuote>,
    val heartwarmingMoments: List<HeartwarmingMoment>,
    val audioCatchphrases: List<DoorwayCatchphrase>,
    val presetStickers: List<String>
)

data class DoorwayQuote(
    val text: String,
    val contextTag: String
)

data class HeartwarmingMoment(
    val title: String,
    val era: String,
    val story: String,
    val icon: String
)

data class DoorwayCatchphrase(
    val phrase: String,
    val contextNote: String
)

data class FanTributeNote(
    val id: String,
    val author: String,
    val message: String,
    val sticker: String,
    val timeAgo: String
)

val allOneDirectionDoorways = listOf(
    OneDirectionDoorway(
        id = DoorwayId.BAND,
        title = "One Direction Together",
        subtitle = "Five voices, one brotherhood, infinite starlight",
        iconEmoji = "👑",
        symbolBadge = "FOREVER 1D",
        primaryPastel = PastelPink,
        secondaryPastel = PastelLavender,
        hometown = "London, UK (Formed July 23, 2010)",
        birthday = "July 23, 2010 • 8:22 PM",
        gentleRole = "The Band • Liam, Harry, Louis, Niall & Zayn",
        doorPlaque = "✨ 1D BAND DOORWAY ✨",
        welcomingGreeting = "Welcome home, Directioner! Step inside the band's gentle pastel memory palace.",
        gentleBio = "From five solo boys on the X Factor stairs to the biggest band in the world, selling out stadiums across the globe while keeping their warmth, gentle silliness, and heartfelt love for fans.",
        iconicQuotes = listOf(
            DoorwayQuote("We're One Direction!", "Classic Band Intro"),
            DoorwayQuote("To our Directioners, thank you for everything. We love you more than words can say.", "Wembley Stadium"),
            DoorwayQuote("This isn't the end, it's just history in the making.", "History Era"),
            DoorwayQuote("Baby you light up my world like nobody else.", "What Makes You Beautiful")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("The Video Diaries on the Stairs", "2010", "Sitting on the spiral stairs every week, laughing until they cried and sharing spontaneous jokes with fans.", "🪜"),
            HeartwarmingMoment("Selling Out Wembley Stadium 3 Nights", "2014", "Singing together under a sea of pastel flashlights with 90,000 voices singing back every lyric.", "🏟️"),
            HeartwarmingMoment("Carpool Karaoke Joy", "2015", "Matching denim jackets, synchronized dance routines, and pure vocal harmonies in the car.", "🚗"),
            HeartwarmingMoment("San Siro Stadium Concert Film", "2014", "Singing Right Now as the sun set over Milan in a golden pastel haze.", "🌅")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("We're One Direction!", "The iconic unison group greeting"),
            DoorwayCatchphrase("Thank you for lighting up our world!", "Stadium thank-you message"),
            DoorwayCatchphrase("Directioners forever and always!", "Heartfelt band dedication")
        ),
        presetStickers = listOf("👑 Forever 1D", "✨ 5 Boys 1 Dream", "💖 Starlight", "🌟 Best Memories")
    ),
    OneDirectionDoorway(
        id = DoorwayId.LIAM,
        title = "Liam Payne",
        subtitle = "Payno • Gentle heart, sweet smile & golden vocals",
        iconEmoji = "🎤",
        symbolBadge = "BIG PAYNO",
        primaryPastel = PastelSky,
        secondaryPastel = PastelButtercup,
        hometown = "Wolverhampton, West Midlands, UK",
        birthday = "August 29, 1993",
        gentleRole = "Lead Harmonies • The Warm Sensible Brother",
        doorPlaque = "🕊️ LIAM'S DOORWAY 🕊️",
        welcomingGreeting = "Hello love! Welcome to Liam's gentle sanctuary of warm memories and sweet harmonies.",
        gentleBio = "Known lovingly as Payno. Liam held the band together with his steady vocal grounding, effortless falsettos, beatboxing, and deep care for every fan he ever met.",
        iconicQuotes = listOf(
            DoorwayQuote("Payno in the house!", "Classic Liam Catchphrase"),
            DoorwayQuote("I'm the sensible one... mostly!", "Video Diaries"),
            DoorwayQuote("Always keep a little smile on your face.", "Gentle Fan Message"),
            DoorwayQuote("The best thing about our fans is that you never gave up on us.", "World Tour")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("The Opening Note of Steal My Girl", "2014", "Liam stepping onto the stage, hitting that opening harmony with perfect soulful pitch.", "🎶"),
            HeartwarmingMoment("Sweet Beatboxing on Tour", "2012", "Liam beatboxing into the microphone while the other four boys freestyled acoustic rap.", "🎤"),
            HeartwarmingMoment("The Batman and Dinosaur Moments", "2011", "His playful love for cartoon costumes and laughing on hotel balconies with the fans.", "🦇")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("Payno in the house!", "Liam's signature cheerful greeting"),
            DoorwayCatchphrase("I'm the sensible one... usually!", "Playful interview quote"),
            DoorwayCatchphrase("Big love to every single one of you.", "Gentle closing dedication")
        ),
        presetStickers = listOf("🕊️ Angel Voice", "🎤 Payno Forever", "💙 Pure Heart", "☁️ Gentle Soul")
    ),
    OneDirectionDoorway(
        id = DoorwayId.HARRY,
        title = "Harry Styles",
        subtitle = "Hazza • Curls, dimples & Treat People With Kindness",
        iconEmoji = "🍓",
        symbolBadge = "TPWK",
        primaryPastel = PastelPink,
        secondaryPastel = PastelLavender,
        hometown = "Holmes Chapel, Cheshire, UK",
        birthday = "February 1, 1994",
        gentleRole = "Vocalist • Pure Kindness & Sweet Melodies",
        doorPlaque = "🌸 HARRY'S DOORWAY 🌸",
        welcomingGreeting = "Hello darling! Welcome to Harry's pastel sanctuary of kindness, curls, and sunshine.",
        gentleBio = "With his iconic dimpled smile, green eyes, and warm philosophy of 'Treat People With Kindness', Harry brought magnetic joy, acoustic tenderness, and unconditional acceptance to fans everywhere.",
        iconicQuotes = listOf(
            DoorwayQuote("Treat People With Kindness.", "Life Motto"),
            DoorwayQuote("A dream is only a dream until you decide to make it real.", "Tour Message"),
            DoorwayQuote("Remember to be nice to each other, smile, and eat your vegetables!", "Cozy Advice"),
            DoorwayQuote("Baby you light up my world like nobody else!", "Iconic Solo")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("The Bakery in Holmes Chapel", "2010", "Working at the local Mandeville bakery, singing to customers before his X Factor audition.", "🥐"),
            HeartwarmingMoment("Catwalk Hair Swirls", "2013", "Swinging his curls under the stadium wind machine while laughing into the mic.", "🌬️"),
            HeartwarmingMoment("Whale Water Sprinkles", "2014", "Gently spitting water high into the air like a cute whale during stadium intros.", "🐳")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("Treat People With Kindness!", "Harry's signature life motto"),
            DoorwayCatchphrase("You're all looking gorgeous tonight!", "Concert crowd compliment"),
            DoorwayCatchphrase("Be a lover, give love, choose love.", "Gentle heart affirmation")
        ),
        presetStickers = listOf("🌸 TPWK", "🍓 Hazza Curls", "💖 Sweet Boy", "🦋 Pure Kindness")
    ),
    OneDirectionDoorway(
        id = DoorwayId.LOUIS,
        title = "Louis Tomlinson",
        subtitle = "Tommo • Mischievous smile & Doncaster songwriting soul",
        iconEmoji = "🥕",
        symbolBadge = "DONCASTER",
        primaryPastel = PastelMint,
        secondaryPastel = PastelSky,
        hometown = "Doncaster, South Yorkshire, UK",
        birthday = "December 24, 1991",
        gentleRole = "Master Songwriter • Heart & Mischief of 1D",
        doorPlaque = "✨ LOUIS'S DOORWAY ✨",
        welcomingGreeting = "Alright mate! Welcome to Louis's bright pastel doorway of fun, laughter, and songwriting magic.",
        gentleBio = "The eldest and most spirited brother of the group. Louis co-wrote over 37 iconic 1D songs including 'History', 'No Control', and 'Night Changes', standing fiercely by his friends and fans.",
        iconicQuotes = listOf(
            DoorwayQuote("I like girls who eat carrots!", "Legendary Video Diary"),
            DoorwayQuote("Live fast, have fun, be a bit mischievous!", "Tour Advice"),
            DoorwayQuote("Don't let anyone ever tell you that you can't do something.", "Heartfelt Message"),
            DoorwayQuote("Kevin the pigeon, look at him!", "Stair Video Diary")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("Kevin the Plastic Pigeon", "2010", "Adopting a fake plastic garden pigeon as the band's honorary sixth member.", "🐦"),
            HeartwarmingMoment("No Control Radio Movement", "2015", "Fans launching an unreleased track to #1 worldwide, leaving Louis in grateful awe.", "📻"),
            HeartwarmingMoment("Water Fights on Catwalks", "2014", "Super-soaker water fights with Niall across entire football stadiums.", "💦")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("I like girls who eat carrots!", "The timeless classic 1D quote"),
            DoorwayCatchphrase("Live fast, have fun, be mischievous!", "Tommo's fun energy"),
            DoorwayCatchphrase("Thank you from the bottom of my heart.", "Grateful Doncaster spirit")
        ),
        presetStickers = listOf("🥕 Carrots & Smiles", "✨ Tommo Mischief", "🛹 Doncaster King", "⭐ Songwriting Soul")
    ),
    OneDirectionDoorway(
        id = DoorwayId.NIALL,
        title = "Niall Horan",
        subtitle = "Nialler • Acoustic guitar sunshine & Irish charm",
        iconEmoji = "🎸",
        symbolBadge = "IRISH GOLD",
        primaryPastel = PastelButtercup,
        secondaryPastel = PastelMint,
        hometown = "Mullingar, County Westmeath, Ireland",
        birthday = "September 13, 1993",
        gentleRole = "Acoustic Guitarist • Pure Joy & Contagious Laughter",
        doorPlaque = "🍀 NIALL'S DOORWAY 🍀",
        welcomingGreeting = "Top of the morning! Welcome to Niall's cozy pastel sanctuary of acoustic strums and Irish luck.",
        gentleBio = "With his acoustic guitar perpetually slung over his shoulder, infectious laugh, and genuine appreciation for the moment, Niall was the sunny heartbeat who kept everyone smiling through long tour days.",
        iconicQuotes = listOf(
            DoorwayQuote("Oh potato!", "Classic Video Diary"),
            DoorwayQuote("Every time I walk on stage, I feel like the luckiest boy alive.", "Acoustic Moment"),
            DoorwayQuote("C'mon Ireland! Let's sing together!", "Croke Park"),
            DoorwayQuote("Music is the strongest form of magic.", "Guitar Jam")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("Returning Home to Croke Park Dublin", "2014", "Singing to 80,000 fans in Ireland with tears of sheer pride in his eyes.", "🍀"),
            HeartwarmingMoment("Acoustic Strums of Little Things", "2012", "Just Niall and his guitar leading arenas in quiet, gentle harmony.", "🎸"),
            HeartwarmingMoment("Loving Nandos More Than Anything", "2011", "Celebrating platinum records with a giant bucket of chicken and peri-peri chips.", "🍗")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("Oh potato!", "Niall's most quoted exclamation"),
            DoorwayCatchphrase("Hello beautiful people!", "Niall's warm stadium greeting"),
            DoorwayCatchphrase("Let's have a lovely gentle evening!", "Cozy concert vibe")
        ),
        presetStickers = listOf("🍀 Irish Gold", "🎸 Acoustic Sunshine", "🥔 Potato Love", "💛 Nialler Laugh")
    ),
    OneDirectionDoorway(
        id = DoorwayId.ZAYN,
        title = "Zayn Malik",
        subtitle = "Zayn • Angelic high notes & quiet artistic soul",
        iconEmoji = "🌙",
        symbolBadge = "VAS HAPPENIN",
        primaryPastel = PastelLavender,
        secondaryPastel = PastelSky,
        hometown = "Bradford, West Yorkshire, UK",
        birthday = "January 12, 1993",
        gentleRole = "High Notes • Mysterious R&B Vocal Genius",
        doorPlaque = "⚡ ZAYN'S DOORWAY ⚡",
        welcomingGreeting = "Hey. Welcome to Zayn's peaceful pastel sanctuary of high notes and cosmic sketches.",
        gentleBio = "The quiet artist with vocal agility that could silence an entire arena. Zayn gave 1D their legendary high notes in 'You & I' and 'Rock Me', expressing his soul through murals, comic art, and effortless style.",
        iconicQuotes = listOf(
            DoorwayQuote("Vas Happenin'?!", "The Timeless Catchphrase"),
            DoorwayQuote("I'd rather be who I am than who everyone wants me to be.", "Artistic Motto"),
            DoorwayQuote("Life is funny. Things change, people change, but you will always be you.", "Quiet Reflection"),
            DoorwayQuote("Art and music speak when words cannot.", "Creative Insight")
        ),
        heartwarmingMoments = listOf(
            HeartwarmingMoment("The Legendary You & I High Note", "2014", "Hitting the stratospheric high note live on the pier without missing a beat.", "🎶"),
            HeartwarmingMoment("Graffiti Murals on the Tour Bus", "2013", "Painting comic art and spray-painting colorful characters on backstage walls.", "🎨"),
            HeartwarmingMoment("Buying a House for His Mum", "2013", "The unforgettable sweet moment in This Is Us calling his mom to show her the home he bought her.", "🏡")
        ),
        audioCatchphrases = listOf(
            DoorwayCatchphrase("Vas Happenin'?!", "Zayn's immortal catchphrase"),
            DoorwayCatchphrase("Just be yourself, that's more than enough.", "Gentle self-worth reminder"),
            DoorwayCatchphrase("Peace and love to all the real ones.", "Soulful farewell")
        ),
        presetStickers = listOf("⚡ Vas Happenin", "🌙 Cosmic Zayn", "🎨 Angel Falsetto", "💜 Bradford Boy")
    )
)

@Composable
fun OneDirectionDoorwaysScreen(
    viewModel: KikiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDoorway by remember { mutableStateOf<OneDirectionDoorway?>(null) }
    var sparkleHeartCount by remember { mutableIntStateOf(11) }

    // Floating sparkle shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "sparkleTransition")
    val sparklePulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparklePulse"
    )

    // Back handler: if inside a doorway, back goes to the doorway hall
    BackHandler(enabled = selectedDoorway != null) {
        selectedDoorway = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PastelDeepBg)
            .testTag("one_direction_doorways_screen")
    ) {
        // Decorative background pastel ambient gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PastelPink.copy(alpha = 0.12f * sparklePulse),
                            PastelLavender.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        AnimatedVisibility(
            visible = selectedDoorway == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            // THE GRAND DOORWAY HALL
            DoorwayHallContent(
                doorways = allOneDirectionDoorways,
                onSelectDoorway = { doorway ->
                    HapticsUtil.performSparkleClick(context)
                    selectedDoorway = doorway
                    sparkleHeartCount += 5
                    Toast.makeText(context, "Entering ${doorway.title}'s Doorway ✨", Toast.LENGTH_SHORT).show()
                }
            )
        }

        AnimatedVisibility(
            visible = selectedDoorway != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            selectedDoorway?.let { doorway ->
                // INSIDE THE MEMBER'S DOORWAY ROOM
                DoorwayRoomContent(
                    doorway = doorway,
                    onBack = {
                        HapticsUtil.performSparkleClick(context)
                        selectedDoorway = null
                    },
                    onSendSparkleHeart = {
                        HapticsUtil.performSparkleBurst(context)
                        sparkleHeartCount++
                        Toast.makeText(context, "Sent a sparkle heart to ${doorway.title}! 💖", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun DoorwayHallContent(
    doorways: List<OneDirectionDoorway>,
    onSelectDoorway: (OneDirectionDoorway) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grand Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ONE DIRECTION DOORWAYS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✨", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Gentle pastel sanctuary for the band together and each boy",
                    style = MaterialTheme.typography.bodySmall,
                    color = PastelLavender
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PastelBadge(text = "🌸 Pastel Sparkle", bg = PastelPink.copy(alpha = 0.25f), textCol = PastelPink)
                    PastelBadge(text = "👑 Band & Members Only", bg = PastelLavender.copy(alpha = 0.25f), textCol = PastelLavender)
                    PastelBadge(text = "🚪 6 Magical Doorways", bg = PastelSky.copy(alpha = 0.25f), textCol = PastelSky)
                }
            }
        }

        // The 6 Arched Doorway Cards
        items(doorways, key = { it.id.name }) { doorway ->
            ArchedDoorwayCard(
                doorway = doorway,
                onClick = { onSelectDoorway(doorway) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ArchedDoorwayCard(
    doorway: OneDirectionDoorway,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(doorway.primaryPastel, doorway.secondaryPastel.copy(alpha = 0.4f))
                ),
                RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .clickable(onClick = onClick)
            .testTag("doorway_card_${doorway.id.name.lowercase()}"),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = PastelCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Door Arch Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plaque
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = doorway.primaryPastel.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, doorway.primaryPastel.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = doorway.doorPlaque,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = doorway.primaryPastel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = doorway.symbolBadge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = doorway.secondaryPastel
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Doorway Visual: Arched Frame representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                doorway.primaryPastel.copy(alpha = 0.25f),
                                doorway.secondaryPastel.copy(alpha = 0.10f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        doorway.primaryPastel.copy(alpha = 0.5f),
                        RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = doorway.iconEmoji,
                        fontSize = 38.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TAP TO STEP INSIDE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Info
            Text(
                text = doorway.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Text(
                text = doorway.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = doorway.secondaryPastel
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = doorway.welcomingGreeting,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = doorway.primaryPastel),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Enter ${doorway.title}'s Room",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DoorwayRoomContent(
    doorway: OneDirectionDoorway,
    onBack: () -> Unit,
    onSendSparkleHeart: () -> Unit
) {
    var fanNotes by remember(doorway.id) {
        mutableStateOf(
            listOf(
                FanTributeNote("1", "DirectionerLove", "Forever my favorite boy, your smile lights up everything! 💖", doorway.presetStickers.first(), "2h ago"),
                FanTributeNote("2", "PastelDreamer", "Thank you for the best memories of my entire life. ✨", doorway.presetStickers.getOrElse(1) { "🌟" }, "5h ago")
            )
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var newNoteText by remember { mutableStateOf("") }
    var selectedSticker by remember { mutableStateOf(doorway.presetStickers.first()) }
    var heartsCollected by remember { mutableIntStateOf(28) }
    var activeSoundPhrase by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation & Room Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, doorway.primaryPastel.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("doorway_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = doorway.primaryPastel, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Doorways Hall", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = doorway.primaryPastel.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, doorway.primaryPastel.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = doorway.doorPlaque,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = doorway.primaryPastel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Room Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, doorway.primaryPastel.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(doorway.primaryPastel.copy(alpha = 0.25f))
                                .border(1.5.dp, doorway.primaryPastel, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(doorway.iconEmoji, fontSize = 30.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = doorway.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = doorway.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = doorway.secondaryPastel
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = doorway.gentleBio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Member Stats
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🎂 Birthday:", fontSize = 11.sp, color = PastelLavender)
                                Text(doorway.birthday, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("📍 Hometown:", fontSize = 11.sp, color = PastelSky)
                                Text(doorway.hometown, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("💫 Role in 1D:", fontSize = 11.sp, color = PastelMint)
                                Text(doorway.gentleRole, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Gentle Voice Soundboard Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, doorway.secondaryPastel.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = doorway.primaryPastel, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gentle Voice & Soundboard",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    doorway.audioCatchphrases.forEach { catchphrase ->
                        val isPlaying = activeSoundPhrase == catchphrase.phrase
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPlaying) doorway.primaryPastel.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPlaying) doorway.primaryPastel else PastelBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    activeSoundPhrase = catchphrase.phrase
                                    heartsCollected += 2
                                    HapticsUtil.performJukeboxBeat(context)
                                    com.example.util.MusicAudioEngine.playCatchphrase(
                                        context = context,
                                        speakerName = doorway.title,
                                        phrase = catchphrase.phrase
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isPlaying) "🔊" else "▶️",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "\"${catchphrase.phrase}\"",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isPlaying) doorway.primaryPastel else Color.White
                                    )
                                    Text(
                                        text = catchphrase.contextNote,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                if (isPlaying) {
                                    Text("✨", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Heartfelt Quotes Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, doorway.primaryPastel.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = doorway.secondaryPastel, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Memorable Quotes & Words of Comfort",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    doorway.iconicQuotes.forEach { quote ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "\"${quote.text}\"",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color.White,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "— ${quote.contextTag}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = doorway.secondaryPastel
                                )
                            }
                        }
                    }
                }
            }
        }

        // Heartwarming Moments
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, PastelLavender.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PastelButtercup, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Heartwarming Moments & Memories",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    doorway.heartwarmingMoments.forEach { moment ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(moment.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(moment.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Text(moment.era, fontSize = 10.sp, color = PastelLavender)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(moment.story, fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f), lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sparkle Love Jar & Heart Counter
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, doorway.primaryPastel.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💖", fontSize = 34.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sparkle Love Jar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$heartsCollected Sparkle Hearts Given to ${doorway.title}",
                        fontSize = 11.sp,
                        color = doorway.primaryPastel
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            heartsCollected++
                            onSendSparkleHeart()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = doorway.primaryPastel),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("send_sparkle_heart_btn")
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Send Sparkle Heart (+1)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Fan Tribute Wall & Memory Corkboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, doorway.secondaryPastel.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pastel Fan Tribute Wall",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${fanNotes.size} Notes Pinned",
                            fontSize = 10.sp,
                            color = doorway.secondaryPastel
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset stickers selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(doorway.presetStickers) { sticker ->
                            val isSel = selectedSticker == sticker
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSel) doorway.primaryPastel else Color.Black.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) doorway.primaryPastel else PastelBorder
                                ),
                                modifier = Modifier.clickable { selectedSticker = sticker }
                            ) {
                                Text(
                                    text = sticker,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            placeholder = { Text("Write a gentle love note...", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = doorway.primaryPastel,
                                unfocusedBorderColor = PastelBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (newNoteText.isNotBlank()) {
                                    val note = FanTributeNote(
                                        id = System.currentTimeMillis().toString(),
                                        author = "You",
                                        message = newNoteText.trim(),
                                        sticker = selectedSticker,
                                        timeAgo = "Just now"
                                    )
                                    fanNotes = listOf(note) + fanNotes
                                    newNoteText = ""
                                    heartsCollected += 5
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(doorway.primaryPastel)
                                .testTag("pin_fan_note_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Pin Note", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pinned notes list
                    fanNotes.forEach { note ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, doorway.primaryPastel.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(note.sticker, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = doorway.primaryPastel)
                                    Text(note.timeAgo, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(note.message, fontSize = 11.sp, color = Color.White, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PastelBadge(
    text: String,
    bg: Color,
    textCol: Color
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, textCol.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = textCol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
