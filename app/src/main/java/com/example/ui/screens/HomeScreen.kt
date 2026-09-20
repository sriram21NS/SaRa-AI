package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SakuraParticlesBackground
import com.example.ui.components.SaraAnimatedCharacter
import com.example.ui.components.SaraAnimationState
import com.example.ui.components.SaraSize
import com.example.ui.theme.SakuraPetalPink
import com.example.ui.theme.SakuraPrimaryLight
import com.example.viewmodel.SaraViewModel

private data class QuickAction(
    val title: String,
    val icon: String,
    val mode: String,
    val initialPrompt: String
)

@Composable
fun HomeScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val characterState by viewModel.characterState.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    val quickActions = listOf(
        QuickAction("Coding Help", "💻", "Coding", "Hi SaRa! 💻 I'd love some help with coding and programming concepts."),
        QuickAction("Study Assistant", "📚", "Education", "Hi SaRa! 📚 Can you be my study guide and help me learn step-by-step?"),
        QuickAction("Writing Assistant", "📝", "Writing", "Hi SaRa! 📝 Can you help me polish, structure, and write high quality content?"),
        QuickAction("Idea Generator", "💡", "Creativity", "Hi SaRa! 💡 Let's brainstorm some innovative ideas together!"),
        QuickAction("Research Guide", "🔍", "Research", "Hi SaRa! 🔍 Can you help me research and break down this topic?"),
        QuickAction("Creative Assistant", "🎨", "Creativity", "Hi SaRa! 🎨 Let's explore design, art, and creative problem solving!"),
        QuickAction("Explain a Topic", "🧠", "Education", "Hi SaRa! 🧠 Can you explain a complex concept to me in simple terms?"),
        QuickAction("Data Assistant", "📊", "Data", "Hi SaRa! 📊 Can you help me organize, analyze, and understand data?")
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Ambient Sakura Particles
        SakuraParticlesBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SakuraPrimaryLight.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.6f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🌸", fontSize = 14.sp)
                        Text(
                            text = "SaRa — AI Friendly Guide",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakuraPrimaryLight
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Animated SaRa Character Hero
            SaraAnimatedCharacter(
                state = characterState,
                size = SaraSize.HERO,
                isSpeaking = isSpeaking,
                onCharacterTap = {
                    viewModel.setCharacterState(SaraAnimationState.GREETING)
                }
            )

            Spacer(Modifier.height(16.dp))

            // Hero Greeting Text
            Text(
                text = "Hello, I'm SaRa",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Your friendly AI companion for learning,\ncreating, exploring and solving problems.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Primary Call to Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.startNewConversation()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("start_chatting_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimaryLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Start Chat",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Start Chatting", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.navigateTo(SaraViewModel.Screen.GUIDE)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("explore_sara_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraPrimaryLight),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SakuraPrimaryLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Explore",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guide Mode", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(28.dp))

            // Quick Actions Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "✨ Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Quick Actions Grid (2 columns)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (chunk in quickActions.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (action in chunk) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    SakuraPetalPink.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.setKnowledgeMode(action.mode)
                                        viewModel.startNewConversation(
                                            title = "${action.icon} ${action.title}",
                                            initialPrompt = action.initialPrompt,
                                            mode = action.mode
                                        )
                                    }
                                    .testTag("quick_action_${action.title.lowercase().replace(" ", "_")}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                                ) {
                                    Text(text = action.icon, fontSize = 20.sp)
                                    Text(
                                        text = action.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
