package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.GuideModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningModeScreen
import com.example.ui.screens.MemorySettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SakuraPrimaryLight
import com.example.viewmodel.SaraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val saraViewModel: SaraViewModel = viewModel()

                // Seed initial sample memory if empty
                LaunchedEffect(Unit) {
                    if (saraViewModel.memories.value.isEmpty()) {
                        saraViewModel.addMemory("Learning Focus", "Android Jetpack Compose & Python", "Education")
                        saraViewModel.addMemory("Guidance Style", "Values clear step-by-step breakdowns", "Preference")
                    }
                }

                SaRaMainApp(viewModel = saraViewModel)
            }
        }
    }
}

private data class NavItem(
    val screen: SaraViewModel.Screen,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun SaRaMainApp(viewModel: SaraViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    val navItems = listOf(
        NavItem(SaraViewModel.Screen.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItem(SaraViewModel.Screen.CHAT, "Chat", Icons.Default.ChatBubble, "nav_chat"),
        NavItem(SaraViewModel.Screen.GUIDE, "Guide", Icons.Default.Explore, "nav_guide"),
        NavItem(SaraViewModel.Screen.LEARNING, "Learn", Icons.Default.School, "nav_learn"),
        NavItem(SaraViewModel.Screen.SETTINGS, "Settings", Icons.Default.Settings, "nav_settings")
    )

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SakuraPrimaryLight,
                            selectedTextColor = SakuraPrimaryLight,
                            indicatorColor = SakuraPrimaryLight.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_crossfade"
            ) { screen ->
                when (screen) {
                    SaraViewModel.Screen.HOME -> HomeScreen(viewModel = viewModel)
                    SaraViewModel.Screen.CHAT -> ChatScreen(viewModel = viewModel)
                    SaraViewModel.Screen.GUIDE -> GuideModeScreen(viewModel = viewModel)
                    SaraViewModel.Screen.LEARNING -> LearningModeScreen(viewModel = viewModel)
                    SaraViewModel.Screen.SETTINGS -> MemorySettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name! 🌸 Welcome to SaRa.", modifier = modifier)
}

