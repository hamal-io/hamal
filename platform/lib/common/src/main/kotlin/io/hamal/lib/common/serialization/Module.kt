package io.hamal.lib.common.serialization

import kotlin.reflect.KClass

sealed interface SerdeModule

abstract class SerdeModuleJson : SerdeModule {

    protected operator fun <TYPE : Any, ADAPTER : AdapterJson<TYPE>> set(clazz: KClass<TYPE>, adapter: ADAPTER): SerdeModuleJson {
        adapters[clazz] = adapter
        return this
    }

    internal val adapters = mutableMapOf<KClass<*>, AdapterJson<*>>()
}

abstract class SerdeModuleHon : SerdeModule {

    protected operator fun <TYPE : Any, ADAPTER : AdapterHon<TYPE>> set(clazz: KClass<TYPE>, adapter: ADAPTER): SerdeModuleHon {
        adapters[clazz] = adapter
        return this
    }

    internal val adapters = mutableMapOf<KClass<*>, AdapterHon<*>>()
}