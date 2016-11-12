package com.catinthedark.squatality.server.spy

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Service to get GEO info by ip address of players.
 */
class GeoIPService(
    private val gson: Gson = Gson(),
    private val client: OkHttpClient = OkHttpClient()
) {
    private val host = "http://ip-api.com/json/"
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(4)

    fun find(ip: String): CompletableFuture<GeoModel> {
        val future: CompletableFuture<GeoModel> = CompletableFuture()
        val url = "$host$ip"
        val req = Request.Builder().url(url).build()
        doRequest(req, future)
        return future
    }

    private fun doRequest(req: Request, future: CompletableFuture<GeoModel>, tries: Int = 0) {
        if (tries > 10) {
            future.completeExceptionally(Exception("Exceeded max tries of 10"))
            return
        }

        executor.schedule({
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    doRequest(req, future, tries + 1)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val json = response.body().string()
                            val body = gson.fromJson(json, GeoModel::class.java)
                            future.complete(body)
                        } catch (e: Exception) {
                            future.completeExceptionally(e)
                        }
                    } else {
                        doRequest(req, future, tries + 1)
                    }
                }
            })
        }, timeFunc(tries), TimeUnit.SECONDS)
    }

    /**
     * Back off function, may be fibonacci or exp(2)
     */
    private fun timeFunc(n: Int): Long {
        return (Math.pow(2.0, n.toDouble()) - 1).toLong()
    }
}

data class GeoModel(
    val status: String? = "",
    val country: String? = "",
    val countryCode: String? = "",
    val region: String? = "",
    val regionName: String? = "",
    val city: String? = "",
    val zip: String? = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
    val timezone: String? = "",
    val isp: String? = "",
    val org: String? = "",
    val `as`: String? = "",
    val query: String? = ""
)
