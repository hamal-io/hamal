package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.*
import io.hamal.repository.api.submitted_req.AuthLoginSubmitted
import io.hamal.request.LogInReq
import org.springframework.stereotype.Component


interface AuthLoginPort {
    operator fun <T : Any> invoke(
        req: LogInReq,
        responseHandler: (AuthLoginSubmitted) -> T
    ): T
}

interface AuthPort : AuthLoginPort

@Component
class AuthAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateDomainId,
    private val generateToken: GenerateToken,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort
) : AuthPort {

    override operator fun <T : Any> invoke(
        req: LogInReq,
        responseHandler: (AuthLoginSubmitted) -> T
    ): T {
        val account = accountQueryRepository.find(req.username) ?: throw NoSuchElementException("Account not found")
        val encodedPassword = encodePassword(req.password, account.salt)
        val match = authRepository.list(account.id).filterIsInstance<PasswordAuth>().any { it.hash == encodedPassword }
        if (!match) {
            throw NoSuchElementException("Account not found")
        }
        return AuthLoginSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
            hash = encodedPassword,
            token = generateToken(),
            name = account.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}