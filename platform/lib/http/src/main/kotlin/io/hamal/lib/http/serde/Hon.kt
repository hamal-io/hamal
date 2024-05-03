package io.hamal.lib.http.serde

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueHon
import io.hamal.lib.http.HttpContentDeserializer
import io.hamal.lib.http.HttpContentSerializer
import io.hamal.lib.http.HttpErrorDeserializer
import java.io.InputStream
import kotlin.reflect.KClass


object HttpErrorHonDeserializer : HttpErrorDeserializer {
    override fun <ERROR : Any> deserialize(inputStream: InputStream, clazz: KClass<ERROR>): ERROR {
        return honSerde.read(clazz, inputStream)
    }
}

object HttpContentHonDeserializer : HttpContentDeserializer {
    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
        return honSerde.read(clazz, inputStream)
    }

    override fun <VALUE : Any> deserializeList(inputStream: InputStream, clazz: KClass<VALUE>): List<VALUE> {
        return honSerde.read<Array<VALUE>>(TypeToken.getArray(clazz.java).type, inputStream).toList()
    }
}

object HttpContentHonSerializer : HttpContentSerializer {
    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
        return honSerde.write(value)
    }
}

private val honSerde = Serde.hon()
    .register(SerdeModuleValueHon)