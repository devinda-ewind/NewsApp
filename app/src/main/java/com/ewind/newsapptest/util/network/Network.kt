package com.ewind.newsapptest.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.ewind.newsapptest.util.ext.isOnline
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


const val TIME_OUT_DURATION = 40L
const val HEADER_CACHE_CONTROL = "Cache-Control"
const val HEADER_PRAGMA = "Pragma"

fun createNetworkClient(
    context: Context,
    baseUrl: String,
    debug: Boolean = false,
    supportInterceptor: SupportInterceptor
) = retrofitClient(baseUrl, httpClient(context, debug, supportInterceptor))

private fun myCache(context: Context): Cache {
    val cacheSize = (100.times(1024)
        .times(1024)
        .toLong())
    return Cache(File(context.cacheDir, "news"), cacheSize)
}

private fun httpClient(
    context: Context,
    debug: Boolean,
    supportInterceptor: SupportInterceptor
): OkHttpClient {
    val myCache = myCache(context)

    val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
    val clientBuilder = OkHttpClient.Builder()
        .apply {
            cache(myCache)
            connectTimeout(TIME_OUT_DURATION, TimeUnit.SECONDS)
            writeTimeout(TIME_OUT_DURATION, TimeUnit.SECONDS)
            readTimeout(TIME_OUT_DURATION, TimeUnit.SECONDS)

            if (debug) {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(httpLoggingInterceptor)
            }
            addInterceptor(supportInterceptor)
            addNetworkInterceptor(networkInterceptor())
            addInterceptor(offlineInterceptor(context))
        }

    //clientBuilder.authenticator(supportInterceptor)
    return clientBuilder.build()
}

private fun retrofitClient(
    baseUrl: String,
    httpClient: OkHttpClient
): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

private fun networkInterceptor(): Interceptor = Interceptor { chain ->
    val response = chain.proceed(chain.request())
    val cacheControl = CacheControl.Builder()
        .maxAge(5, TimeUnit.SECONDS)
        .build()
    response.newBuilder()
        .removeHeader(HEADER_PRAGMA)
        .removeHeader(HEADER_CACHE_CONTROL)
        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
        .build()
}

private fun offlineInterceptor(context: Context): Interceptor = Interceptor { chain ->
    var request = chain.request()

    context.isOnline {
        val cacheControl = CacheControl.Builder()
            .maxStale(7, TimeUnit.DAYS)
            .build()

        request = request.newBuilder()
            .removeHeader(HEADER_PRAGMA)
            .removeHeader(HEADER_CACHE_CONTROL)
            .cacheControl(cacheControl)
            .build()
    }

    chain.proceed(request)
}
