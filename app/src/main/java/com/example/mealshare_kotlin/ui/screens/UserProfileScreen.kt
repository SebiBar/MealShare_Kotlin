package com.example.mealshare_kotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    
    // Determine if the currently logged in user is viewing their own profile
    val isCurrentUserProfile = userId == user?.id?.toString()

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
                },
                searchResults = searchResults,
                showSearchResults = showSearchResults,
                onRecipeClicked = { recipe ->
                    navController.navigate(Screen.RecipeDetails.createRoute(recipe.id.toString()))
                    searchViewModel.clearSearchResults()
                },
                onUserClicked = { user ->
                    navController.navigate(Screen.UserProfile.createRoute(user.id.toString()))
                    searchViewModel.clearSearchResults()
                },
                onCloseSearch = {
                    searchViewModel.clearSearch()
                }
            )
        },
        floatingActionButton = {
            // Only show the add recipe FAB if the current user is viewing their own profile
            if (isCurrentUserProfile) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateRecipe.route) },
                    containerColor = Color(0xFFE29E21) // Yellow color from the image
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Recipe",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // User profile header
            if (userProfile != null) {
                Text(
                    text = "${userProfile?.username}'s Recipes",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (userRecipes.isEmpty()) {
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
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
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
}

@Composable
private fun RecipesList(
    recipes: List<Recipe>,
    onRecipeClicked: (Recipe) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onRecipeClicked(recipe) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF302D40)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Add an arrow icon to indicate clickability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View Recipe",
                            tint = Color(0xFFE29E21),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
