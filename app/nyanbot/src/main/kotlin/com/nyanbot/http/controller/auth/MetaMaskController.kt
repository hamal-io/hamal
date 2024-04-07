package com.nyanbot.http.controller.auth

import com.nyanbot.repository.*
import com.nyanbot.security.DomainGenerateToken
import com.nyanbot.security.SecureRandomSalt
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.web3.evm.EvmSignature
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.domain.EvmSignedMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.temporal.ChronoUnit

data class AuthChallengeMetaMaskRequest(
    val address: Web3Address
)

data class ChallengeMetaMask(
    val challenge: Web3Challenge
)

data class AuthLoginMetaMaskRequest(
    val address: Web3Address,
    val signature: Web3Signature
)

data class TokenRequested(
    val id: AccountId,
    val token: AuthToken
)

@RestController
internal class AuthLoginMetaMaskController(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val generateDomainId: GenerateDomainId,
) {

    @PostMapping("/v1/metamask/challenge")
    fun challenge(@RequestBody req: AuthChallengeMetaMaskRequest): ResponseEntity<ChallengeMetaMask> {
        return ResponseEntity.ok(
            ChallengeMetaMask(challenge(req.address))
        )
    }

    @PostMapping("/v1/metamask/token")
    fun login(@RequestBody req: AuthLoginMetaMaskRequest): ResponseEntity<TokenRequested> {
//        return authLoginMetaMask(req).accepted()
//        verifySignature(req)

        val auth = authRepository.find(req.address)
        if (auth == null) {

            val account = createAccount()

            authRepository.create(
                AuthCmdRepository.CreateMetaMaskAuthCmd(
                    authId = generateDomainId(::AuthId),
                    accountId = account.id,
                    address = req.address
                )
            )

            return authRepository.create(
                AuthCmdRepository.CreateTokenAuthCmd(
                    authId = generateDomainId(::AuthId),
                    accountId = account.id,
                    token = DomainGenerateToken(),
                    expiresAt = ExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
                )
            ).let { auth ->
                require(auth is Auth.Token)
                ResponseEntity.ok(TokenRequested(auth.accountId, auth.token))
            }

        } else {
            if (auth !is HasAccountId) {
                throw NoSuchElementException("Account not found")
            }

            return authRepository.create(
                AuthCmdRepository.CreateTokenAuthCmd(
                    authId = generateDomainId(::AuthId),
                    accountId = auth.accountId,
                    token = DomainGenerateToken(),
                    expiresAt = ExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
                )
            ).let { auth ->
                require(auth is Auth.Token)
                ResponseEntity.ok(TokenRequested(auth.accountId, auth.token))
            }
        }
    }

    private fun createAccount(): Account {
        return accountRepository.create(
            AccountCmdRepository.CreateCmd(
                accountId = generateDomainId(::AccountId),
                accountType = AccountType.User,
                salt = SecureRandomSalt()
            )
        )
    }
}


internal fun verifySignature(req: AuthLoginMetaMaskRequest) {
    val challenge = challenge(req.address)
    val signedMessage = EvmSignedMessage(
        data = challenge.value.toByteArray(),
        signature = EvmSignature(EvmPrefixedHexString(req.signature.value))
    )

    if (signedMessage.address.toPrefixedHexString().value.lowercase() != req.address.value.lowercase()) {
        throw NoSuchElementException("Account not found")
    }
}

private fun challenge(adress: Web3Address): Web3Challenge {
    return Web3Challenge("Please sign this message to login: ${adress.value.substring(2, 10).lowercase()}")
}