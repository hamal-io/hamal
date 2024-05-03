package io.hamal.lib.http

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
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

class JsonHttpSerdeFactory(private val serde: Serde) : HttpSerdeFactory {
    override var errorDeserializer = object : HttpErrorDeserializer {
        override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>) = serde.read(clazz, inputStream)
    }
    override var contentDeserializer = object : HttpContentDeserializer {
        override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>) = serde.read(clazz, inputStream)
        override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>) =
             serde.read<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()

    }
    override var contentSerializer = object : HttpContentSerializer {
        override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>) = serde.write(value)
    }
}


private val serde = Serde(
    GsonFactoryBuilder()
        .register(SerdeModule)
        .register(SerdeModuleJsonValue)
        .register(SerdeModuleJsonValueVariable)
)

interface HttpErrorDeserializer {
    fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR
}

object JsonErrorDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return serde.read(clazz, inputStream)
    }
}

interface HttpContentDeserializer {
    fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE
    fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE>
}

object JsonHttpContentDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return serde.read(clazz, inputStream)
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return serde.read<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()
    }
}

interface HttpContentSerializer {
    fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String
}

object JsonHttpContentSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return serde.write(value)
    }
}
