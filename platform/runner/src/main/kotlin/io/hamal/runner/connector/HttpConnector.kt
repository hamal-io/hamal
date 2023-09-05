package io.hamal.runner.connector

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.HubSdk

class HttpConnector(
    private val sdk: HubSdk
) : Connector {

    override fun poll(): List<UnitOfWork> {
        return sdk.exec.poll().work.map {
            UnitOfWork(
                id = it.id,
                groupId = it.groupId,
                inputs = it.inputs,
                state = it.state,
                code = it.code,
                correlation = it.correlation,
                events = it.events
            )
        }
    }

    override fun complete(execId: ExecId, state: State, events: List<EventToSubmit>) {
        sdk.exec.complete(execId, state, events)
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        sdk.exec.fail(execId, error)
    }
}