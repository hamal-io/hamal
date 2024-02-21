package io.hamal.core.adapter.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import org.springframework.stereotype.Component

fun interface CodeGetPort {
    operator fun invoke(codeId: CodeId, codeVersion: CodeVersion?): Code
}

@Component
class CodeGetAdapter(
    private val codeQueryRepository: CodeQueryRepository
) : CodeGetPort {

    override fun invoke(codeId: CodeId, codeVersion: CodeVersion?): Code {
        ensureCodeExists(codeId)
        return if (codeVersion != null) {
            ensureVersionExists(codeId, codeVersion)
            codeQueryRepository.get(codeId, codeVersion)
        } else {
            ensureCodeExists(codeId)
            codeQueryRepository.get(codeId)
        }
    }

    private fun ensureCodeExists(codeId: CodeId) = codeQueryRepository.get(codeId)

    private fun ensureVersionExists(codeId: CodeId, codeVersion: CodeVersion) {
        codeQueryRepository.get(codeId, codeVersion)
    }
}

