package io.hamal.lib.http.serde

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.lib.http.HttpContentDeserializer
import io.hamal.lib.http.HttpContentSerializer
import io.hamal.lib.http.HttpErrorDeserializer
import java.io.InputStream
import kotlin.reflect.KClass


object HttpErrorJsonDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return jsonSerde.read(clazz, inputStream)
    }
}

object HttpContentJsonDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return jsonSerde.read(clazz, inputStream)
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return jsonSerde.read<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()
    }
}

object HttpContentJsonSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return jsonSerde.write(value)
    }
}

private val jsonSerde = Serde.json()
    .register(SerdeModuleValueJson)
    .register(SerdeModuleValueVariable)