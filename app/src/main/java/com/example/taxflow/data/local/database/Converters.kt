package com.example.taxflow.data.local.database

import androidx.room.TypeConverter
import com.example.shared2.domain.model.TransactionType
import kotlinx.datetime.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?):String? {
        return value?.toString()}

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {LocalDate.parse(it)}
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType?= value?.let{ TransactionType.valueOf(it)}
}