package io.hamal.lib.http

import com.google.gson.reflect.TypeToken
import io.hamal.lib.domain.Json
import java.io.InputStream
import kotlin.reflect.KClass

interface HttpSerdeFactory {
    var errorDeserializer: HttpErrorDeserializer
    var contentDeserializer: HttpContentDeserializer
    var contentSerializer: HttpContentSerializer
}

object DefaultHttpSerdeFactory : HttpSerdeFactory {
    override var errorDeserializer: HttpErrorDeserializer = GsonErrorDeserializer
    override var contentDeserializer: HttpContentDeserializer = GsonHttpContentDeserializer
    override var contentSerializer: HttpContentSerializer = GsonHttpContentSerializer
}

interface HttpErrorDeserializer {
    fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR
}

object GsonErrorDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return Json.deserialize(clazz, inputStream)
    }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
    fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE>
}

object GsonHttpContentDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return Json.deserialize(clazz, inputStream)
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return Json.deserialize(object : TypeToken<List<VALUE>>() {}, inputStream)
    }
}

interface HttpContentSerializer {
    fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String
}

object GsonHttpContentSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return Json.serialize(value)
    }
}
