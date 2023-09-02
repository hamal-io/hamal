package io.hamal.backend.event

import io.hamal.backend.event.event.HubEvent
import kotlin.reflect.KClass

class HubEventContainerFactory {
    private val container = HubEventContainer()

    fun <EVENT : HubEvent> register(
        clazz: KClass<EVENT>,
        handler: HubEventHandler<EVENT>
    ): HubEventContainerFactory {
        container.register(clazz, handler)
        return this
    }

    fun create(): HubEventContainer {
        return container
    }
}