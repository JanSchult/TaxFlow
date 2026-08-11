package com.example.shared2.data.local.database

import androidx.room.TypeConverter
import com.example.shared2.domain.model.TransactionType
import kotlinx.datetime.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDays()?.toLong()

    @TypeConverter
    fun toLocalDate(epochDays: Long?): LocalDate? = epochDays?.let { LocalDate.Companion.fromEpochDays(it.toInt()) }

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? = type?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? =
        value?.let { TransactionType.valueOf(it) }
}