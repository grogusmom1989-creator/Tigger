package com.example.util

import com.example.data.AvatarEvolutionStage
import com.example.data.CreatorProfile
import com.example.data.DynamicAvatarState

object AvatarEvolutionHelper {

    fun calculateAvatarState(
        profile: CreatorProfile,
        charmsCount: Int = 3
    ): DynamicAvatarState {
        val formulas = profile.totalFormulasCreated
        val quests = profile.totalVotesCast / 3 // Approximate completed quests or use formula/streak

        val stage = when {
            formulas >= 20 || quests >= 15 -> AvatarEvolutionStage.ASTRAL_ARCHON
            formulas >= 12 || quests >= 10 -> AvatarEvolutionStage.ETHEREAL_LUMINARY
            formulas >= 6 || quests >= 5 -> AvatarEvolutionStage.CYBER_NEONIST
            formulas >= 3 || quests >= 2 -> AvatarEvolutionStage.CHROMATIC_WEAVER
            else -> AvatarEvolutionStage.NOVICE_ALCHEMIST
        }

        val nextStage = when (stage) {
            AvatarEvolutionStage.NOVICE_ALCHEMIST -> AvatarEvolutionStage.CHROMATIC_WEAVER
            AvatarEvolutionStage.CHROMATIC_WEAVER -> AvatarEvolutionStage.CYBER_NEONIST
            AvatarEvolutionStage.CYBER_NEONIST -> AvatarEvolutionStage.ETHEREAL_LUMINARY
            AvatarEvolutionStage.ETHEREAL_LUMINARY -> AvatarEvolutionStage.ASTRAL_ARCHON
            AvatarEvolutionStage.ASTRAL_ARCHON -> null
        }

        val progress = if (nextStage != null) {
            val formulaProgress = (formulas.toFloat() / nextStage.requiredFormulas.coerceAtLeast(1)).coerceIn(0f, 1f)
            val questProgress = (quests.toFloat() / nextStage.requiredQuests.coerceAtLeast(1)).coerceIn(0f, 1f)
            (formulaProgress * 0.6f + questProgress * 0.4f).coerceIn(0f, 1f)
        } else {
            1.0f
        }

        val unlockedAuras = mutableListOf("Basic Luminescence")
        if (stage.stageLevel >= 2) unlockedAuras.add("Cyan Hologram Aura")
        if (stage.stageLevel >= 3) unlockedAuras.add("Magenta Pulse Halo")
        if (stage.stageLevel >= 4) unlockedAuras.add("Solar Gold Prismatic Corona")
        if (stage.stageLevel >= 5) unlockedAuras.add("Cosmic Stardust Nebula")

        val currentAccessory = when (stage) {
            AvatarEvolutionStage.NOVICE_ALCHEMIST -> "Alchemist Pipette"
            AvatarEvolutionStage.CHROMATIC_WEAVER -> "Prismatic Visor"
            AvatarEvolutionStage.CYBER_NEONIST -> "Cyber Neon Ear-Cuff"
            AvatarEvolutionStage.ETHEREAL_LUMINARY -> "Aura Crown of Light"
            AvatarEvolutionStage.ASTRAL_ARCHON -> "Stardust Celestial Diadem"
        }

        return DynamicAvatarState(
            stage = stage,
            nextStage = nextStage,
            progressPercent = progress,
            unlockedAuras = unlockedAuras,
            currentAccessory = currentAccessory,
            evolutionRankTitle = "${stage.badgeIcon} ${stage.title} (Tier ${stage.stageLevel})"
        )
    }
}
