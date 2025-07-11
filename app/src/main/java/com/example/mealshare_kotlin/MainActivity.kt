package com.example.mealshare_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mealshare_kotlin.ui.navigation.AppNavigation
import com.example.mealshare_kotlin.ui.theme.MealShare_KotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealShare_KotlinTheme {
                MealShareApp()
            }
        }
    }
}

@Composable
fun MealShareApp() {
    val navController = rememberNavController()
    var hasNavBar by remember { mutableStateOf(false) }

    MealShare_KotlinTheme(useNavBarStatusBarColor = hasNavBar) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onScreenWithNavBarChange = { screenHasNavBar ->
                    hasNavBar = screenHasNavBar
                }
            )
        }
    }
}