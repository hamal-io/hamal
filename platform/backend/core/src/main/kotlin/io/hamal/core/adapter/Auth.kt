package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.*
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.submitted_req.AuthLoginSubmitted
import io.hamal.repository.api.submitted_req.AuthLogoutSubmitted
import io.hamal.request.LogInReq
import io.hamal.request.LogOutReq
import org.springframework.stereotype.Component


interface AuthLoginPort {
    operator fun <T : Any> invoke(
        req: LogInReq,
        responseHandler: (AuthLoginSubmitted) -> T
    ): T
}

interface AuthLogoutPort {
    operator fun <T : Any> invoke(
        req: LogOutReq,
        responseHandler: (AuthLogoutSubmitted) -> T
    ): T
}

interface AuthPort : AuthLoginPort, AuthLogoutPort

@Component
class AuthAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateDomainId,
    private val generateToken: GenerateToken,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort,
    private val flowList: FlowListPort,
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

        val groupIds = groupList(account.id) { groups -> groups.map(Group::id) }

        val flowIds = flowList(FlowQuery(groupIds = groupIds)) { flows ->
            flows.groupBy { it.groupId }.map {
                val default = it.value.minBy { it.id }
                default.groupId to default.id
            }.toMap()
        }

        return AuthLoginSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
            defaultFlowIds = flowIds,
            hash = encodedPassword,
            token = generateToken(),
            name = account.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(req: LogOutReq, responseHandler: (AuthLogoutSubmitted) -> T): T {
        val account = accountQueryRepository.get(req.id)
        return AuthLogoutSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            accountId = req.id,
            token = req.token
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}