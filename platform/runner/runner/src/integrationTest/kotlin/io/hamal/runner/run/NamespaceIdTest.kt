package io.hamal.runner.run

import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes.Lua54
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.runner.connector.UnitOfWork
import org.junit.jupiter.api.Test

internal class NamespaceIdTest : AbstractTest() {
    @Test
    fun `NamespaceId available in code`() {
        runTest(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                triggerId = TriggerId(4567),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("assert(context.exec.namespace_id == '2694')"),
                codeType = CodeType(Lua54),
                correlation = null
            )
        )
    }
}