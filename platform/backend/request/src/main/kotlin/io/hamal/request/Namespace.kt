package io.hamal.request

import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName

interface CreateNamespaceReq {
    val name: NamespaceName
    val inputs: NamespaceInputs
}


interface UpdateNamespaceReq {
    val name: NamespaceName
    val inputs: NamespaceInputs
}
