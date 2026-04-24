package edu.gvsu.cis.multi_timer.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromPlayerStateList(value: List<PlayerActiveState>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toPlayerStateList(value: String): List<PlayerActiveState> {
        return Json.decodeFromString(value)
    }
}