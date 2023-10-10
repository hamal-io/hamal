package io.hamal.core.adapter

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import org.springframework.stereotype.Component

interface GetCodePort {
    operator fun <T : Any> invoke(codeId: CodeId, codeVersion: CodeVersion, responseHandler: (Code) -> T): T
}

interface CodePort : GetCodePort

@Component
class CodeAdapter(
    private val codeQueryRepository: CodeQueryRepository
) : CodePort {

    override fun <T : Any> invoke(codeId: CodeId, codeVersion: CodeVersion, responseHandler: (Code) -> T): T {
        if (codeVersion != CodeVersion(0)) {
            return responseHandler(codeQueryRepository.get(codeId, codeVersion))
        }
        return responseHandler(codeQueryRepository.get(codeId))

    }
}