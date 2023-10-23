package io.hamal.request

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionName

interface CreateExtensionReq {
    val name: ExtensionName
    val codeId: CodeId
    val codeVersion: CodeVersion
}

interface UpdateExtensionReq {
    val name: ExtensionName?
    val codeId: CodeId?
    val codeVersion: CodeVersion?
}