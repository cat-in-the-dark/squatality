package com.catinthedark.lib

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class JsonConverter(): Register, Parser {
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val clazzRegister: MutableMap<String, (JsonNode) -> IMessage?> = hashMapOf()

    override fun wrap(data: IMessage): String {
        val wrapper = Wrapper(data, data.javaClass.canonicalName)
        return mapper.writeValueAsString(wrapper)
    }

    override fun unwrap(json: String): IMessage {
        val wrapper = mapper.readValue(json, RootWrapper::class.java)
        val converter = clazzRegister[wrapper.className] ?: throw Exception("Converter for ${wrapper.className} not found")
        return converter(wrapper.data) ?: throw Exception("Can't parse ${wrapper.data} to ${wrapper.className}")
    }

    override fun <T: IMessage> add(clazz: Class<out T>): Register {
        clazzRegister.put(clazz.canonicalName, {
            mapper.convertValue(it, clazz)
        })
        return this
    }

    override fun <T: IMessage> addAll(clazzList: List<Class<out T>>): Register {
        clazzList.forEach { clazz -> add(clazz) }
        return this
    }

    private data class RootWrapper(val data: JsonNode, val className: String, val sender: String? = null)
    private data class Wrapper(val data: IMessage, val className: String)
}
