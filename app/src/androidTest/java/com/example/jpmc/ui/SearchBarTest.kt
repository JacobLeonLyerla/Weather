package com.example.jpmc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jpmc.viewmodel.WeatherViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var viewModel: WeatherViewModel  // Declare as lateinit var

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun searchBar_InitialDisplay() {

        // When
        composeTestRule.setContent {
            TestSearchBar(viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Search City").assertIsDisplayed()
    }

    @Test
    fun searchBar_TypeText() {
        // Given
        val textToType = "New York"

        // When
        composeTestRule.setContent {
            TestSearchBar(viewModel)
        }

        // Then
        composeTestRule
            .onNodeWithText("Search City")
            .performTextInput(textToType)

        composeTestRule.onNodeWithText(textToType).assertIsDisplayed()
    }


}

@Composable
fun TestSearchBar(viewModel: WeatherViewModel) {
    SearchBar(viewModel = viewModel)
}
