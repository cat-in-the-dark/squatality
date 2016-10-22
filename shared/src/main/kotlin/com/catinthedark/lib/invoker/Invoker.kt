package com.catinthedark.lib.invoker

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction4

/**
 * Invoker that puts every method call into queue.
 * Yes, we have to make many functions, there is no way to escape it.
 */
class Invoker(
    val queue: InvokeQueue
) {
    operator fun <T, R> invoke(invokable: T, func: KFunction1<T, R>): Future<R?> {
        val f = CompletableFuture<R?>()

        queue.put({
            func.invoke(invokable)
        }, { data ->
            f.complete(data)
        })

        return f
    }

    operator fun <T, U, R> invoke(invokable: T, func: KFunction2<T, U, R>, arg: U): Future<R?> {
        val f = CompletableFuture<R?>()

        queue.put({
            func.invoke(invokable, arg)
        }, { data ->
            f.complete(data)
        })

        return f
    }

    operator fun <T, U1, U2, R> invoke(invokable: T, func: KFunction3<T, U1, U2, R>, arg1: U1, arg2: U2): Future<R?> {
        val f = CompletableFuture<R?>()

        queue.put({
            func.invoke(invokable, arg1, arg2)
        }, { data ->
            f.complete(data)
        })

        return f
    }

    operator fun <T, U1, U2, U3, R> invoke(invokable: T, func: KFunction4<T, U1, U2, U3, R>, arg1: U1, arg2: U2, arg3: U3): Future<R?> {
        val f = CompletableFuture<R?>()

        queue.put({
            func.invoke(invokable, arg1, arg2, arg3)
        }, { data ->
            f.complete(data)
        })

        return f
    }
}
