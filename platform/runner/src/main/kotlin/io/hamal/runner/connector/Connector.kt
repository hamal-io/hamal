package io.hamal.runner.connector

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*

data class UnitOfWork(
    val id: ExecId,
    val namespaceId: NamespaceId,
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

    fun complete(execId: ExecId, result: ExecResult, state: State, events: List<EventToSubmit>)

    fun fail(execId: ExecId, result: ExecResult)
}
