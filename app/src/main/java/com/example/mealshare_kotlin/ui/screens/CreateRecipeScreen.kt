package com.example.mealshare_kotlin.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mealshare_kotlin.model.Ingredient
import com.example.mealshare_kotlin.model.Recipe
import com.example.mealshare_kotlin.ui.components.NavBar
import com.example.mealshare_kotlin.ui.navigation.Screen
import com.example.mealshare_kotlin.viewModel.AuthViewModel
import com.example.mealshare_kotlin.viewModel.CreateRecipeViewModel
import com.example.mealshare_kotlin.viewModel.SearchViewModel

@Composable
fun CreateRecipeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    createRecipeViewModel: CreateRecipeViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState(initial = null)
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()
    val showSearchResults by searchViewModel.showSearchResults.collectAsState(initial = false)
    
    val isSaving by createRecipeViewModel.isSaving.collectAsState()
    val error by createRecipeViewModel.error.collectAsState()
    
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                
                Text(
                    text = "Create New Recipe",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Recipe Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF302D40)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF302D40)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Prep Time and Cook Time
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Prep Time (mins)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = prepTime,
                                onValueChange = { prepTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Cook Time (mins)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = cookTime,
                                onValueChange = { cookTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Servings
                    Column {
                        Text(
                            text = "Servings",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        OutlinedTextField(
                            value = servingSize,
                            onValueChange = { servingSize = it },
                            modifier = Modifier.fillMaxWidth(0.5f),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF302D40)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Nutrition (per serving)",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calories and Protein
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Calories (kcal)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = calories,
                                onValueChange = { calories = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Protein (g)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = protein,
                                onValueChange = { protein = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Carbs and Fat
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Carbs (g)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = carbs,
                                onValueChange = { carbs = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Fat (g)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            OutlinedTextField(
                                value = fat,
                                onValueChange = { fat = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Link Card
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF302D40)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recipe Link",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Link URL (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message if any
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save button
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        user?.let { currentUser ->
                            val newRecipe = Recipe(
                                id = 0, // Will be assigned by the server
                                title = title,
                                description = description,
                                prepTime = prepTime.toLongOrNull() ?: 0,
                                cookTime = cookTime.toLongOrNull() ?: 0,
                                servingSize = servingSize.toLongOrNull() ?: 0,
                                calories = calories.toLongOrNull() ?: 0,
                                protein = protein.toLongOrNull() ?: 0,
                                carbs = carbs.toLongOrNull() ?: 0,
                                fat = fat.toLongOrNull() ?: 0,
                                link = if (link.isBlank()) null else link,
                                ingredients = emptyList(), // No ingredients UI yet
                                user = currentUser
                            )
                            
                            createRecipeViewModel.createRecipe(newRecipe) {
                                // Navigate back to user profile on success
                                navController.popBackStack()
                            }
                        }
                    },
                    enabled = !isSaving && title.isNotBlank() && description.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(4.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Recipe")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
