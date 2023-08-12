package io.hamal.backend.instance.event.handler

import io.hamal.backend.instance.event.SystemEvent
import io.hamal.lib.common.domain.CmdId
import kotlin.reflect.KClass

interface SystemEventHandler<out EVENT : SystemEvent> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

    interface Container {

        fun <NOTIFICATION : SystemEvent> register(
            clazz: KClass<NOTIFICATION>,
            receiver: SystemEventHandler<NOTIFICATION>
        ): Boolean

        operator fun <NOTIFICATION : SystemEvent> get(clazz: KClass<NOTIFICATION>): List<SystemEventHandler<NOTIFICATION>>

        fun topics(): Set<String>

    }
}

