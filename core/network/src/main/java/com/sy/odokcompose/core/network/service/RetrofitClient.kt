package com.sy.odokcompose.core.network.service

import androidx.core.os.trace
import com.squareup.moshi.Moshi
import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor(
    private val moshi: Moshi,
    private val okhttpCallFactory: dagger.Lazy<Call.Factory>
) {
    fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
        return trace("RetrofitClient_createService") {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory {
                    okhttpCallFactory.get().newCall(it)
                }
                .addConverterFactory(
                    MoshiConverterFactory.create(moshi)
                )
                .build()
                .create(serviceClass)
        }
    }
} 