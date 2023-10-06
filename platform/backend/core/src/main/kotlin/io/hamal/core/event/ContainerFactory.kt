package io.hamal.core.event

import io.hamal.repository.api.event.PlatformEvent
import kotlin.reflect.KClass

class PlatformEventContainerFactory {
    private val container = PlatformEventContainer()

    fun <EVENT : PlatformEvent> register(
        clazz: KClass<EVENT>,
        handler: PlatformEventHandler<EVENT>
    ): PlatformEventContainerFactory {
        container.register(clazz, handler)
        return this
    }

    fun create(): PlatformEventContainer {
        return container
    }
}