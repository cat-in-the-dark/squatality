package com.catinthedark.lib

import java.io.Serializable

interface Parser {
    fun wrap(data: IMessage): String
    fun unwrap(json: String): IMessage
}

interface Register {
    fun <T: IMessage> add(clazz: Class<out T>): Register
    fun <T: IMessage> addAll(clazzList: List<Class<out T>>): Register
}

interface IMessage : Serializable
