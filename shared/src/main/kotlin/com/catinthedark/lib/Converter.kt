package com.catinthedark.lib

interface Parser {
    fun wrap(data: IMessage): String
    fun unwrap(json: String): IMessage
}

interface Register {
    fun <T: IMessage> add(clazz: Class<out T>): Register
    fun <T: IMessage> addAll(clazzList: List<Class<out T>>): Register
}
