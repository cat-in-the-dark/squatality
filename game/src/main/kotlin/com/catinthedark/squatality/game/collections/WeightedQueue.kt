package com.catinthedark.squatality.game.collections

class WeightedQueue<T> {
    private val collection: MutableList<Weighted<T>> = arrayListOf()

    fun add(element: T, weight: Long) {
        collection.add(Weighted(
            weight = weight,
            actualWeight = 0L,
            payload = element))
    }

    fun poll(weight: Long): Weighted<T>? {
        var haveWeight = weight
        val elements = collection.takeWhile { el ->
            val need = el.needSync()
            val w = if (need < haveWeight) {
                need
            } else {
                haveWeight
            }
            haveWeight -= w
            el.actualWeight += w

            haveWeight > 0L
        }

        elements.forEach {
            if (it.actualWeight >= it.weight) collection.remove(it)
        }

        return elements.lastOrNull()
    }

    fun weight(): Long {
        if (collection.isEmpty()) return 0
        return collection.map { it.weight }.reduceRight { sum, el -> sum + el }
    }

    fun clear() {
        collection.clear()
    }

    data class Weighted<out T>(
        var weight: Long, // in ms
        var actualWeight: Long, // in ms
        val payload: T
    ) {
        fun needSync() = weight - actualWeight
        fun percentage(): Float {
            if (weight == 0L) return 1f
            return actualWeight.toFloat() / weight.toFloat()
        }
    }
}
