package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import kotlinx.serialization.Serializable

@Serializable
data class CreateNamespaceReq(
    val name: NamespaceName,
    val inputs: NamespaceInputs
)


@Serializable
data class UpdateNamespaceReq(
    val name: NamespaceName,
    val inputs: NamespaceInputs,
)
