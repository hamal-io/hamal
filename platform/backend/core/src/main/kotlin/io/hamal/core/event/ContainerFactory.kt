package io.hamal.core.event

import io.hamal.repository.api.event.InternalEvent
import kotlin.reflect.KClass

class InternalEventContainerFactory {
    private val container = InternalEventContainer()

    fun <EVENT : InternalEvent> register(
        clazz: KClass<EVENT>,
        handler: InternalEventHandler<EVENT>
    ): InternalEventContainerFactory {
        container.register(clazz, handler)
        return this
    }

    fun create(): InternalEventContainer {
        return container
    }
}