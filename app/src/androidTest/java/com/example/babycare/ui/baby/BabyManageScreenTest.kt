package com.example.babycare.ui.baby

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.babycare.util.TestRule
import org.junit.Test

class BabyManageScreenTest : TestRule() {
    @Test
    fun addBaby_Success() {
        // 打开添加宝宝对话框
        composeTestRule.onNodeWithContentDescription("添加宝宝").performClick()
        
        // 输入宝宝信息
        composeTestRule.onNodeWithText("姓名").performTextInput("测试宝宝")
        composeTestRule.onNodeWithText("男").performClick()
        
        // 点击保存
        composeTestRule.onNodeWithText("保存").performClick()
        
        // 验证结果
        composeTestRule.onNodeWithText("测试宝宝").assertIsDisplayed()
    }

    @Test
    fun deleteBaby_ShowsConfirmation() {
        // 点击删除按钮
        composeTestRule.onNodeWithContentDescription("删除").performClick()
        
        // 验证确认对话框显示
        composeTestRule.onNodeWithText("确认删除").assertIsDisplayed()
    }
} 