package io.hamal.api.http.controller.account

import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.AuthRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountLogoutController(
    private val generateCmdId: GenerateCmdId,
    private val authRepository: AuthRepository
) {

    @PostMapping("/v1/logout")
    fun logout(): ResponseEntity<Unit> {
        val auth = SecurityContext.current
        authRepository.revokeAuth(AuthCmdRepository.RevokeAuthCmd(generateCmdId(), auth.id))
        return ResponseEntity.accepted().build()
    }

}