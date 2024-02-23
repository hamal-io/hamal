package io.hamal.core.adapter.code

import io.hamal.core.adapter.security.EnsureAccessPort
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
    private val codeQueryRepository: CodeQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : CodeGetPort {

    override fun invoke(codeId: CodeId, codeVersion: CodeVersion?): Code {
        return if (codeVersion != null) {
            ensureAccess(codeQueryRepository.get(codeId, codeVersion))
        } else {
            ensureAccess(codeQueryRepository.get(codeId))
        }
    }
}

