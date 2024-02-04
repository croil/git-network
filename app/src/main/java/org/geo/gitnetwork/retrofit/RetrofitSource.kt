package org.geo.gitnetwork.retrofit


import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import org.geo.gitnetwork.exception.BackendException
import org.geo.gitnetwork.exception.ConnectionException
import org.geo.gitnetwork.exception.ParseBackendException
import retrofit2.HttpException
import java.io.IOException

open class RetrofitSource(
    retrofitConfig: RetrofitConfig
) {
    val retrofit = retrofitConfig.retrofit
    private val errorAdapter = retrofitConfig.moshi.adapter(ErrorResponseBody::class.java)


    suspend fun <T> handleRetrofitException(block : suspend () -> T) : T {
        return try {
           block()
        } catch (e : JsonDataException) {
            throw ParseBackendException(e)
        } catch (e : JsonEncodingException ) {
            throw ParseBackendException(e)
        } catch (e : HttpException) {
            throw createBackendException(e)
        } catch (e : IOException) {
            throw ConnectionException(e)
        }
    }

    private fun createBackendException(e: HttpException): Exception {
        return try {
            val errorBody = errorAdapter.fromJson(
                e.response()?.errorBody()?.string()!!
            )!!
            BackendException(errorBody.error, e.code())
        } catch (e : Exception) {
            throw ParseBackendException(e)
        }

    }


    class ErrorResponseBody(
        val error : String
    )
}