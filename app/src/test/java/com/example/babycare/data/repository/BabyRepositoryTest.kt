package com.example.babycare.data.repository

import com.example.babycare.data.dao.BabyDao
import com.example.babycare.data.entity.Baby
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BabyRepositoryTest {
    private lateinit var babyDao: BabyDao
    private lateinit var repository: BabyRepository

    @Before
    fun setUp() {
        babyDao = mockk()
        repository = BabyRepository(babyDao)
    }

    @Test
    fun `getAllBabies returns correct data`() = runTest {
        // Given
        val babies = listOf(
            Baby(id = 1, name = "测试1", gender = 0, birthday = 0),
            Baby(id = 2, name = "测试2", gender = 1, birthday = 0)
        )
        coEvery { babyDao.getAllBabies() } returns flowOf(babies)

        // When
        val result = repository.getAllBabies()

        // Then
        result.collect { 
            assertEquals(babies, it)
        }
    }
} 