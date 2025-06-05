package com.example.mealshare_kotlin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mealshare_kotlin.model.Recipe
import com.example.mealshare_kotlin.model.Search
import com.example.mealshare_kotlin.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(
    username: String,
    onHomeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchResults: Search? = null,
    showSearchResults: Boolean = false,
    onRecipeClicked: (Recipe) -> Unit = {},
    onUserClicked: (User) -> Unit = {},
    onCloseSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val debouncedSearchQuery = remember { MutableStateFlow("") }

    // Handle search debouncing
    LaunchedEffect(searchText) {
        delay(300) // 300ms debounce delay
        if (searchText.isNotBlank()) {
            debouncedSearchQuery.value = searchText
        }
    }

    // Listen to debounced search query changes
    LaunchedEffect(Unit) {
        debouncedSearchQuery.collect { query ->
            if (query.isNotBlank()) {
                onSearchQueryChanged(query)
            }
        }
    }

    Column(modifier = modifier.zIndex(10f)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo/Home button
                Text(
                    text = "MealShare",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onHomeClicked() }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Search
                if (searchExpanded) {
                    AnimatedVisibility(
                        visible = searchExpanded,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search recipes or users...") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Search",
                                    modifier = Modifier.clickable {
                                        searchExpanded = false
                                        searchText = ""
                                        onCloseSearch()
                                    }
                                )
                            }
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { searchExpanded = true }
                    )
                }

                // User profile button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .clickable { onSettingsClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    val userInitial = username.firstOrNull()?.uppercase() ?: "?"
                    Text(
                        text = userInitial,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Display search results dropdown
        if (showSearchResults && searchResults != null) {
            SearchResultsDropdown(
                recipes = searchResults.recipes,
                users = searchResults.users,
                onRecipeClicked = onRecipeClicked,
                onUserClicked = onUserClicked
            )
        }
    }
}
