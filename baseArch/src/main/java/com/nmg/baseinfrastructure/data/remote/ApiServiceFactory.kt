package com.nmg.baseinfrastructure.data.remote

import com.nmg.baseinfrastructure.App
import com.nmg.baseinfrastructure.BuildConfig
import com.nmg.baseinfrastructure.utils.AdLog
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("NAME_SHADOWING")
abstract class ApiServiceFactory {

    companion object {

        private var urlAndHeaderInterceptor: Interceptor? = null
        private var headerHttpLoggingInterceptor: HttpLoggingInterceptor? = null
        private var bodyHttpLoggingInterceptor: HttpLoggingInterceptor? = null
        private var offlineCacheInterceptor: Interceptor? = null
        private var cacheInterceptor: Interceptor? = null

        var cacheControl: CacheControl? = null

        private const val TAG = "[ADW] [RTRFT]"

        private const val VERSION = "version"
        private const val APP_NAME = "appname"
        private const val PRAGMA = "Pragma"
        private const val CACHE_CONTROL = "Cache-Control"
        private const val HTTP_CACHE = "http-cache"
        var errorHandler: ErrorHandler? = null


        fun <T> getService(apiConfig: APIConfig): T {

            errorHandler = apiConfig.getErrorHandler()

            return synchronized(this) {
                val instance = Retrofit.Builder()
                        .baseUrl(apiConfig.getHost())
                        .client(provideOkHttpClient(apiConfig))
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(LiveDataCallAdapterFactory())
                        .build()
                        .create(apiConfig.getApiService<T>())
                instance
            }
        }

        private fun provideOkHttpClient(apiConfig: APIConfig? = null): OkHttpClient {
            val connectionSpaces = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2).build()
            val client = OkHttpClient.Builder()
            client.addInterceptor(provideHeaderHttpLoggingInterceptor())
            client.addInterceptor(provideBodyHttpLoggingInterceptor())
            client.addInterceptor(provideUrlAndHeaderInterceptor(apiConfig))
            client.addInterceptor(provideOfflineCacheInterceptor(apiConfig))
            client.addNetworkInterceptor(provideCacheInterceptor(apiConfig))
            client.cache(provideCache())
            client.connectionSpecs(Arrays.asList(connectionSpaces))
            client.connectTimeout(15, TimeUnit.SECONDS)
            client.readTimeout(15, TimeUnit.SECONDS)
            client.writeTimeout(15, TimeUnit.SECONDS)
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(interceptor)//Add Loggong Intercepter

            return client.build()
        }

        private fun provideHeaderHttpLoggingInterceptor(): HttpLoggingInterceptor {

            if (headerHttpLoggingInterceptor == null) {
                headerHttpLoggingInterceptor = HttpLoggingInterceptor(
                        HttpLoggingInterceptor.Logger { message ->
                            AdLog.logDebug(
                                    "$TAG PR HDR\n",
                                    message
                            )
                        })
                headerHttpLoggingInterceptor!!.level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            }
            return headerHttpLoggingInterceptor!!
        }

        private fun provideBodyHttpLoggingInterceptor(): HttpLoggingInterceptor {
            if (bodyHttpLoggingInterceptor == null) {
                bodyHttpLoggingInterceptor = HttpLoggingInterceptor(
                        HttpLoggingInterceptor.Logger { message ->
                            AdLog.logDebug(
                                    "$TAG PR BDY\n",
                                    message
                            )
                        })
                bodyHttpLoggingInterceptor!!.level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

            return bodyHttpLoggingInterceptor!!
        }

        private fun provideUrlAndHeaderInterceptor(apiConfig: APIConfig? = null): Interceptor {
            if (urlAndHeaderInterceptor == null) {
                urlAndHeaderInterceptor = Interceptor { chain ->
                    val request = chain.request()
                    val headers = apiConfig?.getHeaders()

                    val builder = request.newBuilder()
                    headers?.forEach {
                        builder.addHeader(it.key, it.value)

                    }

                    val url = request.url()
                            .newBuilder()
                            .addQueryParameter(VERSION, apiConfig?.getAppVersion())
                            .addQueryParameter(APP_NAME, apiConfig?.getAppName())
                            .build()

                    builder.url(url)

                    chain.proceed(builder.build())
                }
            }

            return urlAndHeaderInterceptor!!
        }

        private fun provideOfflineCacheInterceptor(apiConfig: APIConfig? = null): Interceptor {

            offlineCacheInterceptor = Interceptor { chain ->
                var request = chain.request()
                cacheControl = if (apiConfig!!.isInternetConnected()) {
                    CacheControl.Builder()
                            .maxAge(0, TimeUnit.SECONDS) // Stale for 7 days, with the expired cache
                            .build()

                } else {
                    // If no network
                    CacheControl.Builder().onlyIfCached()
                            .maxStale(7, TimeUnit.DAYS) // Stale for 7 days, with the expired cache
                            .build()
                }

                val requestBuilder = request.newBuilder()
                if (apiConfig!!.isInternetConnected()) {
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
                } else {
                    requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
                }
                request = requestBuilder.build()

                val response: Response = chain.proceed(request)
                response.newBuilder()
                        .removeHeader(PRAGMA)
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build()
            }


            return offlineCacheInterceptor!!
        }

        private fun provideCacheInterceptor(apiConfig: APIConfig? = null): Interceptor {

            cacheInterceptor = Interceptor { chain ->
                var request = chain.request()

                cacheControl = if (apiConfig!!.isInternetConnected()) {
                    CacheControl.Builder()
                            .maxAge(0, TimeUnit.SECONDS) // Stale for 7 days, with the expired cache
                            .build()

                } else {
                    // If no network
                    CacheControl.Builder().onlyIfCached()
                            .maxStale(7, TimeUnit.DAYS) // Stale for 7 days, with the expired cache
                            .build()
                }

                val requestBuilder = request.newBuilder()
                if (apiConfig!!.isInternetConnected()) {
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
                } else {
                    requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
                }
                requestBuilder.build()

                val response = chain.proceed(request)
                response.newBuilder()
                        .removeHeader(PRAGMA)
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build()
            }


            return cacheInterceptor!!
        }

        private var cache: Cache? = null
        private fun provideCache(): Cache? {
            if (cache == null) {
                try {
                    cache = Cache(
                            File(App.appInstance.cacheDir, HTTP_CACHE),
                            1024 * 1024 * 100
                    )
                } catch (e: Exception) {
                    AdLog.logError("$TAG PR CCH\n", "couldn't create cache", e)
                }
            }

            return cache
        }
    }
}