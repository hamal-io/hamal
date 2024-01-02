package io.hamal.lib.http

import java.io.InputStream
import kotlin.reflect.KClass

interface HttpSerdeFactory {
    var errorDeserializer: HttpErrorDeserializer
    var contentDeserializer: HttpContentDeserializer
    var contentSerializer: HttpContentSerializer
}

object DefaultHttpSerdeFactory : HttpSerdeFactory {
    override var errorDeserializer: HttpErrorDeserializer = DefaultErrorDeserializer
    override var contentDeserializer: HttpContentDeserializer = GsonHttpContentDeserializer
    override var contentSerializer: HttpContentSerializer = GsonHttpContentSerializer
}

interface HttpErrorDeserializer {
    fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR
}

object DefaultErrorDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
//        return jsonDelegate.decodeFromStream(clazz.serializer(), inputStream)
        TODO()
    }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
    fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE>
}

object GsonHttpContentDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
//        return jsonDelegate.decodeFromStream(clazz.serializer(), inputStream)
        TODO()
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
//        return jsonDelegate.decodeFromStream(ArraySerializer(clazz, clazz.serializer()), inputStream).toList()
        TODO()
    }
}

interface HttpContentSerializer {
    fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String
}

object GsonHttpContentSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
//        return jsonDelegate.encodeToString(clazz.serializer(), value)
        TODO()
    }
}
