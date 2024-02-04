package org.geo.gitnetwork.retrofit

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.geo.gitnetwork.util.Constant
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    val source: RetrofitUserSource by lazy {
        val moshi = Moshi.Builder().build()
        val config = RetrofitConfig(
            retrofit = createRetrofit(moshi),
            moshi = moshi
        )
        RetrofitUserSource(config)
    }


    private fun createRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.URL)
            .client(createOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor {chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer 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")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                return@Interceptor chain.proceed(requestBuilder.build())
            })
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

}