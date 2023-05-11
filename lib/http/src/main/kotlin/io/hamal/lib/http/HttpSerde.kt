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
    val errorDeserializer: HttpErrorDeserializer
    val contentDeserializer: HttpContentDeserializer
}

object DefaultHttpSerdeFactory : HttpSerdeFactory {
    override val errorDeserializer: HttpErrorDeserializer get() = DefaultErrorDeserializer
    override val contentDeserializer: HttpContentDeserializer get() = KotlinJsonHttpContentDeserializer

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