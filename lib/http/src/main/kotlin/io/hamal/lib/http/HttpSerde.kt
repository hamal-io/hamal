package io.hamal.lib.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
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
    fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR
}

object DefaultErrorDeserializer : HttpErrorDeserializer {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return delegate.decodeFromStream(clazz.serializer(), inputStream)
    }

    private val delegate = Json { ignoreUnknownKeys = true }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
    fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE>
}

object KotlinJsonHttpContentDeserializer : HttpContentDeserializer {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return delegate.decodeFromStream(clazz.serializer(), inputStream)
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return delegate.decodeFromStream(ArraySerializer(clazz, clazz.serializer()), inputStream).toList()
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