package com.example.mealshare_kotlin.ui.screens

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
import com.example.mealshare_kotlin.ui.components.NavBar
import com.example.mealshare_kotlin.ui.navigation.Screen
import com.example.mealshare_kotlin.viewModel.AuthViewModel
import com.example.mealshare_kotlin.viewModel.SearchViewModel

@Composable
fun RecipeDetailsScreen(
    recipeId: Long,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState(initial = null)

    Scaffold(
        topBar = {
            NavBar(
                username = user?.username ?: "User",
                onHomeClicked = { navController.navigate(Screen.Home.route) },
                onSettingsClicked = { navController.navigate(Screen.Settings.route) },
                onSearchQueryChanged = { query ->
                    searchViewModel.search(query)
                    // For recipe details screen, we'll navigate to Home to show search results
                    if (query.isNotBlank()) {
                        navController.navigate(Screen.Home.route)
                    }
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
            Text(
                text = "Recipe Details",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recipe ID: $recipeId",
                style = MaterialTheme.typography.bodyLarge
            )

            // Fetch and display recipe details here
        }
    }
}