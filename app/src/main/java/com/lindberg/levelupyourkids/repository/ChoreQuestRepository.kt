package com.lindberg.levelupyourkids.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.lindberg.levelupyourkids.data.Family
import com.lindberg.levelupyourkids.data.PlayerProfile
import com.lindberg.levelupyourkids.data.Quest
import com.lindberg.levelupyourkids.data.availableQuests
import com.lindberg.levelupyourkids.data.xpForNextSeasonalLevel
import kotlinx.coroutines.tasks.await

class ChoreQuestRepository {

    private val db = Firebase.firestore

    // --- User & Profile Functions ---

    fun addUserProfileListener(userId: String, onUpdate: (PlayerProfile?) -> Unit): ListenerRegistration {
        val userDocRef = db.collection("users").document(userId)

        return userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("Repository", "User lookup listen failed.", error)
                onUpdate(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val familyId = snapshot.getString("familyId")
                if (familyId != null) {
                    val playerDocRef = db.collection("families").document(familyId)
                        .collection("players").document(userId)

                    playerDocRef.addSnapshotListener { playerSnapshot, playerError ->
                        if (playerError != null) {
                            Log.w("Repository", "Player profile listen failed.", playerError)
                            onUpdate(null)
                            return@addSnapshotListener
                        }
                        onUpdate(playerSnapshot?.toObject<PlayerProfile>())
                    }
                } else {
                    onUpdate(PlayerProfile(id = userId))
                }
            } else {
                onUpdate(null)
            }
        }
    }

    fun addPlayerProfileListener(familyId: String, userId: String, onUpdate: (PlayerProfile?) -> Unit): ListenerRegistration {
        val playerDocRef = db.collection("families").document(familyId).collection("players").document(userId)
        return playerDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("Repository", "Single player listen failed.", error)
                onUpdate(null)
                return@addSnapshotListener
            }
            onUpdate(snapshot?.toObject<PlayerProfile>())
        }
    }

    suspend fun updatePlayerCharacter(familyId: String, userId: String, character: com.lindberg.levelupyourkids.data.Character) {
        try {
            db.collection("families").document(familyId)
                .collection("players").document(userId)
                .update("character", character).await()
        } catch (e: Exception) {
            Log.e("Repository", "Error updating character", e)
        }
    }

    suspend fun completeQuest(familyId: String, userId: String, quest: Quest) {
        val playerDocRef = db.collection("families").document(familyId).collection("players").document(userId)
        try {
            val updates = mapOf(
                "seasonalXp" to FieldValue.increment(quest.xp.toLong()),
                "totalXp" to FieldValue.increment(quest.xp.toLong())
            )
            playerDocRef.update(updates).await()
            Log.d("Repository", "Quest '${quest.title}' completed. ${quest.xp} XP added to user $userId.")
        } catch (e: Exception) {
            Log.e("Repository", "Error completing quest", e)
            throw e
        }
    }

    /**
     * NY FUNKTION: Hanterar logiken för en level up.
     * Den höjer level med 1 och drar bort den XP som krävdes för att gå upp.
     */
    suspend fun levelUpPlayer(familyId: String, userId: String, profile: PlayerProfile) {
        val playerDocRef = db.collection("families").document(familyId).collection("players").document(userId)
        val xpNeeded = profile.xpForNextSeasonalLevel
        try {
            val updates = mapOf(
                "seasonalLevel" to FieldValue.increment(1),
                "seasonalXp" to FieldValue.increment(-xpNeeded.toLong()) // Dra bort XP som användes
            )
            playerDocRef.update(updates).await()
            Log.d("Repository", "Player $userId leveled up!")
        } catch (e: Exception) {
            Log.e("Repository", "Error leveling up player", e)
            throw e
        }
    }


    // --- Family Functions ---
    suspend fun createFamily(familyName: String, user: FirebaseUser): String? {
        return try {
            val newFamilyRef = db.collection("families").document()
            val familyId = newFamilyRef.id
            val batch = db.batch()
            val family = Family(id = familyId, name = familyName, inviteCode = generateInviteCode())
            batch.set(newFamilyRef, family)

            val playerProfileRef = newFamilyRef.collection("players").document(user.uid)
            val playerProfile = PlayerProfile(
                id = user.uid,
                familyId = familyId,
                displayName = user.displayName ?: "Användare",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString()
            )
            batch.set(playerProfileRef, playerProfile)

            val userLookupRef = db.collection("users").document(user.uid)
            val userLookupData = mapOf("familyId" to familyId)
            batch.set(userLookupRef, userLookupData)

            val questsCollectionRef = newFamilyRef.collection("quests")
            for (quest in availableQuests) {
                val questDocRef = questsCollectionRef.document(quest.id)
                batch.set(questDocRef, quest)
            }

            batch.commit().await()
            familyId
        } catch (e: Exception) {
            Log.e("Repository", "Error creating family and initial quests", e)
            null
        }
    }

    suspend fun joinFamily(inviteCode: String, user: FirebaseUser): String? {
        return try {
            val query = db.collection("families")
                .whereEqualTo("inviteCode", inviteCode.uppercase().trim())
                .limit(1).get().await()

            if (query.isEmpty) return null
            val familyDoc = query.documents.first()
            val familyId = familyDoc.id

            val batch = db.batch()
            val playerProfileRef = db.collection("families").document(familyId).collection("players").document(user.uid)
            val playerProfile = PlayerProfile(
                id = user.uid,
                familyId = familyId,
                displayName = user.displayName ?: "Användare",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString()
            )
            val userLookupRef = db.collection("users").document(user.uid)
            val userLookupData = mapOf("familyId" to familyId)

            batch.set(playerProfileRef, playerProfile)
            batch.set(userLookupRef, userLookupData)

            batch.commit().await()
            familyId
        } catch (e: Exception) {
            Log.e("Repository", "Error joining family", e)
            null
        }
    }

    private fun generateInviteCode(length: Int = 6): String {
        return ('A'..'Z').plus('0'..'9').shuffled().take(length).joinToString("")
    }

    // --- Quest Functions ---
    suspend fun getQuestsForFamily(familyId: String): List<Quest> {
        return try {
            val snapshot = db.collection("families").document(familyId)
                .collection("quests").get().await()
            snapshot.toObjects(Quest::class.java)
        } catch (e: Exception) {
            Log.e("Repository", "Error getting quests for family", e)
            emptyList()
        }
    }
}
