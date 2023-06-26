package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.req.SubmittedReq
import kotlinx.serialization.Serializable

@Serializable
data class ListSubmittedReqsResponse(
    val reqs: List<SubmittedReq>
)

