package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.serialization.serde.HotObjectModule
import kotlin.reflect.KClass

class JsonFactoryBuilder {

    private val builder = GsonBuilder()

    init {
        register(HotObjectModule)
    }

    fun register(module: HotModule): JsonFactoryBuilder {
        module.adapters.forEach { (clazz, adapter) ->
            builder.registerTypeAdapter(clazz.java, adapter)
        }
        return this
    }

    fun <TYPE : Any, ADAPTER : JsonAdapter<TYPE>> register(
        clazz: KClass<TYPE>,
        adapter: ADAPTER
    ): JsonFactoryBuilder {
        builder.registerTypeAdapter(clazz.java, adapter)
        return this
    }

    fun build(): Gson = builder.create()
}