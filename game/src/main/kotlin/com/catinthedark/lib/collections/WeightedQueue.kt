package com.catinthedark.lib.collections

class WeightedQueue<T>(
    private val overweight: Long = 100L
) {
    private val collection: MutableList<Weighted<T>> = arrayListOf()

    fun add(element: T, weight: Long) {
        collection.add(Weighted(
            weight = weight,
            actualWeight = 0L,
            payload = element))
    }

    fun pollOverweight(): List<Weighted<T>> {
        if (weight() > overweight) {
            return poll((weight() - overweight) * 3)
        }
        return emptyList()
    }

    fun pollWithOverweight(weight: Long): List<Weighted<T>> {
        return pollOverweight() + poll(weight)
    }

    fun poll(weight: Long): List<Weighted<T>> {
        var haveWeight = weight
        val elements = collection.takeWhile { el ->
            if (haveWeight > 0L) {
                val need = el.delta()
                val w = if (need < haveWeight) {
                    need
                } else {
                    haveWeight
                }
                haveWeight -= w
                el.actualWeight += w
                true
            } else {
                false
            }
        }

        collection.removeAll {
            it.percentage() >= 0.98
        }

        return elements
    }

    fun weight(): Long {
        if (collection.isEmpty()) return 0
        return collection.map { it.weight - it.actualWeight }.reduceRight { sum, el -> sum + el }
    }

    val size: Int
        get() = collection.size

    fun clear() {
        collection.clear()
    }

    data class Weighted<out T>(
        val weight: Long, // in ms
        var actualWeight: Long, // in ms
        val payload: T
    ) {
        fun delta() = weight - actualWeight
        fun percentage(): Float {
            if (weight == 0L) return 1f
            val p = actualWeight.toFloat() / weight.toFloat()
            return if (p >= 1) {
                1f
            } else if (p <=0) {
                0f
            } else {
                p
            }
        }
    }
}
