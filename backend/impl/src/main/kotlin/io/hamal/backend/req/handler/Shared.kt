package io.hamal.backend.req.handler

import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)
internal fun Req.commandId() = CommandId(id.value)