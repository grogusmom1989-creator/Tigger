package com.example.data

import com.example.BuildConfig
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import com.example.data.LeaderboardItem

class KikiRepository {
    fun observeHubPortals(): Flow<List<com.example.data.HubPortal>> {
        val db = firestoreDb ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return db.collection("hubs")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(com.example.data.HubPortal::class.java) }
            .catch { emit(emptyList()) }
    }

    fun observeSocialFeed(): Flow<List<SocialFeedItem>> {
        val db = firestoreDb ?: return _socialFeed
        return db.collection("feed")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(SocialFeedItem::class.java) }
            .catch { emit(emptyList()) }
    }

    fun observeLeaderboard(): Flow<List<LeaderboardItem>> {
        val db = firestoreDb ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return db.collection("leaderboard")
            .orderBy("sparksBalance", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .snapshots()
            .map { it.toObjects(LeaderboardItem::class.java) }
            .catch { emit(emptyList()) }
    }

    private val _campaigns = MutableStateFlow(getInitialDrops())
    val campaigns: StateFlow<List<DropCampaign>> = _campaigns.asStateFlow()

    private val _communityFormulas = MutableStateFlow(getInitialCommunityFormulas())
    val communityFormulas: StateFlow<List<CommunityFormula>> = _communityFormulas.asStateFlow()

    private val _userCreations = MutableStateFlow(getInitialUserCreations())
    val userCreations: StateFlow<List<CustomCreation>> = _userCreations.asStateFlow()

    private val _quests = MutableStateFlow(getInitialQuests())
    val quests: StateFlow<List<DailyQuest>> = _quests.asStateFlow()

    private val _charms = MutableStateFlow(getInitialCharms())
    val charms: StateFlow<List<DigitalCharm>> = _charms.asStateFlow()

    private val _profile = MutableStateFlow(CreatorProfile())
    val profile: StateFlow<CreatorProfile> = _profile.asStateFlow()

    private val _activityHistory = MutableStateFlow(getInitialActivityHistory())
    val activityHistory: StateFlow<List<CreatorActivityItem>> = _activityHistory.asStateFlow()

    private val _socialFeed = MutableStateFlow(getInitialSocialFeed())
    val socialFeed: StateFlow<List<SocialFeedItem>> = _socialFeed.asStateFlow()

    private val _challenges = MutableStateFlow(getInitialChallenges())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    fun logActivity(item: CreatorActivityItem) {
        _activityHistory.update { listOf(item) + it }
    }

    private val db = com.example.App.instance.database
    private val formulaDao = db.communityFormulaDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            formulaDao.getAllFormulas().collect { entities ->
                _communityFormulas.value = entities.map { entity ->
                    CommunityFormula(
                        entity.id, entity.name, entity.creatorName, entity.creatorHandle,
                        entity.category, entity.hexColor, entity.secondaryHex, entity.finish,
                        entity.scentNote, entity.upvotes, entity.isLiked, entity.tags
                    )
                }
            }
        }
    }

    private var firestoreDb: com.google.firebase.firestore.FirebaseFirestore? = null
    private var formulaListenerJob: kotlinx.coroutines.Job? = null

    fun initFirestore(databaseId: String) {
        if (databaseId.isNotBlank() && firestoreDb == null) {
            firestoreDb = com.google.firebase.firestore.FirebaseFirestore.getInstance(databaseId)
            formulaListenerJob?.cancel()
            formulaListenerJob = kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                firestoreDb?.collection("formulas")?.addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.toObjects(com.example.data.room.CommunityFormulaEntity::class.java)?.let { entities ->
                        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                            try {
                                formulaDao.deleteAll()
                                formulaDao.insertFormulas(entities)
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
        }
    }



    private val _products = MutableStateFlow<List<ShopifyProduct>>(emptyList())
    val products: StateFlow<List<ShopifyProduct>> = _products.asStateFlow()


        




    fun syncUserSession(userId: String, username: String) {
        val db = firestoreDb ?: return
        try {
            val userDoc = db.collection("users").document(userId)
            val prof = _profile.value
            val data = mapOf(
                "username" to username.ifBlank { prof.username },
                "handle" to prof.handle,
                "sparksBalance" to prof.sparksBalance,
                "tierName" to prof.tierName,
                "tierLevel" to prof.tierLevel,
                "streakDays" to prof.streakDays,
                "totalFormulasCreated" to prof.totalFormulasCreated,
                "totalVotesCast" to prof.totalVotesCast,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            userDoc.set(data)
        } catch (e: Exception) {
            // Local state remains pristine
        }
    }

    fun redeemSparksForSample(cost: Int = 500): Boolean {
        if (_profile.value.sparksBalance >= cost) {
            _profile.update { it.copy(sparksBalance = it.sparksBalance - cost) }
            logActivity(
                CreatorActivityItem(
                    id = "act_${System.currentTimeMillis()}",
                    type = ActivityType.PROTOTYPE_ORDERED,
                    title = "Pilot Batch Sample Ordered",
                    description = "5ml bespoke tester vial with custom laser engraved cap",
                    sparksDelta = -cost,
                    timeAgo = "Just now"
                )
            )
            return true
        }
        return false
    }

    fun voteOnCampaign(campaignId: String, optionId: String): Boolean {
        var rewarded = false
        var campaignName = ""
        _campaigns.update { list ->
            list.map { campaign ->
                if (campaign.id == campaignId) {
                    if (campaign.userVotedOptionId != null) {
                        campaign // already voted
                    } else {
                        rewarded = true
                        campaignName = campaign.title
                        val updatedOptions = campaign.options.map { opt ->
                            if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt
                        }
                        campaign.copy(
                            totalVotes = campaign.totalVotes + 1,
                            userVotedOptionId = optionId,
                            options = updatedOptions
                        )
                    }
                } else campaign
            }
        }
        if (rewarded) {
            _profile.update {
                it.copy(
                    sparksBalance = it.sparksBalance + 50,
                    lifetimeSparksEarned = it.lifetimeSparksEarned + 50,
                    totalVotesCast = it.totalVotesCast + 1
                )
            }
            advanceQuest("quest_vote", 1)
            logActivity(
                CreatorActivityItem(
                    id = "act_${System.currentTimeMillis()}",
                    type = ActivityType.VOTE_CAST,
                    title = "Voted on Drop Formulation",
                    description = "Supported community proposal in $campaignName",
                    sparksDelta = 50,
                    timeAgo = "Just now"
                )
            )
        }
        return rewarded
    }

    fun saveCustomCreation(creation: CustomCreation) {
        _userCreations.update { listOf(creation) + it }
        _profile.update {
            it.copy(
                sparksBalance = it.sparksBalance + 150,
                lifetimeSparksEarned = it.lifetimeSparksEarned + 150,
                totalFormulasCreated = it.totalFormulasCreated + 1
            )
        }
        advanceQuest("quest_blend", 1)
        logActivity(
            CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = ActivityType.FORMULA_CRAFTED,
                title = "Blended ${creation.name}",
                description = "Saved new ${creation.finish.displayName} formula to Beauty Deck",
                sparksDelta = 150,
                timeAgo = "Just now"
            )
        )
    }

    fun submitToChallenge(challengeId: String, creationId: String) {
        // Mock submission
        logActivity(
            CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = ActivityType.FORMULA_CRAFTED,
                title = "Submitted to Challenge",
                description = "Submitted creation $creationId to challenge $challengeId",
                sparksDelta = 0,
                timeAgo = "Just now"
            )
        )
    }

    fun mergeDuplicates(ids: List<String>) {
        if (ids.size < 2) return
        // Keep the first, delete the rest
        val mainId = ids.first()
        _userCreations.update { list ->
            list.filterNot { it.id in ids.drop(1) }
        }
        logActivity(
            CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = ActivityType.FORMULA_CRAFTED,
                title = "Merged Duplicates",
                description = "Blended ${ids.size} duplicate formulas",
                sparksDelta = 50, // Bonus for cleanup
                timeAgo = "Just now"
            )
        )
        _profile.update { it.copy(sparksBalance = it.sparksBalance + 50) }
    }

    fun awardSparks(amount: Int) {
        _profile.update {
            it.copy(
                sparksBalance = it.sparksBalance + amount,
                lifetimeSparksEarned = it.lifetimeSparksEarned + amount
            )
        }
    }

    fun recordAppLaunch(appName: String) {
        _profile.update {
            it.copy(
                sparksBalance = it.sparksBalance + 10,
                lifetimeSparksEarned = it.lifetimeSparksEarned + 10
            )
        }
        logActivity(
            CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = ActivityType.APP_LAUNCHED,
                title = "Launched $appName",
                description = "Earned sparks for opening $appName via Kikiworld",
                sparksDelta = 10,
                timeAgo = "Just now"
            )
        )
    }

    fun deleteCustomCreation(creationId: String) {
        _userCreations.update { it.filterNot { item -> item.id == creationId } }
    }

    fun toggleLikeCommunityFormula(formulaId: String) {
        _communityFormulas.update { list ->
            list.map { formula ->
                if (formula.id == formulaId) {
                    val newLiked = !formula.isLiked
                    val newCount = if (newLiked) formula.upvotes + 1 else formula.upvotes - 1
                    formula.copy(isLiked = newLiked, upvotes = newCount)
                } else formula
            }
        }
    }

    fun advanceQuest(questId: String, amount: Int) {
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId) {
                    val newCount = (q.currentCount + amount).coerceAtMost(q.targetCount)
                    q.copy(currentCount = newCount)
                } else q
            }
        }
    }

    fun claimQuest(questId: String) {
        var rewardToAdd = 0
        var questTitle = ""
        _quests.update { list ->
            list.map { q ->
                if (q.id == questId && q.isCompleted && !q.claimed) {
                    rewardToAdd = q.rewardSparks
                    questTitle = q.title
                    q.copy(claimed = true)
                } else q
            }
        }
        if (rewardToAdd > 0) {
            _profile.update {
                it.copy(
                    sparksBalance = it.sparksBalance + rewardToAdd,
                    lifetimeSparksEarned = it.lifetimeSparksEarned + rewardToAdd
                )
            }
            logActivity(
                CreatorActivityItem(
                    id = "act_${System.currentTimeMillis()}",
                    type = ActivityType.QUEST_CLAIMED,
                    title = "Quest Completed: $questTitle",
                    description = "Claimed daily lab achievement reward",
                    sparksDelta = rewardToAdd,
                    timeAgo = "Just now"
                )
            )
        }
    }

    private fun getInitialChallenges(): List<Challenge> = listOf(
        Challenge("ch1", "Neon Dreams", "Create a neon inspired holographic shade", 500, "2026-11-05"),
        Challenge("ch2", "Soft Cloud", "Craft a velvet matte pastel formula", 300, "2026-11-10")
    )

    private fun getInitialDrops(): List<DropCampaign> = listOf(
        DropCampaign(
            id = "drop_014",
            dropNumber = "DROP 014",
            title = "Cyber Siren Peel Lacquer",
            subtitle = "Zero-odor peelable chrome & holo nails with botanical squalane",
            category = ProductCategory.NAIL_LACQUER,
            hoursRemaining = 14,
            stage = DropStage.VOTING_SHADE,
            totalVotes = 4920,
            options = listOf(
                VoteOption("opt_14_1", "Amethyst Glitch", "Iridescent violet with cyber cyan duochrome shift", 1840, "#9A4BFF", FinishType.HOLOGRAPHIC),
                VoteOption("opt_14_2", "Liquid Mercury", "Ultra-reflective pure mirror chrome silver", 1420, "#C5CAD6", FinishType.CHROME_METALLIC),
                VoteOption("opt_14_3", "Acid Opal", "Translucent milky teal that flashes chartreuse gold", 1160, "#48E5C2", FinishType.PEARL_SHIMMER),
                VoteOption("opt_14_4", "Nebula Berry", "Deep cosmic wine with crushed galaxy diamond flecks", 500, "#C71F66", FinishType.HOLOGRAPHIC)
            ),
            fullDescription = "Co-created with 4,900+ community members. Formulated with our proprietary water-based peel-off resin—no harsh acetone needed. The winning shade enters immediate pilot production."
        ),
        DropCampaign(
            id = "drop_015",
            dropNumber = "DROP 015",
            title = "Solar Glaze Plumping Lip Oil",
            subtitle = "Hyper-gloss peptide treatment with custom pigment infusion",
            category = ProductCategory.LIP_OIL,
            hoursRemaining = 48,
            stage = DropStage.VOTING_SHADE,
            totalVotes = 8240,
            options = listOf(
                VoteOption("opt_15_1", "Sunbeam Nectar", "Warm sheer honey-apricot with micro golden pearl", 3210, "#FF9248", FinishType.GLASS_GLOSS),
                VoteOption("opt_15_2", "Cyber Lilac", "pH-activated mauve glaze with cold holographic sheen", 2890, "#BA68C8", FinishType.HOLOGRAPHIC),
                VoteOption("opt_15_3", "Glass Jelly", "Water-clear mega refractive finish with cooling spearmint", 1430, "#FFD6E0", FinishType.GLASS_GLOSS),
                VoteOption("opt_15_4", "Solar Flare Coral", "Vibrant juicy strawberry pink infused with rosehip oil", 710, "#FF4071", FinishType.PEARL_SHIMMER)
            ),
            fullDescription = "Infused with 4 molecular weights of hyaluronic acid and fermented camellia oil. 100% vegan, cruelty-free, and packaged in a refillable glass prism."
        ),
        DropCampaign(
            id = "drop_016",
            dropNumber = "DROP 016",
            title = "Aura Beam Strobe Stick",
            subtitle = "Solid balm highlighter with multi-angle refraction",
            category = ProductCategory.HIGHLIGHTER,
            hoursRemaining = 72,
            stage = DropStage.VOTING_FINISH,
            totalVotes = 3450,
            options = listOf(
                VoteOption("opt_16_1", "Prismatic Pearl", "Multi-spectral soft pink with diamond flash", 1620, "#F5D6FF", FinishType.PEARL_SHIMMER),
                VoteOption("opt_16_2", "Molten Glass Dew", "Clear wet-look finish without visible glitter grains", 1150, "#E0F7FA", FinishType.GLASS_GLOSS),
                VoteOption("opt_16_3", "Cosmic Amber", "Sun-drenched champagne gold for warm undertones", 680, "#FFD166", FinishType.CHROME_METALLIC)
            ),
            fullDescription = "Formulated to melt seamlessly onto high points of cheekbones, collarbones, and eyelids without disturbing skincare."
        ),
        DropCampaign(
            id = "drop_017",
            dropNumber = "DROP 017",
            title = "Velvet Horizon Scent Mist",
            subtitle = "Skin-safe fragrance mist with botanical squalane base",
            category = ProductCategory.SCENT_MIST,
            hoursRemaining = 120,
            stage = DropStage.VOTING_PACKAGING,
            totalVotes = 2190,
            options = listOf(
                VoteOption("opt_17_1", "Prism Flask", "Geometric heavy-weight glass with chrome nozzle", 1100, "#C77DFF", FinishType.GLASS_GLOSS),
                VoteOption("opt_17_2", "Frosted Cylinder", "Matte soft-touch recycled aluminum capsule", 780, "#7BF2ED", FinishType.VELVET_MATTE),
                VoteOption("opt_17_3", "Pocket Vaporizer", "Compact slim refillable pocket spray with magnetic cap", 310, "#FFAEBE", FinishType.CHROME_METALLIC)
            ),
            fullDescription = "Notes of cold metallic violet, warm salted ambergris, ripe fig skin, and white cedarwood."
        )
    )

    private fun getInitialCommunityFormulas(): List<CommunityFormula> = listOf(
        CommunityFormula(
            id = "cf_01",
            name = "Chrome Orchid No. 4",
            creatorName = "Elena Vance",
            creatorHandle = "@elena_beauty",
            category = ProductCategory.NAIL_LACQUER,
            hexColor = "#9B5DE5",
            secondaryHex = "#00F5D4",
            finish = FinishType.HOLOGRAPHIC,
            scentNote = "Unscented Pure",
            upvotes = 1420,
            tags = listOf("Duochrome", "PeelOff", "Glitch")
        ),
        CommunityFormula(
            id = "cf_02",
            name = "Glass Peach Cloud",
            creatorName = "Momo Chen",
            creatorHandle = "@momo_lab",
            category = ProductCategory.LIP_OIL,
            hexColor = "#FF9E79",
            secondaryHex = "#FFF1E6",
            finish = FinishType.GLASS_GLOSS,
            scentNote = "White Peach Vanilla",
            upvotes = 980,
            tags = listOf("Plumping", "Dewy", "Everyday")
        ),
        CommunityFormula(
            id = "cf_03",
            name = "Supernova Strobe Stick",
            creatorName = "Kai Sterling",
            creatorHandle = "@kai_cosmetics",
            category = ProductCategory.HIGHLIGHTER,
            hexColor = "#FFD166",
            secondaryHex = "#F15BB5",
            finish = FinishType.PEARL_SHIMMER,
            scentNote = "Solar Amber",
            upvotes = 870,
            tags = listOf("CheekGlow", "WetLook", "Vegan")
        ),
        CommunityFormula(
            id = "cf_04",
            name = "Vapor Violet Mist",
            creatorName = "Aria Nova",
            creatorHandle = "@aurora_sky",
            category = ProductCategory.SCENT_MIST,
            hexColor = "#7209B7",
            secondaryHex = "#F72585",
            finish = FinishType.VELVET_MATTE,
            scentNote = "Metallic Rose & Fig",
            upvotes = 640,
            tags = listOf("CleanFragrance", "Unisex")
        ),
        CommunityFormula(
            id = "cf_05",
            name = "Cyber Siren Teal",
            creatorName = "Kiki Studio Official",
            creatorHandle = "@kiki_official",
            category = ProductCategory.NAIL_LACQUER,
            hexColor = "#3A86FF",
            secondaryHex = "#8338EC",
            finish = FinishType.CHROME_METALLIC,
            scentNote = "Ozone Clean",
            upvotes = 1890,
            tags = listOf("StaffPick", "Chrome", "Waterproof")
        )
    )

    private fun getInitialUserCreations(): List<CustomCreation> = listOf(
        CustomCreation(
            id = "my_c1",
            name = "Luminous Jelly Glaze",
            category = ProductCategory.LIP_OIL,
            primaryHex = "#FF5D8F",
            secondaryHex = "#7BF2ED",
            finish = FinishType.HOLOGRAPHIC,
            scentNote = "Wild Raspberry & Mint",
            bottleShape = "Refillable Prism Wand",
            shimmerIntensity = 0.85f,
            squalanePercentage = 18,
            notes = "Custom high-shine lip lacquer recipe blending cold holographic shimmer with raspberry botanical extract."
        ),
        CustomCreation(
            id = "my_c2",
            name = "Obsidian Mirror Lacquer",
            category = ProductCategory.NAIL_LACQUER,
            primaryHex = "#2E1A47",
            secondaryHex = "#9D4EDD",
            finish = FinishType.CHROME_METALLIC,
            scentNote = "Unscented Clean",
            bottleShape = "Brushed Chrome Cube",
            shimmerIntensity = 0.90f,
            squalanePercentage = 8,
            notes = "Deep galactic violet base with molten liquid chrome reflection. Peelable formula with 6-day wear."
        )
    )

    private fun getInitialQuests(): List<DailyQuest> = listOf(
        DailyQuest(
            id = "quest_vote",
            title = "Cast 2 Drop Votes",
            description = "Support upcoming community product formulations",
            rewardSparks = 100,
            targetCount = 2,
            currentCount = 0,
            claimed = false
        ),
        DailyQuest(
            id = "quest_blend",
            title = "Blend a Lab Formula",
            description = "Craft a new shade or texture in the Beauty Lab",
            rewardSparks = 150,
            targetCount = 1,
            currentCount = 1,
            claimed = false
        ),
        DailyQuest(
            id = "quest_remix",
            title = "Remix a Community Recipe",
            description = "Tweak a trending formula and customize pigment ratios",
            rewardSparks = 75,
            targetCount = 1,
            currentCount = 0,
            claimed = false
        ),
        DailyQuest(
            id = "quest_reiki",
            title = "Mindful Reiki Session",
            description = "Set your daily healing intention",
            rewardSparks = 200,
            targetCount = 1,
            currentCount = 0,
            claimed = false,
            isMindfulMoment = true
        )
    )

    private fun getInitialCharms(): List<DigitalCharm> = listOf(
        DigitalCharm(
            id = "charm_01",
            name = "Genesis Alchemist Key",
            rarity = "Legendary",
            badgeNumber = "#008",
            dateUnlocked = "Oct 2026",
            description = "Awarded to early pioneers who co-created KIKI Drop 001. Unlocks lifetime VIP voting weight.",
            accentHex = "#FFD166"
        ),
        DigitalCharm(
            id = "charm_02",
            name = "Cyber Siren NFC Charm",
            rarity = "Rare",
            badgeNumber = "#342",
            dateUnlocked = "Drop 014",
            description = "Linked to physical NFC peel lacquer cap. Tap against phone to verify batch authentications.",
            accentHex = "#5CE5E7"
        ),
        DigitalCharm(
            id = "charm_03",
            name = "Master Blender Pin",
            rarity = "Ultra-Rare",
            badgeNumber = "#120",
            dateUnlocked = "Level 3",
            description = "Given to creators whose community recipes passed 500+ upvotes.",
            accentHex = "#D67BFF"
        ),
        DigitalCharm(
            id = "charm_04",
            name = "Solar Glaze Founder",
            rarity = "Common",
            badgeNumber = "#1094",
            dateUnlocked = "Day 1 Backer",
            description = "Early participation pass for Drop 015 community formulation.",
            accentHex = "#FF729F"
        )
    )

    fun getTierMilestones(): List<TierMilestone> = listOf(
        TierMilestone(
            level = 1,
            name = "Novice Blender",
            requiredSparks = 0,
            perks = listOf("Basic Pigment Blending", "Community Drop Voting", "Public Recipe Deck"),
            isCompleted = true,
            isCurrent = false
        ),
        TierMilestone(
            level = 2,
            name = "Color Alchemist",
            requiredSparks = 500,
            perks = listOf("Duochrome Shimmer Customizer", "Community Recipe Remixing", "Comment Privileges"),
            isCompleted = true,
            isCurrent = false
        ),
        TierMilestone(
            level = 3,
            name = "Lab Chemist",
            requiredSparks = 1500,
            perks = listOf("Free Laser Cap Engraving", "2x Drop Final Hour Vote Weight", "NFC Digital Key Access"),
            isCompleted = true,
            isCurrent = true
        ),
        TierMilestone(
            level = 4,
            name = "Studio Formulator",
            requiredSparks = 2500,
            perks = listOf("Submit Community Drop Proposals", "Early Pilot Batch Testing Access", "VIP Creator Badge"),
            isCompleted = false,
            isCurrent = false
        ),
        TierMilestone(
            level = 5,
            name = "Master Icon",
            requiredSparks = 5000,
            perks = listOf("Named Signature Drop Co-Creator", "10% Royalty on Production Batches", "Annual KIKI Physical Gift"),
            isCompleted = false,
            isCurrent = false
        )
    )

    fun getIngredientGlossary(): List<IngredientInfo> = listOf(
        IngredientInfo(
            name = "Botanical Squalane (100% Plant-Derived)",
            category = "Emollient & Barrier Repair",
            benefit = "Deep hydration without heavy grease; mimics skin's natural lipid balance.",
            safetyScore = "EWG 1 (Clean)",
            description = "Derived from sustainably sourced sugarcane. Locks in moisture and provides a featherlight slip in lip oils and glosses."
        ),
        IngredientInfo(
            name = "Fermented Camellia Seed Oil",
            category = "Antioxidant & Nourishment",
            benefit = "Rich in Omega 9 and Vitamin E to soothe and soften dry lips or cuticles.",
            safetyScore = "EWG 1 (Clean)",
            description = "Traditional Japanese beauty secret fermented to enhance molecular absorption and free-radical defense."
        ),
        IngredientInfo(
            name = "Micronized Holographic Mica",
            category = "Prismatic Mineral Pigment",
            benefit = "Multi-angle light refraction creating hyper-dimensional duochrome shifts.",
            safetyScore = "EWG 1-2 (Clean Mineral)",
            description = "Ethically sourced mineral platelets coated with ultra-thin silica layers to reflect vibrant violet, teal, and gold spectra."
        ),
        IngredientInfo(
            name = "4D Hyaluronic Acid Complex",
            category = "Multi-Depth Hydrator",
            benefit = "Plumps lip lines instantly by locking in water across 4 molecular weights.",
            safetyScore = "EWG 1 (Clean)",
            description = "Penetrates various layers of skin tissue for sustained moisture retention and plump, dewy bounce."
        ),
        IngredientInfo(
            name = "Water-Based Peelable Resin",
            category = "Eco-Friendly Film Former",
            benefit = "Allows peelable nail lacquers to wear for days without harsh acetone removers.",
            safetyScore = "EWG 1 (Non-Toxic)",
            description = "Advanced clean polymer matrix that sets into a durable chrome film and peels off cleanly in warm water."
        )
    )

    private fun getInitialActivityHistory(): List<CreatorActivityItem> = listOf(
        CreatorActivityItem(
            id = "act_init_1",
            type = ActivityType.VOTE_CAST,
            title = "Voted on Drop 014",
            description = "Cast vote for 'Amethyst Glitch' in Cyber Siren Peel Lacquer",
            sparksDelta = 50,
            timeAgo = "2h ago"
        ),
        CreatorActivityItem(
            id = "act_init_2",
            type = ActivityType.FORMULA_CRAFTED,
            title = "Formulated Luminous Jelly Glaze",
            description = "Custom holographic lip oil recipe saved with 18% squalane",
            sparksDelta = 150,
            timeAgo = "Yesterday"
        ),
        CreatorActivityItem(
            id = "act_init_3",
            type = ActivityType.STREAK_BONUS,
            title = "14-Day Lab Streak Maintained",
            description = "Daily active creator login milestone reached",
            sparksDelta = 100,
            timeAgo = "Yesterday"
        ),
        CreatorActivityItem(
            id = "act_init_4",
            type = ActivityType.VOTE_CAST,
            title = "Voted on Drop 015",
            description = "Cast vote for 'Sunbeam Nectar' in Solar Glaze Lip Oil",
            sparksDelta = 50,
            timeAgo = "3d ago"
        ),
        CreatorActivityItem(
            id = "act_init_5",
            type = ActivityType.FORMULA_CRAFTED,
            title = "Blended Obsidian Mirror Lacquer",
            description = "Crafted molten liquid chrome peelable nail lacquer",
            sparksDelta = 150,
            timeAgo = "5d ago"
        ),
        CreatorActivityItem(
            id = "act_init_6",
            type = ActivityType.QUEST_CLAIMED,
            title = "Genesis Co-Creator Onboarding",
            description = "Joined KIKI World 2.0 and unlocked Alchemist Key",
            sparksDelta = 300,
            timeAgo = "1w ago"
        )
    )

    private fun getInitialSocialFeed(): List<SocialFeedItem> = listOf(
        SocialFeedItem(
            id = "feed_01",
            authorName = "KIKI Studio",
            authorHandle = "@kiki_official",
            content = "New Drop Alert! Join the co-creation phase for Drop 018.",
            dropId = "drop_018",
            timestamp = "1h ago"
        ),
        SocialFeedItem(
            id = "feed_02",
            authorName = "Meta Muse",
            authorHandle = "@meta_muse",
            content = "Synthesized a new avant-garde texture based on 2027 trend analysis.",
            imageUrl = "https://example.com/meta_muse_feed.jpg",
            timestamp = "3h ago"
        )
    )

    private val _mediaItems = MutableStateFlow(getInitialMediaItems())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private fun getInitialMediaItems(): List<MediaItem> = listOf(
        MediaItem("m1", "Studio Lab Vibes", MediaType.IMAGE, "https://example.com/lab1.jpg"),
        MediaItem("m2", "Chrome Siren Nails", MediaType.IMAGE, "https://example.com/nails1.jpg"),
        MediaItem("m3", "Texture Remix", MediaType.VIDEO, "https://example.com/texture1.mp4", thumbnailUrl = "https://example.com/texture1.jpg"),
        MediaItem("m4", "Glass Gloss Finish", MediaType.IMAGE, "https://example.com/lips1.jpg")
    )

    private val _blogEntries = MutableStateFlow(listOf(BlogEntry("b1", "Welcome to KikiVerse", "Exploring the digital sparkle.", "2026-10-31")))
    val blogEntries: StateFlow<List<BlogEntry>> = _blogEntries.asStateFlow()

    private val _musicTracks = MutableStateFlow(listOf(MusicTrack("t1", "Sparkle Jam", "Queen K", "https://example.com/song1.mp3")))
    val musicTracks: StateFlow<List<MusicTrack>> = _musicTracks.asStateFlow()

    private val _shrinePages = MutableStateFlow(listOf(ShrinePage("s1", "Siren Alchemist Shrine", "A space for light.", "https://example.com/shrine.jpg")))
    val shrinePages: StateFlow<List<ShrinePage>> = _shrinePages.asStateFlow()

    private val _galleryItems = MutableStateFlow(listOf(GalleryItem("g1", "Sparkle Art", "https://example.com/art1.jpg")))
    val galleryItems: StateFlow<List<GalleryItem>> = _galleryItems.asStateFlow()

    fun updateProfile(userId: String, username: String, bio: String, profileImageUrl: String?, interests: List<String>) {
        firestoreDb?.collection("users")?.document(userId)?.update(
            mapOf(
                "username" to username,
                "bio" to bio,
                "profileImageUrl" to profileImageUrl,
                "interests" to interests
            )
        )
    }

    fun addActivity(userId: String, activity: UserActivity) {
        val db = firestoreDb ?: return
        db.collection("users").document(userId).collection("activities").add(activity)
    }

    fun claimReward(userId: String, reward: UserReward) {
        val db = firestoreDb ?: return
        db.collection("users").document(userId).collection("rewards").add(reward)
    }

    fun updateCreditBalance(amount: Int) {
        _profile.update { it.copy(creditBalance = it.creditBalance + amount) }
        logActivity(
            CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = ActivityType.QUEST_CLAIMED,
                title = "Credit Purchase",
                description = "Added $amount credits to your vault",
                sparksDelta = 0,
                timeAgo = "Just now"
            )
        )
    }

    companion object {
        @Volatile
        private var instance: KikiRepository? = null

        fun getInstance(): KikiRepository {
            return instance ?: synchronized(this) {
                instance ?: KikiRepository().also { instance = it }
            }
        }
    }
}

