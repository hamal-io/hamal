package io.hamal.backend.event.handler

import io.hamal.backend.event.Event
import kotlin.reflect.KClass

interface EventHandler<out EVENT : Event> {

    fun handle(notification: @UnsafeVariance EVENT)

    interface Container {

        fun <NOTIFICATION : Event> register(
            clazz: KClass<NOTIFICATION>,
            receiver: EventHandler<NOTIFICATION>
        ): Boolean

        operator fun <NOTIFICATION : Event> get(clazz: KClass<NOTIFICATION>): List<EventHandler<NOTIFICATION>>

        fun topics(): Set<String>

    }
}


