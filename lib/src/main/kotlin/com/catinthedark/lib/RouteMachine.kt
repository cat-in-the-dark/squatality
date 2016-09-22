package com.catinthedark.lib

class RouteMachine {
    private val routes: MutableMap<YieldUnit<*, *>, (Any) -> YieldUnit<Any, *>> = hashMapOf()
    private lateinit var current: YieldUnit<*, *>

    fun <T> start(unit: YieldUnit<T, Any>, data: T) {
        current = unit
        unit.onActivate(data)
    }

    fun run(delta: Float) {
        val data = current.run(delta)
        if (data != null) {
            doRoute(data)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> addRoute(from: YieldUnit<*, T>, routeFn: (T) -> YieldUnit<T, *>) {
        routes.put(from as YieldUnit<*, *>, routeFn as (Any) -> YieldUnit<Any, *>)
    }

    private fun doRoute(data: Any) {
        val from = current
        println("Begin transition from $from")
        from.onExit()
        val routeFn = routes[from] ?: throw Exception("Could not find route function from $from")
        val to = routeFn(data)
        to.onActivate(data)
        println("End transition to $to")
        current = to
    }
}
