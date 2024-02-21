package io.hamal.core.adapter

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import org.springframework.stereotype.Component

interface CodeGetPort {
    operator fun invoke(codeId: CodeId, codeVersion: CodeVersion?): Code
}

interface CodePort : CodeGetPort

@Component
class CodeAdapter(
    private val codeQueryRepository: CodeQueryRepository
) : CodePort {

    override fun invoke(codeId: CodeId, codeVersion: CodeVersion?): Code {
        ensureCodeExists(codeId)
        return if (codeVersion != null) {
            ensureVersionExists(codeId, codeVersion)
            codeQueryRepository.get(codeId, codeVersion)
        } else {
            codeQueryRepository.get(codeId)
        }

    }

    private fun ensureCodeExists(codeId: CodeId) = codeQueryRepository.get(codeId)

    private fun ensureVersionExists(codeId: CodeId, codeVersion: CodeVersion) {
        codeQueryRepository.get(codeId, codeVersion)
    }
}

