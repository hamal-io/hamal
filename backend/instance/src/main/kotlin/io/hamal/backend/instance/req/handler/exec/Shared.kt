package io.hamal.backend.instance.req.handler.exec

import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun merge(funcInputs: FuncInputs, invocationInputs: InvocationInputs): ExecInputs = TODO()