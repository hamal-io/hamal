package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import kotlinx.serialization.Serializable

@Serializable
data class ApiNamespaceList(
    val namespaces: List<ApiSimpleNamespace>
) {
    @Serializable
    data class ApiSimpleNamespace(
        val id: NamespaceId, val name: NamespaceName
    )
}


@Serializable
data class ApiNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
)