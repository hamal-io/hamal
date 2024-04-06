package io.hamal.core.request.handler.auth

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.AuthUpdateRequested
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.event.AuthUpdateEvent
import org.springframework.stereotype.Component

@Component
class AuthUpdateHandler(
    private val authCmdRepository: AuthCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AuthUpdateRequested>(AuthUpdateRequested::class) {


    override fun invoke(req: AuthUpdateRequested) {
        updateHash(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateHash(req: AuthUpdateRequested): Auth {
        return authCmdRepository.update(
            req.id,
            AuthCmdRepository.UpdateEmailHashCmd(
                id = req.cmdId(),
                hash = req.hash
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, auth: Auth) {
        eventEmitter.emit(cmdId, AuthUpdateEvent(auth))
    }
}