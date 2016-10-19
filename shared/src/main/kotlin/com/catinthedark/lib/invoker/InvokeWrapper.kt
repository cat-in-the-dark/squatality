package com.catinthedark.lib.invoker

import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction4

/**
 * Helper class to tie target with invoke queue.
 */
class InvokeWrapper<out T>(
    private val target: T,
    private val invoker: Invoker
) {
    operator fun <R> invoke(func: KFunction1<T, R>) = invoker(target, func)

    operator fun <U, R> invoke(func: KFunction2<T, U, R>, arg: U) = invoker(target, func, arg)

    operator fun <U1, U2, R> invoke(func: KFunction3<T, U1, U2, R>, arg1: U1, arg2: U2) = invoker(target, func, arg1, arg2)

    operator fun <U1, U2, U3, R> invoke(func: KFunction4<T, U1, U2, U3, R>, arg1: U1, arg2: U2, arg3: U3) = invoker(target, func, arg1, arg2, arg3)
}
