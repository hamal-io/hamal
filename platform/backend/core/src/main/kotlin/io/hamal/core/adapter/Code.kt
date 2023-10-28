package io.hamal.core.adapter

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import org.springframework.stereotype.Component

interface CodeGetPort {
    operator fun <T : Any> invoke(codeId: CodeId, codeVersion: CodeVersion, responseHandler: (Code) -> T): T
}

interface CodePort : CodeGetPort

@Component
class CodeAdapter(
    private val codeQueryRepository: CodeQueryRepository
) : CodePort {

    override fun <T : Any> invoke(codeId: CodeId, codeVersion: CodeVersion, responseHandler: (Code) -> T): T {
        ensureCodeExists(codeId)
        return if (codeVersion != CodeVersion(0)) {
            ensureVersionExists(codeId, codeVersion)
            responseHandler(codeQueryRepository.get(codeId, codeVersion))
        } else {
            responseHandler(codeQueryRepository.get(codeId))
        }

    }

    private fun ensureCodeExists(codeId: CodeId) = codeQueryRepository.get(codeId)

    private fun ensureVersionExists(codeId: CodeId, codeVersion: CodeVersion) {
        codeQueryRepository.get(codeId, codeVersion)
    }

}

