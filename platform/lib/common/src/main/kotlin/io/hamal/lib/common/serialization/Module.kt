package io.hamal.lib.common.serialization

import kotlin.reflect.KClass

abstract class HotModule {

    protected operator fun <TYPE : Any, ADAPTER : JsonAdapter<TYPE>> set(
        clazz: KClass<TYPE>,
        adapter: ADAPTER
    ): HotModule {
        adapters[clazz] = adapter
        return this
    }

    internal val adapters = mutableMapOf<KClass<*>, JsonAdapter<*>>()
}