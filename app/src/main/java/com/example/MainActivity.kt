package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.local.AppDatabase
import com.example.data.local.SessionManager
import com.example.data.model.UserRole
import com.example.data.repository.ShopRepository
import com.example.ui.AuthViewModel
import com.example.ui.OwnerViewModel
import com.example.ui.SalesmanViewModel
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.SalesmanFloorScreen
import com.example.ui.theme.MJGarmentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = ShopRepository(database.shopDao())
        val sessionManager = SessionManager(this)

        setContent {
            MJGarmentsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    MJGarmentsApp(
                        repository = repository,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}

@Composable
fun MJGarmentsApp(
    repository: ShopRepository,
    sessionManager: SessionManager
) {
    val authViewModel = remember { AuthViewModel(repository, sessionManager) }
    val salesmanViewModel = remember { SalesmanViewModel(repository) }
    val ownerViewModel = remember { OwnerViewModel(repository) }

    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            salesmanViewModel.setSalesman(currentUser)
        }
    }

    AnimatedContent(
        targetState = currentUser,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "user_screen_routing"
    ) { user ->
        when {
            user == null -> {
                LoginScreen(
                    authViewModel = authViewModel
                )
            }
            user.role.equals(UserRole.OWNER.name, ignoreCase = true) -> {
                OwnerDashboardScreen(
                    ownerViewModel = ownerViewModel,
                    currentUser = user,
                    onLogoutClick = { authViewModel.logout() }
                )
            }
            else -> {
                SalesmanFloorScreen(
                    salesmanViewModel = salesmanViewModel,
                    onLogoutClick = { authViewModel.logout() }
                )
            }
        }
    }
}
