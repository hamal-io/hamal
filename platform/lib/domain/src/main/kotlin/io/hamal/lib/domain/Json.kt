package io.hamal.lib.domain

import io.hamal.lib.common.compress.Compressor
import io.hamal.lib.common.compress.NopCompressor
import io.hamal.lib.common.serialization.GsonFactoryBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import kotlin.reflect.KClass

class Json(
    factory: GsonFactoryBuilder,
    private val compressor: Compressor = NopCompressor
) {
    fun <TYPE : Any> serialize(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> serializeAndCompress(src: TYPE): ByteArray {
        val json = serialize(src)
        return compressor.compress(json)
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, content: String): TYPE {
        return gsonInstance.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(compressor.toString(bytes), clazz.java)!!
    }

//    fun <TYPE : Any> deserialize(typeToken: TypeToken<TYPE>, content: String): TYPE {
//        return gsonInstance.fromJson(content, typeToken)
//    }

    //    fun <TYPE : Any> deserialize(typeToken: TypeToken<TYPE>, stream: InputStream): TYPE {
//        return gsonInstance.fromJson(InputStreamReader(stream), typeToken)
//    }
//
    fun <TYPE : Any> deserialize(type: Type, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), type)
    }


    fun <TYPE : Any> deserialize(type: Type, content: String): TYPE {
        return gsonInstance.fromJson(content, type)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> decompressAndDeserialize(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(compressor.toString(bytes), clazz.java)!!
    }

    private val gsonInstance = factory.build()
}