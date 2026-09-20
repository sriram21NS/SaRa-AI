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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun GuideModeScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.selectedGuideProject.collectAsState()
    val stepIndex by viewModel.currentGuideStepIndex.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    val currentStep = project.steps.getOrElse(stepIndex) { project.steps.first() }
    val totalSteps = project.steps.size
    val progress = (stepIndex + 1).toFloat() / totalSteps.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🧭", fontSize = 18.sp)
                        Text(
                            text = "SaRa Guide Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(SaraViewModel.Screen.HOME) },
                        modifier = Modifier.testTag("guide_back_button")
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
                // Project Track selector tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.guideProjects.forEach { proj ->
                        val isSelected = proj.title == project.title
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectGuideProject(proj) },
                            label = {
                                Text(
                                    text = "${proj.icon} ${proj.title}",
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

                Spacer(Modifier.height(14.dp))

                // Progress Indicator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                        .border(1.dp, SakuraPetalPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step ${stepIndex + 1} of $totalSteps",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakuraPrimaryLight
                            )
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SakuraPrimaryLight,
                        trackColor = SakuraPrimaryLight.copy(alpha = 0.2f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Character Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SaraAnimatedCharacter(
                        state = SaraAnimationState.GUIDING,
                        size = SaraSize.MEDIUM,
                        isSpeaking = isSpeaking,
                        dialogueBubbleText = "Let's complete Step ${stepIndex + 1}! 🌸"
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SakuraPrimaryLight.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "STEP ${currentStep.stepNumber}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakuraPrimaryLight
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Step Details Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentStep.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        if (currentStep.exampleCode != null) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Code Example:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.height(6.dp))
                            MarkdownText(
                                content = "```\n${currentStep.exampleCode}\n```",
                                onExplainCode = { code ->
                                    viewModel.startNewConversation(
                                        title = "Explaining Step ${currentStep.stepNumber}",
                                        initialPrompt = "Hi SaRa! 🌸 Please explain this code example from Step ${currentStep.stepNumber}:\n```\n$code\n```"
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // SaRa's Tip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SakuraPrimaryLight.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tip",
                                    tint = SakuraPrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = currentStep.tip,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Step Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousGuideStep() },
                        enabled = stepIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("guide_prev_step_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Previous")
                    }

                    Button(
                        onClick = { viewModel.nextGuideStep() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("guide_next_step_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SakuraPrimaryLight)
                    ) {
                        Text(if (stepIndex == totalSteps - 1) "Complete 🎉" else "Next Step")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Discuss with SaRa button
                Button(
                    onClick = {
                        viewModel.startNewConversation(
                            title = "Guide: ${project.title}",
                            initialPrompt = "Hi SaRa! 🌸 I'm working on '${project.title}', specifically Step ${currentStep.stepNumber}: ${currentStep.title}. Can you guide me through this with some extra tips and questions?"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("guide_discuss_chat_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(
                        Icons.Default.ChatBubble,
                        contentDescription = "Discuss in chat",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Ask SaRa About This Step in Chat",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
