package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ApiRequest {
    val id: ReqId
    val status: ReqStatus
}

@Serializable
@SerialName("Adhoc")
data class ApiAdhocRequest(
    override val id: ReqId,
    override val status: ReqStatus,
    val execId: ExecId,
    val execStatus: ExecStatus
) : ApiRequest