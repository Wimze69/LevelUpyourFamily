@file:Suppress("SpellCheckingInspection")

package com.lindberg.levelupyourkids.data

import androidx.compose.ui.graphics.Color
import com.lindberg.levelupyourkids.R

// --- DATA FÖR KARAKTÄRSSKAPANDE ---

// KORRIGERING: Använder nu imageResId för att peka på lokala bilder i drawable-mappen.
val availableAvatars = listOf(
    Avatar(id = "avatar01", name = "Riddare", imageResId = R.drawable.avatar01),
    Avatar(id = "avatar02", name = "Magiker", imageResId = R.drawable.avatar02),
    Avatar(id = "avatar03", name = "Lönnmördare", imageResId = R.drawable.avatar03),
    Avatar(id = "avatar04", name = "Jägare", imageResId = R.drawable.avatar04),
    Avatar(id = "avatar05", name = "Häxa", imageResId = R.drawable.avatar05),
    Avatar(id = "avatar06", name = "Barbar", imageResId = R.drawable.avatar06),
    Avatar(id = "avatar07", name = "Druid", imageResId = R.drawable.avatar07),
    Avatar(id = "avatar08", name = "Bard", imageResId = R.drawable.avatar08),
    Avatar(id = "avatar09", name = "Tjuv", imageResId = R.drawable.avatar09),
    Avatar(id = "avatar10", name = "Paladin", imageResId = R.drawable.avatar10),
)

val availableBackgroundColors = listOf(
    Color(0xFFBDBDBD), Color(0xFFE57373), Color(0xFF64B5F6),
    Color(0xFF81C784), Color(0xFFBA68C8), Color(0xFFFFB74D),
    Color(0xFF4DB6AC), Color(0xFFF06292)
)

val availableClasses = listOf(
    CharacterClass(
        id = "class01", name = "Beskyddaren", description = "Envis och svår att knäcka.",
        trait = Trait(type = TraitType.RESIST_LEVEL_DOWN, chance = 0.15, description = "15% chans att ignorera en level-sänkning.")
    ),
    CharacterClass(
        id = "class02", name = "Skattjägaren", description = "Hittar alltid något extra.",
        trait = Trait(type = TraitType.EXTRA_LOOT, chance = 0.10, description = "10% chans att få extra belöning (loot).")
    ),
    CharacterClass(
        id = "class03", name = "Diplomaten", description = "Löser problem med charm.",
        trait = Trait(type = TraitType.RESIST_LEVEL_DOWN, chance = 0.05, description = "5% chans att övertala GM att inte sänka en level.")
    ),
    CharacterClass(
        id = "class04", name = "Hantverkaren", description = "Effektiv och noggrann.",
        trait = Trait(type = TraitType.EXTRA_LOOT, chance = 0.05, description = "5% chans att få en extra materialbelöning.")
    ),
    CharacterClass(
        id = "class05", name = "Den Vise", description = "Lär sig snabbare än andra.",
        trait = Trait(type = TraitType.XP_BOOST, value = 1.05, description = "Får 5% extra XP från alla quests.")
    ),
    CharacterClass(
        id = "class06", name = "Skuggan", description = "Smidig och obemärkt.",
        trait = Trait(type = TraitType.RESIST_LEVEL_DOWN, chance = 0.10, description = "10% chans att smita undan en level-sänkning.")
    ),
    CharacterClass(
        id = "class07", name = "Elementaristen", description = "I samklang med naturen.",
        trait = Trait(type = TraitType.EXTRA_LOOT, chance = 0.08, description = "8% chans att hitta en sällsynt naturresurs.")
    ),
    CharacterClass(
        id = "class08", name = "Gladiatorn", description = "Trivs i hetluften.",
        trait = Trait(type = TraitType.XP_BOOST, value = 1.02, description = "Får 2% extra XP.")
    ),
    CharacterClass(
        id = "class09", name = "Helaren", description = "Hjälper och stöttar.",
        trait = Trait(type = TraitType.EXTRA_LOOT, chance = 0.12, description = "12% chans att få en bonus för att ha hjälpt andra.")
    ),
    CharacterClass(
        id = "class10", name = "Upptäckaren", description = "Nyfiken på allt.",
        trait = Trait(type = TraitType.XP_BOOST, value = 1.03, description = "Får 3% extra XP från nya typer av quests.")
    ),
    CharacterClass(
        id = "class11", name = "Illusionisten", description = "Inget är vad det ser ut att vara.",
        trait = Trait(type = TraitType.RESIST_LEVEL_DOWN, chance = 0.20, description = "20% chans att förvirra GM vid en level-sänkning.")
    ),
    CharacterClass(
        id = "class12", name = "Jokern", description = "Kaos och tur i en salig blandning.",
        trait = Trait(type = TraitType.EXTRA_LOOT, chance = 0.50, description = "50% chans att få 1 extra XP.")
    ),
)


// --- START-DATA FÖR QUESTS ---
val availableQuests = listOf(
    // --- DAILY QUESTS ---
    Quest(id = "d1", title = "Bädda sängen", description = "Snyggt och prydligt!", xp = 10, questType = QuestType.DAILY.name, category = "Morgon-Uppdrag"),
    Quest(id = "d2", title = "Ställ skorna i skostället", description = "Direkt när du kommer hem.", xp = 10, questType = QuestType.DAILY.name, category = "Ordning & Reda"),
    Quest(id = "d3", title = "Häng upp ytterkläderna", description = "På sin plats direkt.", xp = 10, questType = QuestType.DAILY.name, category = "Ordning & Reda"),
    Quest(id = "d4", title = "All smutsig tvätt i korgen", description = "Inget på golvet.", xp = 10, questType = QuestType.DAILY.name, category = "Städ-Uppdrag"),
    Quest(id = "d5", title = "Duka av egen tallrik", description = "Efter varje måltid.", xp = 10, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d6", title = "Plocka undan på skrivbord/nattduksbord", description = "Innan läggdags.", xp = 10, questType = QuestType.DAILY.name, category = "Kvälls-Uppdrag"),
    Quest(id = "d7", title = "Håll golvet fritt från saker", description = "I ditt rum, innan läggdags.", xp = 10, questType = QuestType.DAILY.name, category = "Kvälls-Uppdrag"),
    Quest(id = "d8", title = "Borsta tänderna (morgon)", description = "Utan påminnelse.", xp = 10, questType = QuestType.DAILY.name, category = "Morgon-Uppdrag"),
    Quest(id = "d9", title = "Borsta tänderna (kväll)", description = "Utan påminnelse.", xp = 10, questType = QuestType.DAILY.name, category = "Kvälls-Uppdrag"),
    Quest(id = "d10", title = "Kvällsrutin kl. 21", description = "Stäng av skärmar, pyjamas etc. utan påminnelse.", xp = 20, questType = QuestType.DAILY.name, category = "Kvälls-Uppdrag"),
    Quest(id = "d11", title = "Duka bordet (Loke)", description = "Duka för dig själv.", xp = 10, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d12", title = "Duka bordet (Cliff/Zach)", description = "Duka halva bordet.", xp = 20, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d13", title = "Duka bordet (Seth)", description = "Duka hela bordet.", xp = 30, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d14", title = "Plocka undan från bordet (Loke)", description = "Din egen disk till diskbänken/maskinen.", xp = 10, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d15", title = "Plocka undan från bordet (Cliff/Zach)", description = "Mer disk, skrapa av.", xp = 20, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d16", title = "Plocka undan från bordet (Seth)", description = "Allt, torka av bordet.", xp = 30, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d17", title = "Packa i diskmaskinen", description = "Hjälp till (Cliff, Zach, Seth).", xp = 20, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d18", title = "Gå ut med sopor/återvinning (lättare)", description = "För Cliff/Zach.", xp = 20, questType = QuestType.DAILY.name, category = "Utomhus-Uppdrag"),
    Quest(id = "d19", title = "Gå ut med sopor/återvinning (allt)", description = "För Seth, se till att det är sorterat.", xp = 30, questType = QuestType.DAILY.name, category = "Utomhus-Uppdrag"),
    Quest(id = "d20", title = "Mata husdjur", description = "Fråga först mamma eller pappa", xp = 20, questType = QuestType.DAILY.name, category = "Köks-Uppdrag"),
    Quest(id = "d21", title = "Plocka undan efter dig", description = "I gemensamma utrymmen.", xp = 20, questType = QuestType.DAILY.name, category = "Ordning & Reda"),
    Quest(id = "d22", title = "Ställ cykeln snyggt utanför", description = "I cykelställ eller bredvid", xp = 20, questType = QuestType.DAILY.name, category = "Utomhus-Uppdrag"),

    // --- WEEKLY QUESTS ---
    Quest(id = "w1", title = "Vattna blommor", description = "Alla blommor hemma.", xp = 20, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),
    Quest(id = "w2", title = "Rasta hunden Ina (promenad)", description = "För Cliff, Zach, Seth.", xp = 40, questType = QuestType.WEEKLY.name, category = "Utomhus-Uppdrag"),
    Quest(id = "w3", title = "Följ med på hundpromenad (Loke)", description = "Hjälp till på promenaden.", xp = 10, questType = QuestType.WEEKLY.name, category = "Utomhus-Uppdrag"),
    Quest(id = "w4", title = "Plocka ur HELA diskmaskinen (Seth)", description = "Ställ allt på rätt plats.", xp = 40, questType = QuestType.WEEKLY.name, category = "Köks-Uppdrag"),
    Quest(id = "w5", title = "Dammsuga/Sopa mindre rum (Cliff/Zach)", description = "Ett mindre rum eller hall.", xp = 30, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),
    Quest(id = "w6", title = "Dammsuga/Sopa större rum (Seth)", description = "Ett större rum eller flera.", xp = 50, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),
    Quest(id = "w7", title = "Ansvara för dammsugning nedervåning (Seth)", description = "Cirka 1 gång/vecka.", xp = 100, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),
    Quest(id = "w8", title = "Hjälp till med tvätt (egen)", description = "Sortera, hänga upp, vika ihop din EGEN tvätt.", xp = 30, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),
    Quest(id = "w9", title = "Hjälp till med matlagning/bakning", description = "Enklare saker under uppsikt.", xp = 40, questType = QuestType.WEEKLY.name, category = "Köks-Uppdrag"),
    Quest(id = "w10", title = "Städa bilen invändigt (Seth)", description = "Dammsuga, torka rent, ta bort skräp.", xp = 150, questType = QuestType.WEEKLY.name, category = "Utomhus-Uppdrag"),
    Quest(id = "w12", title = "Städa upp ditt rum ordentligt", description = "Plocka undan allt, damma ytor, bädda rent.", xp = 80, questType = QuestType.WEEKLY.name, category = "Ordning & Reda"),

    // --- SPECIAL QUESTS (Exempel) ---
    Quest(id = "s1", title = "Hjälpte till med matkassar", description = "Utan att bli ombedd.", xp = 30, questType = QuestType.SPECIAL.name, category = "Special-Uppdrag", isRepeatable = false),
    Quest(id = "s2", title = "Hjälpte Loke", description = "Spontant och snällt.", xp = 20, questType = QuestType.SPECIAL.name, category = "Special-Uppdrag", isRepeatable = false),
    Quest(id = "s3", title = "Plocka upp efter något syskon", description = "Dubbel exp!!", xp = 20, questType = QuestType.SPECIAL.name, category = "Special-Uppdrag", isRepeatable = false)
)
