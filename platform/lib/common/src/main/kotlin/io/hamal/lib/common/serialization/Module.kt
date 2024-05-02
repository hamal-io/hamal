package io.hamal.lib.common.serialization

import kotlin.reflect.KClass

abstract class SerializationModule {

    protected operator fun <TYPE : Any, ADAPTER : JsonAdapter<TYPE>> set(clazz: KClass<TYPE>, adapter: ADAPTER): SerializationModule {
        adapters[clazz] = adapter
        return this
    }

    internal val adapters = mutableMapOf<KClass<*>, JsonAdapter<*>>()
}