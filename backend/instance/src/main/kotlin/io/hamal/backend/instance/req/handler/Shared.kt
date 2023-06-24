package io.hamal.backend.instance.req.handler

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.req.Req
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)
internal fun Req.cmdId() = CmdId(id.value)