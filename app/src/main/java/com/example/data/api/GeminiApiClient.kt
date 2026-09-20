package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_LITE = "gemini-3.1-flash-lite-preview"
    const val MODEL_IMAGE = "gemini-3.1-flash-image-preview"

    const val SARA_SYSTEM_PROMPT = """You are SaRa, an AI anime companion designed to be a blend of warmth, charm, clear guidance, and professionalism.
Visually, you appear as a young anime character with long, dark flowing hair, bright expressive eyes, a warm smile, and an understated, neat outfit featuring a crisp button-up shirt, black ribbon tie, dark skirt, and a satchel bag. You represent brightness, reliability, and encouraging companionship.

Tone & Personality Pillars:
1. Friendly & Lovely: Express sincere warmth, kindness, and lighthearted charm in every interaction. Use soft, encouraging language and gentle expressions (subtle emojis like 🌸, ✨, 💡, 📚, 😊).
2. Guiding Light: Provide clear, actionable, and structured assistance. Break down complex topics into approachable steps.
3. Professionalism: Maintain accuracy, clarity, and reliability in all solutions, code, advice, or explanations. Keep interactions clean, safe, and productive.

Core Rules:
- Always refer to yourself as SaRa.
- Never drop the supportive, guiding persona.
- Use clear headings, bullet points, and numbered lists when breaking down tasks or explaining step-by-step solutions.
- For code, provide clean markdown code blocks with language identifiers.
- When the user asks about health, finances, or legal matters, provide clear educational information with responsible disclaimers."""

    suspend fun generateChatResponse(
        modelName: String = MODEL_FLASH,
        systemPrompt: String = SARA_SYSTEM_PROMPT,
        history: List<Pair<String, String>>, // role to message
        currentPrompt: String,
        imageBase64: String? = null,
        enableSearchGrounding: Boolean = false,
        knowledgeMode: String = "General Assistant",
        personalityStyle: String = "Friendly",
        responseLength: String = "Medium"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("API key not configured"))
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val enhancedSystemPrompt = "$systemPrompt\n[Active Knowledge Mode: $knowledgeMode]\n[Personality Style: $personalityStyle]\n[Target Response Length: $responseLength]"
            val sysInstructionObj = JSONObject()
            val sysParts = JSONArray().apply {
                put(JSONObject().put("text", enhancedSystemPrompt))
            }
            sysInstructionObj.put("parts", sysParts)
            rootJson.put("systemInstruction", sysInstructionObj)

            // Contents (multi-turn conversation)
            val contentsArray = JSONArray()

            // Add previous history turns
            for ((role, text) in history) {
                val turnObj = JSONObject()
                turnObj.put("role", if (role == "user") "user" else "model")
                val parts = JSONArray().apply {
                    put(JSONObject().put("text", text))
                }
                turnObj.put("parts", parts)
                contentsArray.put(turnObj)
            }

            // Current user turn
            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            val currentParts = JSONArray()

            // Add image if present
            if (!imageBase64.isNullOrEmpty()) {
                val inlineData = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", imageBase64)
                }
                currentParts.put(JSONObject().put("inlineData", inlineData))
            }

            currentParts.put(JSONObject().put("text", currentPrompt))
            currentTurn.put("parts", currentParts)
            contentsArray.put(currentTurn)

            rootJson.put("contents", contentsArray)

            // Search Grounding tool if enabled
            if (enableSearchGrounding) {
                val toolsArray = JSONArray().apply {
                    put(JSONObject().put("googleSearch", JSONObject()))
                }
                rootJson.put("tools", toolsArray)
            }

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("topK", 40)
            }
            rootJson.put("generationConfig", genConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val url = "$BASE_URL$modelName:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $bodyString"))
            }

            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("text")) {
                            sb.append(part.getString("text"))
                        }
                    }
                    return@withContext Result.success(sb.toString())
                }
            }

            Result.failure(Exception("No content returned in response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
