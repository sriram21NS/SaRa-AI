package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.components.MarkdownText
import com.example.ui.components.SakuraParticlesBackground
import com.example.ui.components.SaraAnimatedCharacter
import com.example.ui.components.SaraAnimationState
import com.example.ui.components.SaraSize
import com.example.ui.components.VisualSourcesCard
import com.example.ui.theme.SakuraPetalPink
import com.example.ui.theme.SakuraPrimaryLight
import com.example.viewmodel.SaraViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val listState = rememberLazyListState()

    val currentConversationId by viewModel.currentConversationId.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val knowledgeMode by viewModel.knowledgeMode.collectAsState()
    val attachedImage by viewModel.attachedImageBase64.collectAsState()
    val conversations by viewModel.filteredConversations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showAttachSheet by remember { mutableStateOf(false) }

    val knowledgeModes = listOf(
        "General Assistant", "Coding", "Education", "Science",
        "Mathematics", "Technology", "Writing", "Creativity", "Data", "Research"
    )

    // Auto-scroll on new messages
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🌸 Conversations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakuraPrimaryLight
                            )
                        )
                        IconButton(
                            onClick = {
                                viewModel.startNewConversation()
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.testTag("drawer_new_chat_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Chat", tint = SakuraPrimaryLight)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search chats...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(conversations, key = { it.id }) { conv ->
                            val isSelected = conv.id == currentConversationId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) SakuraPrimaryLight.copy(alpha = 0.15f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.selectConversation(conv.id)
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = conv.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = conv.knowledgeMode,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteConversation(conv.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SaraAnimatedCharacter(
                                state = characterState,
                                size = SaraSize.AVATAR,
                                isSpeaking = isSpeaking
                            )

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "SaRa 🌸",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    text = "$knowledgeMode Guide",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SakuraPrimaryLight,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(SaraViewModel.Screen.HOME) },
                            modifier = Modifier.testTag("chat_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to home")
                        }
                    },
                    actions = {
                        if (isSpeaking) {
                            IconButton(
                                onClick = { viewModel.stopSpeaking() },
                                modifier = Modifier.testTag("stop_speaking_button")
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop voice", tint = SakuraPrimaryLight)
                            }
                        }

                        IconButton(
                            onClick = { viewModel.startNewConversation() },
                            modifier = Modifier.testTag("chat_new_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Chat", tint = SakuraPrimaryLight)
                        }

                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("chat_history_button")
                        ) {
                            Icon(Icons.Default.History, contentDescription = "Conversation history")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            },
            bottomBar = {
                ChatInputBar(
                    inputText = inputText,
                    onInputTextChange = { inputText = it },
                    onSend = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    attachedImage = attachedImage,
                    onClearAttachedImage = { viewModel.clearAttachedImage() },
                    onOpenAttachMenu = { showAttachSheet = true },
                    isListening = isListening,
                    onToggleVoiceInput = {
                        if (isListening) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening { recognized ->
                                inputText = recognized
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SakuraParticlesBackground(modifier = Modifier.fillMaxSize(), particleCount = 12)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Knowledge Mode Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        knowledgeModes.forEach { mode ->
                            val isSelected = mode == knowledgeMode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setKnowledgeMode(mode) },
                                label = {
                                    Text(
                                        text = mode,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                                ),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }

                    // Message List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            if (msg.role == "user") {
                                UserMessageBubble(message = msg)
                            } else {
                                SaraMessageBubble(
                                    message = msg,
                                    onExplainCode = { codeSnippet ->
                                        viewModel.sendMessage("Hi SaRa! 🌸 Could you explain how this code works step-by-step?\n```\n$codeSnippet\n```")
                                    },
                                    onActionChipClick = { chipText ->
                                        viewModel.sendMessage(chipText)
                                    },
                                    onReadAloud = { viewModel.speakResponse(msg.content) },
                                    onRegenerate = { viewModel.regenerateLastResponse() },
                                    onFeedback = { isLiked -> viewModel.setMessageFeedback(msg.id, isLiked) }
                                )
                            }
                        }

                        // Thinking / Generating Indicator
                        if (isGenerating) {
                            item {
                                SaraGeneratingIndicator(onStop = { viewModel.stopGenerating() })
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }

    // Attachment Modal Sheet
    if (showAttachSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🖼️ Attach Visual Context",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Select an image or sample diagram for SaRa to analyze, explain, or solve:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SakuraPrimaryLight.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.attachSampleImage("diagram")
                                showAttachSheet = false
                                Toast.makeText(context, "Attached Architecture Diagram! 🌸", Toast.LENGTH_SHORT).show()
                            }
                            .padding(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📐", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Architecture", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Diagram Sample", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SakuraPrimaryLight.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.attachSampleImage("code")
                                showAttachSheet = false
                                Toast.makeText(context, "Attached Code Screenshot! 💻", Toast.LENGTH_SHORT).show()
                            }
                            .padding(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "💻", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Code Screenshot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Code Snippet Sample", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UserMessageBubble(message: MessageEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            color = SakuraPrimaryLight,
            shadowElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 290.dp)
                .testTag("user_message_bubble")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.imageUri != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🖼️ Attached Image", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(6.dp))
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SaraMessageBubble(
    message: MessageEntity,
    onExplainCode: (String) -> Unit,
    onActionChipClick: (String) -> Unit,
    onReadAloud: () -> Unit,
    onRegenerate: () -> Unit,
    onFeedback: (Boolean?) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(text = "🌸", fontSize = 13.sp)
                Text(
                    text = "SaRa",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakuraPrimaryLight
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("sara_message_bubble")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Formatted Markdown and Code blocks
                    MarkdownText(
                        content = message.content,
                        onExplainCode = onExplainCode
                    )

                    // Visual Source Card (if provided)
                    if (message.visualSourceTitle != null && message.visualSourceDesc != null) {
                        Spacer(Modifier.height(10.dp))
                        VisualSourcesCard(
                            title = message.visualSourceTitle,
                            description = message.visualSourceDesc
                        )
                    }

                    // Action toolbar: Copy, Read aloud, Regenerate, Like/Dislike
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.content))
                                    Toast.makeText(context, "Copied response! ✨", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(
                                onClick = onReadAloud,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Read aloud", modifier = Modifier.size(16.dp), tint = SakuraPrimaryLight)
                            }

                            IconButton(
                                onClick = onRegenerate,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Regenerate", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { onFeedback(if (message.isLiked == true) null else true) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.ThumbUp,
                                    contentDescription = "Helpful",
                                    modifier = Modifier.size(15.dp),
                                    tint = if (message.isLiked == true) SakuraPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }

                            IconButton(
                                onClick = { onFeedback(if (message.isLiked == false) null else false) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.ThumbDown,
                                    contentDescription = "Not helpful",
                                    modifier = Modifier.size(15.dp),
                                    tint = if (message.isLiked == false) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Suggested prompt actions below SaRa's bubble
            if (!message.suggestedActions.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    message.suggestedActions.split(",").forEach { action ->
                        val cleanAction = action.trim()
                        if (cleanAction.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clickable { onActionChipClick(cleanAction) }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "💡 $cleanAction",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SakuraPrimaryLight,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SaraGeneratingIndicator(onStop: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SakuraPetalPink),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = SakuraPrimaryLight
                )
                Text(
                    text = "SaRa is thinking & guiding... 🌸",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakuraPrimaryLight,
                        fontWeight = FontWeight.Medium
                    )
                )

                TextButton(
                    onClick = onStop,
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("Stop", color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSend: () -> Unit,
    attachedImage: String?,
    onClearAttachedImage: () -> Unit,
    onOpenAttachMenu: () -> Unit,
    isListening: Boolean,
    onToggleVoiceInput: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // Attached image preview pill
            if (attachedImage != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .background(SakuraPrimaryLight.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "🖼️", fontSize = 14.sp)
                        Text(
                            text = "Image attached for visual understanding",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakuraPrimaryLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    IconButton(onClick = onClearAttachedImage, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Remove attached image", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Attach button
                IconButton(
                    onClick = onOpenAttachMenu,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("attach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Attach image or diagram",
                        tint = SakuraPrimaryLight
                    )
                }

                // Text Input
                TextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    placeholder = {
                        Text(
                            text = if (isListening) "Listening to you... 🎤" else "Ask SaRa anything...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, SakuraPetalPink.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .testTag("chat_input_field"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() })
                )

                // Voice input button
                IconButton(
                    onClick = onToggleVoiceInput,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (isListening) SakuraPrimaryLight.copy(alpha = 0.2f) else Color.Transparent,
                            CircleShape
                        )
                        .testTag("voice_input_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice input",
                        tint = if (isListening) SakuraPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Send button
                IconButton(
                    onClick = onSend,
                    enabled = inputText.isNotBlank() || attachedImage != null,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (inputText.isNotBlank() || attachedImage != null) SakuraPrimaryLight else SakuraPrimaryLight.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
