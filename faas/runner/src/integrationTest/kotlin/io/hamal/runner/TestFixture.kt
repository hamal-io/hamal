package io.hamal.runner

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork

class TestConnector : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, state: State, events: List<Event>) {}
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

    override fun complete(execId: ExecId, state: State, events: List<Event>) {
        org.junit.jupiter.api.fail { "Test expected to fail" }
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        block(error)
    }
}

class StringCaptor(
    var result: String? = null
) : Function1In0Out<StringType>(FunctionInput1Schema(StringType::class)) {
    override fun invoke(ctx: FunctionContext, arg1: StringType) {
        result = arg1.value
    }
}