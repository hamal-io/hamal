package io.hamal.lib.http

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.domain.vo.ValueVariableJsonModule
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

class JsonHttpSerdeFactory(private val json: Json) : HttpSerdeFactory {
    override var errorDeserializer = object : HttpErrorDeserializer {
        override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>) = json.deserialize(clazz, inputStream)
    }
    override var contentDeserializer = object : HttpContentDeserializer {
        override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>) = json.deserialize(clazz, inputStream)
        override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>) =
             json.deserialize<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()

    }
    override var contentSerializer = object : HttpContentSerializer {
        override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>) = json.serialize(value)
    }
}


private val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueObjectJsonModule)
        .register(ValueVariableJsonModule)
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
        return json.deserialize<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()
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
