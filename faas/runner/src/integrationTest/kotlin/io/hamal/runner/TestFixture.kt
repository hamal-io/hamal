package io.hamal.runner

import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.type.ErrorType
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork

class TestConnector : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, state: State, events: List<EventPayload>) {}
    override fun fail(execId: ExecId, error: ErrorType) {
        org.junit.jupiter.api.fail { error.message }
    }
}

class TestFailConnector(
    val block: (ErrorType) -> Unit = {}
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, state: State, events: List<EventPayload>) {
        org.junit.jupiter.api.fail { "Test expected to fail" }
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        block(error)
    }
}
