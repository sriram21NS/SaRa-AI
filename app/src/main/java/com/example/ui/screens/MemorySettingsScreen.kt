package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
import com.example.ui.components.SakuraParticlesBackground
import com.example.ui.components.SaraAnimatedCharacter
import com.example.ui.components.SaraAnimationState
import com.example.ui.components.SaraSize
import com.example.ui.theme.SakuraPetalPink
import com.example.ui.theme.SakuraPrimaryLight
import com.example.viewmodel.SaraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorySettingsScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val personality by viewModel.personality.collectAsState()
    val responseLength by viewModel.responseLength.collectAsState()
    val searchGrounding by viewModel.searchGroundingEnabled.collectAsState()
    val aiModel by viewModel.aiModel.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    var newMemoryKey by remember { mutableStateOf("") }
    var newMemoryValue by remember { mutableStateOf("") }

    val personalities = listOf("Friendly", "Professional", "Teacher", "Creative", "Concise", "Detailed")
    val lengths = listOf("Short", "Medium", "Detailed")
    val models = listOf(
        Pair(GeminiApiClient.MODEL_FLASH, "Gemini 3.5 Flash (Balanced & Fast)"),
        Pair(GeminiApiClient.MODEL_PRO, "Gemini 3.1 Pro (Deep Reasoning)"),
        Pair(GeminiApiClient.MODEL_LITE, "Gemini 3.1 Flash-Lite (Ultra Fast)")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚙️", fontSize = 18.sp)
                        Text(
                            text = "SaRa Settings & Memory",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(SaraViewModel.Screen.HOME) },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SakuraParticlesBackground(modifier = Modifier.fillMaxSize(), particleCount = 12)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Character Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SaraAnimatedCharacter(
                        state = SaraAnimationState.IDLE,
                        size = SaraSize.MEDIUM,
                        isSpeaking = isSpeaking,
                        dialogueBubbleText = "Tune my personality to your preference! 🌸"
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SaRa Companion Config",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Customize how SaRa communicates, retains context, and searches information.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                // Voice Test Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔊 SaRa Voice Preview",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Hear SaRa speak in her gentle, friendly anime companion voice.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.speakResponse("Hi there! 🌸 I'm SaRa! It's so lovely to be your companion and guide.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimaryLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("test_voice_button")
                        ) {
                            Text("Test Voice ✨")
                        }
                    }
                }

                // Personality Style
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Personality", tint = SakuraPrimaryLight)
                            Text(
                                text = "Personality Style",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            personalities.take(3).forEach { p ->
                                val isSelected = p == personality
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) SakuraPrimaryLight.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SakuraPrimaryLight else Color.Transparent),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setPersonality(p) }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = p,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) SakuraPrimaryLight else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            personalities.drop(3).forEach { p ->
                                val isSelected = p == personality
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) SakuraPrimaryLight.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SakuraPrimaryLight else Color.Transparent),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setPersonality(p) }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = p,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) SakuraPrimaryLight else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Response Length
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Response Length",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            lengths.forEach { len ->
                                val isSelected = len == responseLength
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SakuraPrimaryLight else Color.Transparent)
                                        .clickable { viewModel.setResponseLength(len) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = len,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Google Search Grounding Toggle
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = SakuraPrimaryLight, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Google Search Grounding",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Ground SaRa's responses with live web results for real-time accuracy.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Switch(
                            checked = searchGrounding,
                            onCheckedChange = { viewModel.toggleSearchGrounding() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SakuraPrimaryLight
                            ),
                            modifier = Modifier.testTag("search_grounding_switch")
                        )
                    }
                }

                // AI Model Selection
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Gemini Model Engine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(10.dp))

                        models.forEach { (modelId, label) ->
                            val isSelected = modelId == aiModel
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SakuraPrimaryLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SakuraPrimaryLight else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { viewModel.setAiModel(modelId) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "●" else "○",
                                        color = if (isSelected) SakuraPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Long-Term Memory Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = "Memory", tint = SakuraPrimaryLight)
                            Text(
                                text = "SaRa's Memory Database",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Things SaRa remembers about you to personalize future conversations:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(Modifier.height(12.dp))

                        if (memories.isEmpty()) {
                            Text(
                                text = "No custom memories saved yet. Add one below! 🌸",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        } else {
                            memories.forEach { mem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SakuraPrimaryLight.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mem.key,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SakuraPrimaryLight
                                            )
                                        )
                                        Text(
                                            text = mem.value,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteMemory(mem.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete memory", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Add new memory inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newMemoryKey,
                                onValueChange = { newMemoryKey = it },
                                placeholder = { Text("Topic (e.g. Coding)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = newMemoryValue,
                                onValueChange = { newMemoryValue = it },
                                placeholder = { Text("Detail (e.g. Prefers Kotlin)") },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (newMemoryKey.isNotBlank() && newMemoryValue.isNotBlank()) {
                                    viewModel.addMemory(newMemoryKey, newMemoryValue)
                                    newMemoryKey = ""
                                    newMemoryValue = ""
                                    Toast.makeText(context, "Memory saved! 🌸", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = newMemoryKey.isNotBlank() && newMemoryValue.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("add_memory_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimaryLight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save Memory")
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
