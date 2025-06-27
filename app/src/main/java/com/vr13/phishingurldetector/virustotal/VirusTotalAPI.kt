package com.vr13.phishingurldetector.virustotal

import retrofit2.Response
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class UrlScanResponse(
    val data: Data?
)

data class Data(
    val id: String?
)

data class UrlAnalysis(
    val data: UrlData
)

data class UrlData(
    val attributes: UrlAttributes
)

data class UrlAttributes(
    val last_analysis_stats: Map<String, Int>,
    val results: Map<String, AnalysisResult>,
    val status: String? = null
)

data class AnalysisResult(
    val category: String?,
    val result: String? = null
)

interface VirusTotalApi {

    @FormUrlEncoded
    @POST("urls")
    suspend fun submitUrl(
        @Header("x-apikey") apiKey: String,
        @Field("url") url: String
    ): Response<UrlScanResponse>

    @GET("analyses/{id}")
    suspend fun getUrlAnalysis(
        @Header("x-apikey") apiKey: String,
        @Path("id") id: String
    ): Response<UrlAnalysis>

    companion object {
        fun create(): VirusTotalApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.virustotal.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(VirusTotalApi::class.java)
        }
    }
}
