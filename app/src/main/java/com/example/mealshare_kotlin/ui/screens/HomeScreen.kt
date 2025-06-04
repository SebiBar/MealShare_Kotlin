package com.example.mealshare_kotlin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mealshare_kotlin.model.Search
import com.example.mealshare_kotlin.ui.components.NavBar
import com.example.mealshare_kotlin.ui.navigation.Screen
import com.example.mealshare_kotlin.viewModel.AuthViewModel
import com.example.mealshare_kotlin.viewModel.SearchViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState(initial = null)
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            NavBar(
                username = user?.username ?: "User",
                onHomeClicked = { navController.navigate(Screen.Home.route) },
                onSettingsClicked = { navController.navigate(Screen.Settings.route) },
                onSearchQueryChanged = { query ->
                    searchViewModel.search(query)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Display search results if any
            if (isSearching) {
                Text(
                    text = "Searching...",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (searchResults != null) {
                DisplaySearchResults(searchResults!!, navController)
            } else {
                // Regular home screen content goes here
                Text(
                    text = "Welcome to MealShare",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Home screen content will be displayed here",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun DisplaySearchResults(
    searchResults: Search,
    navController: NavController
) {
    Column {
        Text(
            text = "Search Results",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display recipe results
        if (searchResults.recipes.isNotEmpty()) {
            Text(
                text = "Recipes (${searchResults.recipes.size})",
                style = MaterialTheme.typography.titleMedium
            )

            searchResults.recipes.forEach { recipe ->
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        // Navigate to recipe details when clicked
                        .clickable {
                            navController.navigate(Screen.RecipeDetails.createRoute(recipe.id.toString()))
                        }
                )
            }
        }

        // Display user results
        if (searchResults.users.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Users (${searchResults.users.size})",
                style = MaterialTheme.typography.titleMedium
            )

            searchResults.users.forEach { user ->
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                    // Could add navigation to user profile here if needed
                )
            }
        }
    }
}