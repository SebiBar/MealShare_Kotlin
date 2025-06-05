package com.example.mealshare_kotlin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.mealshare_kotlin.ui.screens.CreateRecipeScreen
import com.example.mealshare_kotlin.ui.screens.HomeScreen
import com.example.mealshare_kotlin.ui.screens.LoginScreen
import com.example.mealshare_kotlin.ui.screens.RecipeDetailsScreen
import com.example.mealshare_kotlin.ui.screens.RegisterScreen
import com.example.mealshare_kotlin.ui.screens.SettingsScreen
import com.example.mealshare_kotlin.ui.screens.UserProfileScreen
import com.example.mealshare_kotlin.viewModel.AuthViewModel

/**
 * Navigation destinations used in the app
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object RecipeDetails : Screen("recipe_details/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_details/$recipeId"
    }
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
    object CreateRecipe : Screen("create_recipe")
}

/**
 * Main navigation component for the application
 * Handles conditional navigation based on authentication status
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onScreenWithNavBarChange: (Boolean) -> Unit = {}
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)

    // Track when we're on a screen with NavBar
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    val isScreenWithNavBar = currentDestination != Screen.Login.route && currentDestination != Screen.Register.route

    // Notify the parent about whether current screen has NavBar
    LaunchedEffect(isScreenWithNavBar) {
        onScreenWithNavBarChange(isScreenWithNavBar)
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route,
        modifier = modifier
    ) {
        // Login screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        //Settings screen
        composable(Screen.Settings.route){
            SettingsScreen(navController = navController)
        }

        // Recipe details screen
        composable(
            route = Screen.RecipeDetails.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull()
            if (recipeId != null) {
                RecipeDetailsScreen(
                    recipeId = recipeId,
                    navController = navController
                )
            }
        }        // User profile screen
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfileScreen(
                    userId = userId,
                    navController = navController
                )
            }
        }
        
        // Create recipe screen
        composable(Screen.CreateRecipe.route) {
            CreateRecipeScreen(
                navController = navController
            )
        }
    }
}
