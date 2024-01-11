package io.hamal.lib.domain

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

class Json(
    factory: JsonFactoryBuilder
) {
    fun <TYPE : Any> serialize(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    fun <TYPE : Any> serializeAndCompress(src: TYPE): ByteArray {
        val json = serialize(src)
        return json.toByteArray()
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, content: String): TYPE {
        return gsonInstance.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> deserialize(typeToken: TypeToken<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), typeToken)
    }

    fun <TYPE : Any> decompressAndDeserialize(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(String(bytes), clazz.java)!!
    }

    private val gsonInstance = factory.build()
}