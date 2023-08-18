package io.hamal.runner.execute

import io.hamal.lib.domain.Event
import io.hamal.lib.kua.SandboxContext
import kotlin.reflect.KClass

class ExecutionContext : SandboxContext {

    operator fun <OBJ : Any> set(clazz: KClass<OBJ>, obj: OBJ) {
        store[clazz] = obj
    }

    override operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return store[clazz] as OBJ
    }

    fun emit(evt: Event) {
        eventsToEmit.add(evt)
    }

    private val store = mutableMapOf<KClass<*>, Any>()
    val eventsToEmit = mutableListOf<Event>()
}