package com.catinthedark.lib.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity

inline fun <reified T: Component> Engine.createComponent(): T {
    return this.createComponent(T::class.java)
}

inline fun <reified T: Component> Entity.getComponent(): T {
    return this.getComponent(T::class.java)
}
