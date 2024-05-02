package io.hamal.runner.run

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.SandboxContext
import kotlin.reflect.KClass


class RunnerContext(
    val state: State,
    val inputs: ExecInputs
) : SandboxContext {

    operator fun <OBJ : Any> set(clazz: KClass<OBJ>, obj: OBJ) {
        store[clazz] = obj
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return checkNotNull(store[clazz]) as OBJ
    }

    fun emit(evt: EventToSubmit) {
        eventsToSubmit.add(evt)
    }

    private val store = mutableMapOf<KClass<*>, Any>()

    val eventsToSubmit = mutableListOf<EventToSubmit>()

    init {
        this[ExecInputs::class] = inputs
    }
}

