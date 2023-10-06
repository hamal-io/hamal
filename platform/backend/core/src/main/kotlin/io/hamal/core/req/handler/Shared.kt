package io.hamal.core.req.handler

import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.submitted_req.SubmittedReq

internal fun SubmittedReq.cmdId() = CmdId(reqId.value.value)