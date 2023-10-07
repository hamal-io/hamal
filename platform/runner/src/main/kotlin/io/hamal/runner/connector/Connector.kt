package io.hamal.runner.connector

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.ErrorType

data class UnitOfWork(
    val id: ExecId,
    val groupId: GroupId,
    val inputs: ExecInputs,
    val state: State,
    val code: CodeValue,
    val token: ExecToken = ExecToken("let_me_in"), // FIXME
    val correlation: Correlation? = null,
    val events: List<Event> = listOf()
)

// FIXME ConnectorState, ConnectorEvent, ConnectorError
interface Connector {
    fun poll(): List<UnitOfWork>

    fun complete(execId: ExecId, state: State, events: List<EventToSubmit>)

    fun fail(execId: ExecId, error: ErrorType)
}
