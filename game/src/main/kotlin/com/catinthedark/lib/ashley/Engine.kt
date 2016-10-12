package com.catinthedark.lib.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine

inline fun <reified T: Component> Engine.createComponent(): T {
    return this.createComponent(T::class.java)
}
