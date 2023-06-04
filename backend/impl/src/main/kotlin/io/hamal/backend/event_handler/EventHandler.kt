package io.hamal.backend.event_handler

import io.hamal.backend.event.Event
import io.hamal.lib.domain.CmdId
import kotlin.reflect.KClass

interface EventHandler<out EVENT : Event> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

    interface Container {

        fun <NOTIFICATION : Event> register(
            clazz: KClass<NOTIFICATION>,
            receiver: EventHandler<NOTIFICATION>
        ): Boolean

        operator fun <NOTIFICATION : Event> get(clazz: KClass<NOTIFICATION>): List<EventHandler<NOTIFICATION>>

        fun topics(): Set<String>

    }
}


