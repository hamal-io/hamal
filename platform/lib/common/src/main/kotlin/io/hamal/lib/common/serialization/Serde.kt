package io.hamal.lib.common.serialization

import io.hamal.lib.common.compress.Compressor
import io.hamal.lib.common.compress.CompressorNop
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import kotlin.reflect.KClass

class Serde(
    factory: GsonFactoryBuilder,
    private val compressor: Compressor = CompressorNop
) {
    fun <TYPE : Any> write(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> writeAndCompress(src: TYPE): ByteArray {
        val json = write(src)
        return compressor.compress(json)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, content: String): TYPE {
        return gsonInstance.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(compressor.toString(bytes), clazz.java)!!
    }

    fun <TYPE : Any> read(type: Type, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), type)
    }


    fun <TYPE : Any> read(type: Type, content: String): TYPE {
        return gsonInstance.fromJson(content, type)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> decompressAndRead(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(compressor.toString(bytes), clazz.java)!!
    }

    private val gsonInstance = factory.build()
}