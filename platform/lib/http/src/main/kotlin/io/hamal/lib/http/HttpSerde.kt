package io.hamal.lib.http

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import java.io.InputStream
import kotlin.reflect.KClass

interface HttpSerdeFactory {
    var errorDeserializer: HttpErrorDeserializer
    var contentDeserializer: HttpContentDeserializer
    var contentSerializer: HttpContentSerializer
}

object DefaultHttpSerdeFactory : HttpSerdeFactory {
    override var errorDeserializer: HttpErrorDeserializer = JsonErrorDeserializer
    override var contentDeserializer: HttpContentDeserializer = JsonHttpContentDeserializer
    override var contentSerializer: HttpContentSerializer = JsonHttpContentSerializer
}

private val json = Json(
    JsonFactoryBuilder()
        .register(HotJsonModule)
        .register(ValueObjectJsonModule)
)

interface HttpErrorDeserializer {
    fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR
}

object JsonErrorDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return json.deserialize(clazz, inputStream)
    }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
    fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE>
}

object JsonHttpContentDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return json.deserialize(clazz, inputStream)
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return json.deserialize(object : TypeToken<List<VALUE>>() {}, inputStream)
    }
}

interface HttpContentSerializer {
    fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String
}

object JsonHttpContentSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return json.serialize(value)
    }
}
