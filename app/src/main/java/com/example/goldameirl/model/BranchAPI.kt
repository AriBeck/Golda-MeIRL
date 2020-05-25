package com.example.goldameirl.model

import com.example.goldameirl.misc.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

private val geoJsonRetrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BranchAPIService {
    @GET("golda.json")
    fun getBranches(): Deferred<List<Branch>>
}

interface GeoJsonAPIService {
    @GET("{source}.json")
    fun getGeoJson(@Path("source") source: String): Call<String>
}



object BranchAPI {
    val retrofitService: BranchAPIService by lazy {
        retrofit.create(BranchAPIService::class.java)
    }

    val geoJsonRetrofitService: GeoJsonAPIService by lazy {
        geoJsonRetrofit.create(GeoJsonAPIService::class.java)
    }
}