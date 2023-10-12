package io.hamal.request

import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.NamespaceId

interface CreateHookReq {
    val namespaceId: NamespaceId?
    val name: HookName
}


interface UpdateHookReq {
    val namespaceId: NamespaceId?
    val name: HookName?
}
