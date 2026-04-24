package edu.gvsu.cis.multi_timer.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playset (
    val name: String = "Playset",
    val playerCount: Int = 0,
    var fullBackgroundColor: Color = Color(255f, 255f, 255f),
    var fullBackgroundImage: String = "Null",
    val counterTypes: MutableList<Unit> = mutableListOf(Unit),
    @PrimaryKey(autoGenerate = true) val playsetID: Int = 0
)

@Entity
data class Player (
    var name: String = "Player",
    var profilePicture: String = "Null",
    var playerBackgroundPicture: String = "Null",
    var playerBackgroundColor: Color = Color(255f, 255f, 255f),
    @PrimaryKey(autoGenerate = true) val playerID: Int = 0
)

@Entity
data class ActiveGameState (
    val counterValueList: MutableList<Float.Companion> = mutableListOf(Float),
    @PrimaryKey(autoGenerate = true) val activeGameStateID: Int = 0
)