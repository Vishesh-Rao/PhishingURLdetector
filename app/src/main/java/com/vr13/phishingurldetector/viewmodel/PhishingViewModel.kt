package com.vr13.phishingurldetector.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vr13.phishingurldetector.BuildConfig
import com.vr13.phishingurldetector.safebrowsing.*
import com.vr13.phishingurldetector.virustotal.VirusTotalApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PhishingViewModel : ViewModel() {

    private val vtApi = VirusTotalApi.create()
    private val sbApi = SafeBrowsingApi.create()

    var result by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // 🟦 expose engine-level results to HomeScreen
    var engineResults by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    fun checkUrl(url: String) {
        viewModelScope.launch {
            isLoading = true
            engineResults = emptyMap()
            result = "🔍 Checking with Google Safe Browsing..."
            Log.d("PhishingVM", "Calling Google Safe Browsing API...")

            try {
                // 1️⃣ Google Safe Browsing
                val sbRequest = SafeBrowsingRequest(
                    threatInfo = ThreatInfo(
                        threatTypes = listOf(
                            "MALWARE", "SOCIAL_ENGINEERING",
                            "POTENTIALLY_HARMFUL_APPLICATION", "UNWANTED_SOFTWARE"
                        ),
                        platformTypes = listOf("ANY_PLATFORM"),
                        threatEntryTypes = listOf("URL"),
                        threatEntries = listOf(ThreatEntry(url))
                    )
                )

                val sbResponse = sbApi.checkUrl(BuildConfig.SB_API_KEY, sbRequest)

                if (!sbResponse?.matches.isNullOrEmpty()) {
                    result = "⚠️ Safe Browsing flagged: ${
                        sbResponse.matches.joinToString { it.threatType }
                    }"
                    isLoading = false
                    return@launch
                }

                result = "✅ Safe Browsing found no issues. Submitting to VirusTotal..."
                Log.d("PhishingVM", "Calling VirusTotal API...")

                // 2️⃣ VirusTotal submit
                val vtResponse = vtApi.submitUrl(BuildConfig.VT_API_KEY, url)

                val analysisId = vtResponse.body()?.data?.id
                if (analysisId.isNullOrEmpty()) {
                    result = "❌ VirusTotal submission failed. Try again later."
                    Log.e("PhishingVM", "VirusTotal submission returned null ID or empty body.")
                    isLoading = false
                    return@launch
                }

                result = "🕒 Scanning with VirusTotal (up to 2 minutes)..."

                // 3️⃣ Poll for results
                repeat(30) {
                    delay(4000)
                    val analysis = vtApi.getUrlAnalysis(BuildConfig.VT_API_KEY, analysisId)
                    val stats = analysis.body()?.data?.attributes?.last_analysis_stats

                    // 🟦 update engine-level results live
                    val results = analysis.body()?.data?.attributes?.results
                    if (results != null) {
                        engineResults = results.mapValues { it.value.category ?: "unknown" }
                    }

                    if (stats != null) {
                        val malicious = stats["malicious"] ?: 0
                        val suspicious = stats["suspicious"] ?: 0

                        if (malicious > 0 || suspicious > 0) {
                            result = "⚠️ VirusTotal detected malicious/suspicious activity."
                            isLoading = false
                            return@launch
                        }
                    }
                }

                result = "✅ Double checked! No threats detected."

            } catch (e: HttpException) {
                result = "HTTP Error: ${e.code()} - ${e.message()}"
                Log.e("PhishingVM", "HTTP exception during checkUrl: ${e.message()}", e)
            } catch (e: Exception) {
                result = "Error: ${e.message}"
                Log.e("PhishingVM", "Error during checkUrl: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }
}
