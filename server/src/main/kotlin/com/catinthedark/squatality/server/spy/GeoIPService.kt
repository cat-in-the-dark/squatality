package com.catinthedark.squatality.server.spy

import com.catinthedark.squatality.server.spy.entities.GeoEntity
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
    private val client: OkHttpClient = OkHttpClient(),
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(4)
) {
    private val host = "http://ip-api.com/json/"

    fun find(ip: String): CompletableFuture<GeoEntity> {
        val future: CompletableFuture<GeoEntity> = CompletableFuture()
        val url = "$host$ip"
        val req = Request.Builder().url(url).build()
        doRequest(req, future)
        return future
    }

    private fun doRequest(req: Request, future: CompletableFuture<GeoEntity>, tries: Int = 0) {
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
                            val body = gson.fromJson(json, GeoEntity::class.java)
                            future.complete(body)
                        } catch (e: Exception) {
                            doRequest(req, future, tries + 1)
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
