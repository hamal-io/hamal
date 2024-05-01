package io.hamal.lib.sdk.bridge

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion

data class BridgeCode(
    val id: CodeId,
    val value: ValueCode,
    val version: CodeVersion
)