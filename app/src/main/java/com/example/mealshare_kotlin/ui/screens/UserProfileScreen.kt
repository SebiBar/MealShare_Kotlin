package com.example.mealshare_kotlin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mealshare_kotlin.model.Recipe
import com.example.mealshare_kotlin.ui.components.NavBar
import com.example.mealshare_kotlin.ui.navigation.Screen
import com.example.mealshare_kotlin.viewModel.AuthViewModel
import com.example.mealshare_kotlin.viewModel.SearchViewModel
import com.example.mealshare_kotlin.viewModel.UserProfileViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState(initial = null)
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val userRecipes by userProfileViewModel.userRecipes.collectAsState()
    val isLoading by userProfileViewModel.isLoading.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()
    val showSearchResults by searchViewModel.showSearchResults.collectAsState(initial = false)

    // Load user profile and recipes when the screen is displayed
    LaunchedEffect(userId) {
        userProfileViewModel.loadUserProfile(userId.toLongOrNull() ?: 0)
        userProfileViewModel.loadUserRecipes(userId.toLongOrNull() ?: 0)
    }

    Scaffold(
        topBar = {
            NavBar(
                username = user?.username ?: "User",
                onHomeClicked = {
                    // Navigate to logged in user's profile
                    user?.id?.let { userId ->
                        navController.navigate(Screen.UserProfile.createRoute(userId.toString())) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    } ?: navController.navigate(Screen.Home.route)
                },
                onSettingsClicked = { navController.navigate(Screen.Settings.route) },
                onSearchQueryChanged = { query ->
                    searchViewModel.search(query)
                }
            )

            // Show search results dropdown if there are results and search is active
            if (showSearchResults && searchResults != null) {
                com.example.mealshare_kotlin.ui.components.SearchResultsDropdown(
                    recipes = searchResults?.recipes ?: emptyList(),
                    users = searchResults?.users ?: emptyList(),
                    onRecipeClicked = { recipe ->
                        navController.navigate(Screen.RecipeDetails.createRoute(recipe.id.toString()))
                        searchViewModel.clearSearchResults()
                    },
                    onUserClicked = { user ->
                        navController.navigate(Screen.UserProfile.createRoute(user.id.toString()))
                        searchViewModel.clearSearchResults()
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (userProfile != null) {
                // User profile header
                Text(
                    text = "${userProfile?.username}'s Recipes",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (userRecipes.isEmpty()) {
                    Text(
                        text = "No recipes found",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp)
                    )
                } else {
                    // Display user recipes
                    RecipesList(
                        recipes = userRecipes,
                        onRecipeClicked = { recipe ->
                            navController.navigate(Screen.RecipeDetails.createRoute(recipe.id.toString()))
                        }
                    )
                }
            } else {
                Text(
                    text = "User not found",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun RecipesList(
    recipes: List<Recipe>,
    onRecipeClicked: (Recipe) -> Unit
) {
    LazyColumn {
        items(recipes) { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onRecipeClicked(recipe) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
