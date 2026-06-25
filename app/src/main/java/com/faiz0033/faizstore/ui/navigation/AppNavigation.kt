package com.faiz0033.faizstore.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faiz0033.faizstore.ui.screens.login.LoginScreen
import com.faiz0033.faizstore.ui.screens.profile.ProfileScreen
import com.faiz0033.faizstore.ui.viewmodel.AuthState
import com.faiz0033.faizstore.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AuthState.Authenticated -> {
            val container = (androidx.compose.ui.platform.LocalContext.current.applicationContext as com.faiz0033.faizstore.FaizStoreApplication).container
            
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = modifier
            ) {
                composable("home") {
                    com.faiz0033.faizstore.presentation.home.HomeScreen(
                        laptopRepository = container.laptopRepository,
                        onNavigateToDetail = { id -> navController.navigate("detail/$id") },
                        onNavigateToAdd = { navController.navigate("add") },
                        onNavigateToProfile = { navController.navigate("profile") }
                    )
                }
                composable("detail/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: return@composable
                    com.faiz0033.faizstore.presentation.detail.DetailScreen(
                        laptopId = id,
                        laptopRepository = container.laptopRepository,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEdit = { editId -> navController.navigate("edit/$editId") }
                    )
                }
                composable("add") {
                    com.faiz0033.faizstore.presentation.add_edit.AddEditLaptopScreen(
                        laptopRepository = container.laptopRepository,
                        imageUploadRepository = container.imageUploadRepository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("edit/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: return@composable
                    com.faiz0033.faizstore.presentation.add_edit.AddEditLaptopScreen(
                        laptopRepository = container.laptopRepository,
                        imageUploadRepository = container.imageUploadRepository,
                        onNavigateBack = { navController.popBackStack() },
                        laptopId = id
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        viewModel = authViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
        is AuthState.Unauthenticated -> {
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = modifier
            ) {
                composable("login") {
                    LoginScreen(viewModel = authViewModel)
                }
            }
        }
    }
}
