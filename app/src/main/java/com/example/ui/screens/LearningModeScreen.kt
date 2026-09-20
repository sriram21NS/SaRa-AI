package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MarkdownText
import com.example.ui.components.SakuraParticlesBackground
import com.example.ui.components.SaraAnimatedCharacter
import com.example.ui.components.SaraAnimationState
import com.example.ui.components.SaraSize
import com.example.ui.theme.SakuraPetalPink
import com.example.ui.theme.SakuraPrimaryLight
import com.example.viewmodel.SaraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningModeScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.selectedTopic.collectAsState()
    val difficulty by viewModel.learningDifficulty.collectAsState()
    val selectedOption by viewModel.quizSelectedOption.collectAsState()
    val isSubmitted by viewModel.quizSubmitted.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    val currentContent = when (difficulty) {
        "Intermediate" -> topic.intermediateContent
        "Advanced" -> topic.advancedContent
        else -> topic.beginnerContent
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📚", fontSize = 18.sp)
                        Text(
                            text = "SaRa Learning Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(SaraViewModel.Screen.HOME) },
                        modifier = Modifier.testTag("learning_back_button")
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
            SakuraParticlesBackground(modifier = Modifier.fillMaxSize(), particleCount = 14)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Topic Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.learningTopics.forEach { t ->
                        val isSelected = t.id == topic.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectLearningTopic(t) },
                            label = {
                                Text(
                                    text = "${t.icon} ${t.title}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SakuraPrimaryLight.copy(alpha = 0.2f),
                                selectedLabelColor = SakuraPrimaryLight
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) SakuraPrimaryLight else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Difficulty Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = diff == difficulty
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SakuraPrimaryLight else Color.Transparent)
                                .clickable { viewModel.setLearningDifficulty(diff) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diff,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Hero Header with SaRa Character
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SaraAnimatedCharacter(
                        state = characterState,
                        size = SaraSize.MEDIUM,
                        isSpeaking = isSpeaking,
                        dialogueBubbleText = "Let's master $difficulty concepts! 🌸"
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${topic.icon} ${topic.title}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Level: $difficulty • ${topic.category}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakuraPrimaryLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Lesson Content Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "📖", fontSize = 16.sp)
                            Text(
                                text = "Interactive Lesson Overview",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        MarkdownText(content = currentContent)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Interactive Quiz Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPrimaryLight.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = "Quiz", tint = SakuraPrimaryLight)
                            Text(
                                text = "🌸 SaRa's Checkpoint Quiz",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakuraPrimaryLight
                                )
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = topic.quiz.question,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        // Quiz Options
                        topic.quiz.options.forEachIndexed { index, option ->
                            val isChosen = selectedOption == index
                            val isCorrect = index == topic.quiz.correctIndex

                            val backgroundColor = when {
                                isSubmitted && isCorrect -> Color(0xFF10B981).copy(alpha = 0.15f)
                                isSubmitted && isChosen && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                isChosen -> SakuraPrimaryLight.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }

                            val borderColor = when {
                                isSubmitted && isCorrect -> Color(0xFF10B981)
                                isSubmitted && isChosen && !isCorrect -> MaterialTheme.colorScheme.error
                                isChosen -> SakuraPrimaryLight
                                else -> Color.Transparent
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = backgroundColor,
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !isSubmitted) {
                                        viewModel.selectQuizOption(index)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isChosen,
                                        onClick = { viewModel.selectQuizOption(index) },
                                        enabled = !isSubmitted,
                                        colors = RadioButtonDefaults.colors(selectedColor = SakuraPrimaryLight)
                                    )

                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSubmitted && isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                    } else if (isSubmitted && isChosen && !isCorrect) {
                                        Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        if (!isSubmitted) {
                            Button(
                                onClick = { viewModel.submitQuiz() },
                                enabled = selectedOption != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("submit_quiz_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimaryLight)
                            ) {
                                Text("Check Answer ✨", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Explanation Card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SakuraPrimaryLight.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (selectedOption == topic.quiz.correctIndex) "🎉 Spot on! Great job!" else "💡 Good try! Here's the key idea:",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SakuraPrimaryLight
                                        )
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = topic.quiz.explanation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Chat about this lesson
                Button(
                    onClick = {
                        viewModel.startNewConversation(
                            title = "Learning: ${topic.title}",
                            initialPrompt = "Hi SaRa! 🌸 I'm studying '${topic.title}' ($difficulty level). Can you test me with a new problem or explain a tricky concept in more detail?"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("learning_chat_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        "Ask SaRa for More Practice Problems 💡",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
