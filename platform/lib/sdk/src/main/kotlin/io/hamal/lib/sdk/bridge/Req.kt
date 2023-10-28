package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId
import kotlinx.serialization.Serializable

@Serializable
sealed interface BridgeSubmitted {
    val id: ReqId
    val status: ReqStatus
}
