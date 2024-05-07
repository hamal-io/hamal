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

    operator fun <OBJ : Any> set(clazz: KClass<OBJ>, obj: OBJ?) {
        if (obj != null) {
            store[clazz] = obj
        }
    }

    override operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return checkNotNull(find(clazz)) as OBJ
    }

    @Suppress("UNCHECKED_CAST")
    fun <OBJ : Any> find(clazz: KClass<OBJ>): OBJ? = store[clazz] as OBJ?

    fun emit(evt: EventToSubmit) {
        eventsToSubmit.add(evt)
    }

    private val store = mutableMapOf<KClass<*>, Any>()

    val eventsToSubmit = mutableListOf<EventToSubmit>()

    init {
        this[ExecInputs::class] = inputs
    }
}

