package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
sealed interface Submitted {
    val id: ReqId
    var status: ReqStatus
}