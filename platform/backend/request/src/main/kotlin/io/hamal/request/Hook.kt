package io.hamal.request

import io.hamal.lib.domain.vo.HookName

interface CreateHookReq {
    val name: HookName
}

interface UpdateHookReq {
    val name: HookName?
}