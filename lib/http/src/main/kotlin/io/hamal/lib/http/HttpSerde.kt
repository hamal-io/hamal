package io.hamal.lib.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.InputStream
import kotlin.reflect.KClass

interface HttpSerde {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
}

object KotlinJsonSerde : HttpSerde {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return delegate.decodeFromStream(clazz.serializer(), inputStream)
    }

    private val delegate = Json { ignoreUnknownKeys = true }
}