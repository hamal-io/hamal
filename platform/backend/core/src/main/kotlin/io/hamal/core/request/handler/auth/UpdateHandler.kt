package io.hamal.core.request.handler.auth

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.AuthUpdateRequested
import io.hamal.repository.api.AuthCmdRepository
import org.springframework.stereotype.Component

@Component
class UpdateHandler(
    private val authCmdRepository: AuthCmdRepository
) : RequestHandler<AuthUpdateRequested>(AuthUpdateRequested::class) {

    override fun invoke(req: AuthUpdateRequested) {
        authCmdRepository.update(
            req.id,
            AuthCmdRepository.UpdateEmailHashCmd(
                id = req.cmdId(),
                hash = req.hash
            )
        )
    }
}