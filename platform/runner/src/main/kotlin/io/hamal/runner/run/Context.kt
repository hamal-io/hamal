package io.hamal.runner.run

import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.kua.SandboxContext
import kotlin.reflect.KClass

data class RunnerInvocationEvents(val events: List<Event>)

class RunnerContext(
    val state: State,
    invocationEvents: RunnerInvocationEvents
) : SandboxContext {


    operator fun <OBJ : Any> set(clazz: KClass<OBJ>, obj: OBJ) {
        store[clazz] = obj
    }

    override operator fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return store[clazz] as OBJ
    }

    fun emit(evt: EventToSubmit) {
        eventsToSubmit.add(evt)
    }

    private val store = mutableMapOf<KClass<*>, Any>()

    val eventsToSubmit = mutableListOf<EventToSubmit>()


    init {
        this[RunnerInvocationEvents::class] = invocationEvents
    }

}

