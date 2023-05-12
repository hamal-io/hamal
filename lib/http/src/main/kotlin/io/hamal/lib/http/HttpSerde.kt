package io.hamal.lib.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.reflect.KClass

interface HttpSerdeFactory {
    var errorDeserializer: HttpErrorDeserializer
    var contentDeserializer: HttpContentDeserializer
    var contentSerializer: HttpContentSerializer
}

object DefaultHttpSerdeFactory : HttpSerdeFactory {
    override var errorDeserializer: HttpErrorDeserializer = DefaultErrorDeserializer
    override var contentDeserializer: HttpContentDeserializer = KotlinJsonHttpContentDeserializer
    override var contentSerializer: HttpContentSerializer = KotlinJsonHttpContentSerializer
}

interface HttpErrorDeserializer {
    fun deserialize(inputStream: InputStream): Throwable
}

object DefaultErrorDeserializer : HttpErrorDeserializer {
    override fun deserialize(inputStream: InputStream): HttpError {
        val bis = BufferedInputStream(inputStream)
        val out = ByteArrayOutputStream()
        var result = bis.read()
        while (result != -1) {
            out.write(result.toByte().toInt())
            result = bis.read()
        }
        return HttpError(out.toString("UTF-8"))
    }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
}

object KotlinJsonHttpContentDeserializer : HttpContentDeserializer {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return delegate.decodeFromStream(clazz.serializer(), inputStream)
    }

    private val delegate = Json { ignoreUnknownKeys = true }
}

interface HttpContentSerializer {
    fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String
}

object KotlinJsonHttpContentSerializer : HttpContentSerializer {
    @OptIn(InternalSerializationApi::class)
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return delegate.encodeToString(clazz.serializer(), value)
    }

    private val delegate = Json { ignoreUnknownKeys = true }
}