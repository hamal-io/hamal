package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.hot.HotJsonModule
import kotlin.reflect.KClass

object JsonFactoryBuilder {

    private val builder = GsonBuilder()

    init {
        register(HotJsonModule)
    }

    fun register(module: JsonModule): JsonFactoryBuilder {
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