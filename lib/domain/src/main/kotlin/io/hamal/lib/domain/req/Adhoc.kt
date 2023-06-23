package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class InvokeAdhocReq(
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
)

@Serializable
data class SubmittedInvokeAdhocReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val tenantId: TenantId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
) : Req
