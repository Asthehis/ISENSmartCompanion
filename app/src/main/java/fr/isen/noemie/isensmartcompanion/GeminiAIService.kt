import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

class GeminiAIService(context: Context) {
    private val apiKey: String = loadApiKey(context)

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun getAIResponse(userInput: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = model.generateContent(userInput)
                response.text ?: "No response received."
            } catch (e: Exception) {
                "Error: ${e.localizedMessage}"
            }
        }
    }

    private fun loadApiKey(context: Context): String {
        val properties = Properties()
        try {
            val inputStream = context.assets.open("config.properties") // Utiliser un fichier dans `assets/`
            properties.load(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            return "API_KEY_NOT_FOUND"
        }
        return properties.getProperty("GOOGLE_API_KEY", "API_KEY_NOT_FOUND")
    }

}
