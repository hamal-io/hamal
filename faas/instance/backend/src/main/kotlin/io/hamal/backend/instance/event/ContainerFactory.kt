package io.hamal.backend.instance.event

import io.hamal.backend.instance.event.events.InstanceEvent
import kotlin.reflect.KClass

class InstanceEventContainerFactory {
    private val container = InstanceEventContainer()

    fun <EVENT : InstanceEvent> register(
        clazz: KClass<EVENT>,
        handler: InstanceEventHandler<EVENT>
    ): InstanceEventContainerFactory {
        container.register(clazz, handler)
        return this
    }

    fun create(): InstanceEventContainer {
        return container
    }
}