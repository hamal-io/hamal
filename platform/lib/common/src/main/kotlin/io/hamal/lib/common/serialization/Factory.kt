package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.serialization.json.SerdeModule
import kotlin.reflect.KClass

class GsonFactoryBuilder {

    private val builder = GsonBuilder()

    init {
        register(SerdeModule)
    }

    fun register(module: SerializationModule): GsonFactoryBuilder {
        module.adapters.forEach { (clazz, adapter) ->
            builder.registerTypeAdapter(clazz.java, adapter)
        }
        return this
    }

    fun <TYPE : Any, ADAPTER : JsonAdapter<TYPE>> register(
        clazz: KClass<TYPE>,
        adapter: ADAPTER
    ): GsonFactoryBuilder {
        builder.registerTypeAdapter(clazz.java, adapter)
        return this
    }

    fun build(): Gson = builder.create()
}