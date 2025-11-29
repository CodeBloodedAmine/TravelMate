package com.example.travelmate.data.room

import androidx.room.TypeConverter
import com.example.travelmate.data.models.ActivityCategory
import com.example.travelmate.data.models.BudgetCategory
import com.example.travelmate.data.models.ItineraryItem
import com.example.travelmate.data.models.MessageType
import com.example.travelmate.data.models.NotificationType
import com.example.travelmate.data.models.UserRole
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromItineraryItemList(value: String): List<ItineraryItem> {
        val listType = object : TypeToken<List<ItineraryItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toItineraryItemList(list: List<ItineraryItem>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }

    @TypeConverter
    fun toUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun fromActivityCategory(value: String): ActivityCategory {
        return ActivityCategory.valueOf(value)
    }

    @TypeConverter
    fun toActivityCategory(category: ActivityCategory): String {
        return category.name
    }

    @TypeConverter
    fun fromBudgetCategory(value: String): BudgetCategory {
        return BudgetCategory.valueOf(value)
    }

    @TypeConverter
    fun toBudgetCategory(category: BudgetCategory): String {
        return category.name
    }

    @TypeConverter
    fun fromMessageType(value: String): MessageType {
        return MessageType.valueOf(value)
    }

    @TypeConverter
    fun toMessageType(type: MessageType): String {
        return type.name
    }

    @TypeConverter
    fun fromNotificationType(value: String): NotificationType {
        return NotificationType.valueOf(value)
    }

    @TypeConverter
    fun toNotificationType(type: NotificationType): String {
        return type.name
    }
}

