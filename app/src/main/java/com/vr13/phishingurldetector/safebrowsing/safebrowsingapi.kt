package com.vr13.phishingurldetector.safebrowsing


import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class SafeBrowsingRequest(
    val client: ClientInfo = ClientInfo("phishguard-app", "1.0"),
    val threatInfo: ThreatInfo
)

data class ClientInfo(val clientId: String, val clientVersion: String)
data class ThreatInfo(
    val threatTypes: List<String>,
    val platformTypes: List<String>,
    val threatEntryTypes: List<String>,
    val threatEntries: List<ThreatEntry>
)

data class ThreatEntry(val url: String)
data class SafeBrowsingResponse(val matches: List<ThreatMatch>?)
data class ThreatMatch(val threatType: String)

interface SafeBrowsingApi {
    @POST("v4/threatMatches:find")
    suspend fun checkUrl(
        @Query("key") apiKey: String,
        @Body request: SafeBrowsingRequest
    ): SafeBrowsingResponse?

    companion object {
        fun create(): SafeBrowsingApi {
            return Retrofit.Builder()
                .baseUrl("https://safebrowsing.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SafeBrowsingApi::class.java)
        }
    }
}
