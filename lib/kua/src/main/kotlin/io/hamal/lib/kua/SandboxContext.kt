package io.hamal.lib.kua

import kotlin.reflect.KClass

interface SandboxContext {
    operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ
}

class DefaultSandboxContext : SandboxContext {
    operator fun <OBJ : Any> set(clazz: KClass<OBJ>, obj: OBJ) {
        store[clazz] = obj
    }

    override operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return store[clazz] as OBJ
    }

    private val store = mutableMapOf<KClass<*>, Any>()
}