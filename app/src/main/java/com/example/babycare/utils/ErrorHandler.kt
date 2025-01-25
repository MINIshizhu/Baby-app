package com.example.babycare.utils

import android.content.Context
import com.example.babycare.R
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    private val context: Context
) {
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is NetworkException -> context.getString(R.string.error_network)
            is DatabaseException -> context.getString(R.string.error_database)
            is ValidationException -> context.getString(R.string.error_validation, throwable.message)
            else -> context.getString(R.string.error_unknown)
        }
    }
}

class NetworkException : Exception()
class DatabaseException : Exception()
class ValidationException(message: String) : Exception(message) 