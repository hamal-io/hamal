package io.hamal.runner.connector

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.HamalSdk

class HttpConnector(
    private val sdk: HamalSdk
) : Connector {

    override fun poll(): List<UnitOfWork> {
        return sdk.execService().poll().work.map {
            UnitOfWork(
                id = it.id,
                inputs = it.inputs,
                state = it.state,
                code = it.code,
                correlation = it.correlation
            )
        }
    }

    override fun complete(execId: ExecId, state: State, events: List<Event>) {
        sdk.execService().complete(execId, state, events)
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        sdk.execService().fail(execId, error)
    }
}