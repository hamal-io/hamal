package io.hamal.core.request.handler

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.Requested

internal fun Requested.cmdId() = CmdId(id.value.value)