package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId

sealed interface Submitted {
    val id: ReqId
    var status: ReqStatus
}