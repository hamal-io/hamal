package io.hamal.request

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionName

interface CreateExtensionReq {
    val name: ExtensionName
    val code: CodeValue
}

interface UpdateExtensionReq {
    val name: ExtensionName?
    val code: CodeValue?
}