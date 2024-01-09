package io.hamal.lib.domain

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.kua.type.KuaJsonModule
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass


val gsonInstance = JsonFactoryBuilder
    .register(HotJsonModule)
    .register(KuaJsonModule)
    .register(ValueObjectJsonModule)
    .build()


object Json {

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
        return gsonInstance.fromJson(String(bytes), clazz.java)
    }

}