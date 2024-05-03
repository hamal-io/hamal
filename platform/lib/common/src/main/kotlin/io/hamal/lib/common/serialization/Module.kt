package io.hamal.lib.common.serialization

import kotlin.reflect.KClass

sealed interface SerdeModule

abstract class SerdeModuleJson : SerdeModule {

    protected operator fun <TYPE : Any, ADAPTER : JsonAdapter<TYPE>> set(clazz: KClass<TYPE>, adapter: ADAPTER): SerdeModuleJson {
        adapters[clazz] = adapter
        return this
    }

    internal val adapters = mutableMapOf<KClass<*>, JsonAdapter<*>>()
}