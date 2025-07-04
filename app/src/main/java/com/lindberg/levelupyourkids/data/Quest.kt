package com.lindberg.levelupyourkids.data

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

// Enum för att hålla reda på quest-typer på ett säkert sätt i koden.
enum class QuestType {
    DAILY, WEEKLY, SPECIAL
}

/**
 * FÖRENKLAD QUEST-KLASS FÖR FIREBASE
 *
 * VARFÖR?
 * Firebase's automatiska dataomvandling (.toObject()) kräver en enkel dataklass
 * med en tom konstruktor. Vår tidigare 'sealed class' var för komplex för detta.
 *
 * @Keep: Denna annotation ser till att ProGuard (som optimerar appen för release)
 * inte tar bort klassen eller dess fält, vilket skulle få Firebase att krascha.
 *
 * @IgnoreExtraProperties: Gör appen mer robust. Om du i framtiden lägger till ett
 * nytt fält i dina Firestore-dokument kommer appen inte att krascha om den inte
 * känner igen fältet.
 *
 * Alla fält har ett standardvärde (t.ex. = "") för att säkerställa att den tomma
 * konstruktorn finns.
 */
@Keep
@IgnoreExtraProperties
data class Quest(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val xp: Int = 0,
    val category: String = "",
    val isRepeatable: Boolean = true,
    // Vi använder en String här eftersom det är enklare att lagra i Firestore än en Enum.
    // Vi sätter en standardtyp för säkerhets skull.
    val questType: String = QuestType.DAILY.name
)