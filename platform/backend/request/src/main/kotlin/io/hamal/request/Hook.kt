package io.hamal.request

import io.hamal.lib.domain.vo.HookName

interface HookCreateReq {
    val name: HookName
}

interface HookUpdateReq {
    val name: HookName?
}