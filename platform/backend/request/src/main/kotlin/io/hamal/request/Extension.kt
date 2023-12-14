package io.hamal.request

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionName

interface ExtensionCreateReq {
    val name: ExtensionName
    val code: CodeValue
}

interface ExtensionUpdateReq {
    val name: ExtensionName?
    val code: CodeValue?
}