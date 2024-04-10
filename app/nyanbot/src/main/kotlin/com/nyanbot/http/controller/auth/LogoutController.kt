package com.nyanbot.http.controller.auth

import com.nyanbot.repository.AuthCmdRepository
import com.nyanbot.repository.AuthRepository
import com.nyanbot.security.SecurityContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LogoutController(
    private val authRepository: AuthRepository
) {

    @PostMapping("/v1/logout")
    fun logout(): ResponseEntity<Unit> {
        val auth = SecurityContext.current
        authRepository.revokeAuth(AuthCmdRepository.RevokeAuthCmd(auth.id))
        return ResponseEntity.noContent().build()
    }

}