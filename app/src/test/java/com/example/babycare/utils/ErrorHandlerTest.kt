package com.example.babycare.utils

import android.content.Context
import com.example.babycare.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class ErrorHandlerTest {
    private lateinit var context: Context
    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setup() {
        context = mockk {
            every { getString(R.string.error_network) } returns "网络连接错误"
            every { getString(R.string.error_database) } returns "数据库操作错误"
            every { getString(R.string.error_validation, any()) } returns "验证错误: test"
            every { getString(R.string.error_unknown) } returns "未知错误"
        }
        errorHandler = ErrorHandler(context)
    }

    @Test
    fun `test network error message`() {
        val message = errorHandler.getErrorMessage(NetworkException())
        assertThat(message).isEqualTo("网络连接错误")
    }

    @Test
    fun `test database error message`() {
        val message = errorHandler.getErrorMessage(DatabaseException())
        assertThat(message).isEqualTo("数据库操作错误")
    }

    @Test
    fun `test validation error message`() {
        val message = errorHandler.getErrorMessage(ValidationException("test"))
        assertThat(message).isEqualTo("验证错误: test")
    }

    @Test
    fun `test unknown error message`() {
        val message = errorHandler.getErrorMessage(Exception())
        assertThat(message).isEqualTo("未知错误")
    }
} 