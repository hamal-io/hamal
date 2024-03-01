package io.hamal.lib.kua

import kotlin.reflect.KClass

interface SandboxContext {
    operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ
}

object SandboxContextNop : SandboxContext {
    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        TODO("Not yet implemented")
    }
}