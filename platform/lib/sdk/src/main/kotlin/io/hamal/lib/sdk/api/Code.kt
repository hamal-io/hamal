package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import kotlinx.serialization.Serializable

@Serializable
data class ApiCode(
    val id: CodeId,
    val value: CodeValue,
    val version: CodeVersion
)