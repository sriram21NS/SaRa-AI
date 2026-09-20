package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.SakuraPrimaryLight

@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    onExplainCode: ((String) -> Unit)? = null
) {
    val blocks = remember(content) { parseMarkdownBlocks(content) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    Text(
                        text = block.text,
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            2 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            else -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is MarkdownBlock.Code -> {
                    CodeBlockView(
                        language = block.language,
                        code = block.code,
                        onExplainCode = onExplainCode
                    )
                }
                is MarkdownBlock.Paragraph -> {
                    FormattedParagraph(text = block.text)
                }
                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier.padding(start = 6.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🌸",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        FormattedParagraph(text = block.text)
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeBlockView(
    language: String,
    code: String,
    onExplainCode: ((String) -> Unit)?
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showPreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBlockBackground)
            .border(1.dp, Color(0xFF3B4252), RoundedCornerShape(12.dp))
    ) {
        // Code header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E3440))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = language.ifEmpty { "code" }.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88C0D0),
                    fontSize = 11.sp
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Copy button
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "Code copied! ✨", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(28.dp).testTag("copy_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Color(0xFFECEFF4),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = "Copy",
                        color = Color(0xFFECEFF4),
                        fontSize = 11.sp
                    )
                }

                // Explain Code
                if (onExplainCode != null) {
                    TextButton(
                        onClick = { onExplainCode(code) },
                        modifier = Modifier.height(28.dp).testTag("explain_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Explain code",
                            tint = SakuraPrimaryLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = "Explain",
                            color = SakuraPrimaryLight,
                            fontSize = 11.sp
                        )
                    }
                }

                // Run/Preview
                TextButton(
                    onClick = { showPreview = !showPreview },
                    modifier = Modifier.height(28.dp).testTag("run_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Preview output",
                        tint = Color(0xFFA3BE8C),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = if (showPreview) "Hide Output" else "Run",
                        color = Color(0xFFA3BE8C),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Code content
        Text(
            text = code,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = Color(0xFFE5E9F0)
            )
        )

        // Simulated Run / Output Area
        if (showPreview) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B1E2B))
                    .padding(10.dp)
                    .border(1.dp, Color(0xFFA3BE8C).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "▶ Execution Output (Simulated):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFA3BE8C),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                val outputText = when {
                    code.contains("print") -> "Hello, Friend! 🌸 Welcome to programming.\n[Execution completed successfully with code 0]"
                    code.contains("html") -> "<DOM Rendered: Web Page Structure Ready>"
                    else -> "Code compiled & evaluated without errors! ✨"
                }
                Text(
                    text = outputText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFD8DEE9),
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun FormattedParagraph(text: String) {
    val annotated = remember(text) {
        buildAnnotatedString {
            var currentIndex = 0
            val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
            val matches = boldRegex.findAll(text)

            for (match in matches) {
                // Append text before bold
                if (match.range.first > currentIndex) {
                    append(text.substring(currentIndex, match.range.first))
                }
                // Append bold text
                val boldContent = match.groupValues[1]
                val start = length
                append(boldContent)
                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, length)

                currentIndex = match.range.last + 1
            }

            if (currentIndex < text.length) {
                append(text.substring(currentIndex))
            }
        }
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

private sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Code(val language: String, val code: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
}

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val result = mutableListOf<MarkdownBlock>()
    val lines = raw.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // Code blocks
        if (line.trim().startsWith("```")) {
            val language = line.trim().removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            result.add(MarkdownBlock.Code(language, codeLines.joinToString("\n")))
            i++
            continue
        }

        // Headings
        if (line.startsWith("### ")) {
            result.add(MarkdownBlock.Heading(3, line.removePrefix("### ")))
            i++
            continue
        } else if (line.startsWith("## ")) {
            result.add(MarkdownBlock.Heading(2, line.removePrefix("## ")))
            i++
            continue
        } else if (line.startsWith("# ")) {
            result.add(MarkdownBlock.Heading(1, line.removePrefix("# ")))
            i++
            continue
        }

        // Bullets
        if (line.trim().startsWith("• ") || line.trim().startsWith("- ") || line.trim().startsWith("* ")) {
            val cleanBullet = line.trim().substring(2).trim()
            result.add(MarkdownBlock.BulletItem(cleanBullet))
            i++
            continue
        }

        // Regular paragraph (group non-empty lines)
        if (line.isNotBlank()) {
            result.add(MarkdownBlock.Paragraph(line.trim()))
        }
        i++
    }

    return result
}
