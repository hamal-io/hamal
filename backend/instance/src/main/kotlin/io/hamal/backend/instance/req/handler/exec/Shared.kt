package io.hamal.backend.instance.req.handler.exec

import io.hamal.lib.domain.vo.*

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)

internal fun merge(funcInputs: FuncInputs, invocationInputs: InvocationInputs): ExecInputs = TODO()
internal fun merge(funcSecrets: FuncSecrets, invocationSecrets: InvocationSecrets): ExecSecrets = TODO()