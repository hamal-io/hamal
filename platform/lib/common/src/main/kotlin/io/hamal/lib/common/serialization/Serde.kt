package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.compress.Compressor
import io.hamal.lib.common.compress.CompressorNop
import io.hamal.lib.common.serialization.json.SerdeModuleJsonDefault
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import kotlin.reflect.KClass

sealed class Serde {
    companion object {
        fun json(compressor: Compressor = CompressorNop): SerdeJson = SerdeJson(compressor)
    }
}

class SerdeJson(
    private val compressor: Compressor = CompressorNop
) {

    fun <TYPE : Any> write(src: TYPE): String {
        return gson.toJson(src)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> writeAndCompress(src: TYPE): ByteArray {
        val json = write(src)
        return compressor.compress(json)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, content: String): TYPE {
        return gson.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gson.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gson.fromJson(compressor.toString(bytes), clazz.java)!!
    }

    fun <TYPE : Any> read(type: Type, stream: InputStream): TYPE {
        return gson.fromJson(InputStreamReader(stream), type)
    }


    fun <TYPE : Any> read(type: Type, content: String): TYPE {
        return gson.fromJson(content, type)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> decompressAndRead(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gson.fromJson(compressor.toString(bytes), clazz.java)!!
    }


    fun register(module: SerdeModuleJson): SerdeJson {
        module.adapters.forEach { (clazz, adapter) ->
            builder.registerTypeAdapter(clazz.java, adapter)
        }
        return this
    }

    fun <TYPE : Any, ADAPTER : AdapterJson<TYPE>> register(
        clazz: KClass<TYPE>,
        adapter: ADAPTER
    ): SerdeJson {
        builder.registerTypeAdapter(clazz.java, adapter)
        return this
    }

    private val builder = GsonBuilder()
    val gson: Gson by lazy { builder.create() }

    init {
        register(SerdeModuleJsonDefault)
    }
}

class SerdeHon(
    private val compressor: Compressor = CompressorNop
) {

    fun <TYPE : Any> write(src: TYPE): String {
        return gson.toJson(src)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> writeAndCompress(src: TYPE): ByteArray {
        val json = write(src)
        return compressor.compress(json)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, content: String): TYPE {
        return gson.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gson.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> read(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gson.fromJson(compressor.toString(bytes), clazz.java)!!
    }

    fun <TYPE : Any> read(type: Type, stream: InputStream): TYPE {
        return gson.fromJson(InputStreamReader(stream), type)
    }


    fun <TYPE : Any> read(type: Type, content: String): TYPE {
        return gson.fromJson(content, type)
    }

    @Deprecated("Remove this as we can pass now a compressor to the json object")
    fun <TYPE : Any> decompressAndRead(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gson.fromJson(compressor.toString(bytes), clazz.java)!!
    }


    fun register(module: SerdeModuleHon): SerdeHon {
        module.adapters.forEach { (clazz, adapter) ->
            builder.registerTypeAdapter(clazz.java, adapter)
        }
        return this
    }

    fun <TYPE : Any, ADAPTER : AdapterHon<TYPE>> register(
        clazz: KClass<TYPE>,
        adapter: ADAPTER
    ): SerdeHon {
        builder.registerTypeAdapter(clazz.java, adapter)
        return this
    }

    private val builder = GsonBuilder()
    val gson: Gson by lazy { builder.create() }

    init {
        register(SerdeModuleJsonDefault)
    }
}