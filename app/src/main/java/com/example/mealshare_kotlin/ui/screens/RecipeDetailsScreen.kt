package com.example.mealshare_kotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mealshare_kotlin.model.Recipe
import com.example.mealshare_kotlin.ui.components.NavBar
import com.example.mealshare_kotlin.ui.navigation.Screen
import com.example.mealshare_kotlin.viewModel.AuthViewModel
import com.example.mealshare_kotlin.viewModel.RecipeViewModel
import com.example.mealshare_kotlin.viewModel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailsScreen(
    recipeId: Long,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    recipeViewModel: RecipeViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState(initial = null)
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()
    val showSearchResults by searchViewModel.showSearchResults.collectAsState(initial = false)
    
    val recipe by recipeViewModel.recipe.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val error by recipeViewModel.error.collectAsState()
    val isEditing by recipeViewModel.isEditing.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isCurrentUserOwner by remember { mutableStateOf(false) }

    // Local state for edited recipe values
    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedPrepTime by remember { mutableStateOf("") }
    var editedCookTime by remember { mutableStateOf("") }
    var editedServings by remember { mutableStateOf("") }
    var editedCalories by remember { mutableStateOf("") }
    var editedProtein by remember { mutableStateOf("") }
    var editedCarbs by remember { mutableStateOf("") }
    var editedFat by remember { mutableStateOf("") }
    var editedLink by remember { mutableStateOf("") }
    
    LaunchedEffect(recipeId) {
        recipeViewModel.getRecipeById(recipeId)
    }
    
    LaunchedEffect(recipe) {
        recipe?.let {
            editedTitle = it.title
            editedDescription = it.description
            editedPrepTime = it.prepTime.toString()
            editedCookTime = it.cookTime.toString()
            editedServings = it.servingSize.toString()
            editedCalories = it.calories.toString()
            editedProtein = it.protein.toString()
            editedCarbs = it.carbs.toString()
            editedFat = it.fat.toString()
            editedLink = it.link?.toString() ?: ""
            
            coroutineScope.launch {
                isCurrentUserOwner = recipeViewModel.isCurrentUserRecipeOwner()
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Recipe",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Are you sure you want to delete this recipe? This action cannot be undone."
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                recipeViewModel.deleteRecipe(recipeId) {
                                    showDeleteConfirmDialog = false
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NavBar(
                username = user?.username ?: "User",
                onHomeClicked = {
                    // Navigate to logged in user's profile
                    user?.id?.let { userId ->
                        navController.navigate(Screen.UserProfile.createRoute(userId.toString()))
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
        ) {
            // Back button and title row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    text = "Back",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            if (isLoading && recipe == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null && recipe == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error ?: "Unknown error occurred")
                }
            } else {
                recipe?.let { recipeData ->
                    // Recipe header with edit/delete buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedTitle,
                                onValueChange = { editedTitle = it },
                                label = { Text("Title") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = recipeData.title,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        if (isCurrentUserOwner) {
                            Row {
                                if (isEditing) {
                                    Button(
                                        onClick = {
                                            val updatedRecipe = recipeData.copy(
                                                title = editedTitle,
                                                description = editedDescription,
                                                prepTime = editedPrepTime.toLongOrNull() ?: 0,
                                                cookTime = editedCookTime.toLongOrNull() ?: 0,
                                                servingSize = editedServings.toLongOrNull() ?: 0,
                                                calories = editedCalories.toLongOrNull() ?: 0,
                                                protein = editedProtein.toLongOrNull() ?: 0,
                                                carbs = editedCarbs.toLongOrNull() ?: 0,
                                                fat = editedFat.toLongOrNull() ?: 0,
                                                link = editedLink.ifEmpty { null }
                                            )
                                            recipeViewModel.updateRecipe(updatedRecipe) {
                                                // Success callback
                                            }
                                        }
                                    ) {
                                        Text("Save")
                                    }
                                    
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    
                                    TextButton(
                                        onClick = { recipeViewModel.toggleEditMode() }
                                    ) {
                                        Text("Cancel")
                                    }
                                } else {
                                    IconButton(onClick = { recipeViewModel.toggleEditMode() }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Recipe"
                                        )
                                    }
                                    
                                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Recipe"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Author
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "by ${recipeData.user.username}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFE29E21)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Description Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedDescription,
                                    onValueChange = { editedDescription = it },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = recipeData.description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Prep Time and Cook Time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Prep Time",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedPrepTime,
                                            onValueChange = { editedPrepTime = it },
                                            label = { Text("Minutes") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.prepTime} mins",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Cook Time",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedCookTime,
                                            onValueChange = { editedCookTime = it },
                                            label = { Text("Minutes") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.cookTime} mins",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Servings
                            Column {
                                Text(
                                    text = "Servings",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editedServings,
                                        onValueChange = { editedServings = it },
                                        label = { Text("Servings") },
                                        modifier = Modifier.fillMaxWidth(0.5f)
                                    )
                                } else {
                                    Text(
                                        text = "${recipeData.servingSize}",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Nutrition Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Calories and Protein
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Calories",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedCalories,
                                            onValueChange = { editedCalories = it },
                                            label = { Text("kcal") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.calories} kcal",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Protein",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedProtein,
                                            onValueChange = { editedProtein = it },
                                            label = { Text("g") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.protein}g",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Carbs and Fat
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Carbs",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedCarbs,
                                            onValueChange = { editedCarbs = it },
                                            label = { Text("g") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.carbs}g",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Fat",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedFat,
                                            onValueChange = { editedFat = it },
                                            label = { Text("g") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = "${recipeData.fat}g",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Link section
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedLink,
                                    onValueChange = { editedLink = it },
                                    label = { Text("Link URL") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = recipeData.link?.toString() ?: "No link provided",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    
                    // Ingredients list could be added here
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}