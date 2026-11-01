package com.example.util

import androidx.room.TypeConverter
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromProductCategory(value: ProductCategory): String = value.name

    @TypeConverter
    fun toProductCategory(value: String): ProductCategory = ProductCategory.valueOf(value)

    @TypeConverter
    fun fromFinishType(value: FinishType): String = value.name

    @TypeConverter
    fun toFinishType(value: String): FinishType = FinishType.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
}
