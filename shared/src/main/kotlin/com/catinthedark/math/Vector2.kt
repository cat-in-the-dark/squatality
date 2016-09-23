package com.catinthedark.math

data class Vector2(var x: Float = 0f, var y: Float = 0f) {
    fun angle(): Double {
        var angle: Double = Math.atan2(y.toDouble(), x.toDouble()) * MathUtils.radiansToDegrees
        if (angle < 0) angle += 360f
        return angle
    }

    fun dst(v: Vector2): Float {
        val x_d = v.x - x
        val y_d = v.y - y
        return Math.sqrt((x_d * x_d + y_d * y_d).toDouble()).toFloat()
    }

    fun setZero() {
        this.x = 0f
        this.y = 0f
    }

    fun add(v: Vector2): Vector2 {
        this.x += v.x
        this.y += v.y
        return this
    }

    fun add(x: Float, y: Float): Vector2 {
        this.x += x
        this.y += y
        return this
    }

    fun sub(x: Float, y: Float): Vector2 {
        this.x -= x
        this.y -= y
        return this
    }

    fun sub(v: Vector2): Vector2 {
        this.x -= v.x
        this.y -= v.y
        return this
    }

    fun setLength(len: Float): Vector2 {
        return setLength2(len * len)
    }

    fun setLength2(len2: Float): Vector2 {
        val oldLen2 = len2()
        return if (oldLen2 == 0f || oldLen2 == len2)
            this
        else
            scl(Math.sqrt((len2 / oldLen2).toDouble()).toFloat())
    }

    fun scl(scalar: Float): Vector2 {
        x *= scalar
        y *= scalar
        return this
    }

    fun len2() = x * x + y * y
}
