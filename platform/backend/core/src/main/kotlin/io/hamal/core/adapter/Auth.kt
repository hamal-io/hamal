package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.submitted.AuthLoginEmailSubmitted
import io.hamal.lib.domain.submitted.AuthLoginMetaMaskSubmitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.request.AccountCreateMetaMaskReq
import io.hamal.request.AuthChallengeMetaMaskReq
import io.hamal.request.AuthLogInEmailReq
import io.hamal.request.AuthLogInMetaMaskReq
import org.springframework.stereotype.Component


interface AuthLoginEmailPort {
    operator fun <T : Any> invoke(
        req: AuthLogInEmailReq,
        responseHandler: (AuthLoginEmailSubmitted) -> T
    ): T
}

interface AuthLoginMetaMaskPort {
    operator fun <T : Any> invoke(
        req: AuthLogInMetaMaskReq,
        responseHandler: (AuthLoginMetaMaskSubmitted) -> T
    ): T
}

interface AuthChallengeMetaMaskPort {
    operator fun invoke(req: AuthChallengeMetaMaskReq): Web3Challenge
}

interface AuthPort : AuthChallengeMetaMaskPort, AuthLoginMetaMaskPort, AuthLoginEmailPort

@Component
class AuthAdapter(
    private val createAccount: AccountCreateMetaMaskPort,
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateId,
    private val generateToken: GenerateToken,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort,
    private val flowList: FlowListPort,
) : AuthPort {

    override fun invoke(req: AuthChallengeMetaMaskReq): Web3Challenge {
        // FIXME 138 - create challenge based on address
        return Web3Challenge("challenge123")
    }

    override fun <T : Any> invoke(req: AuthLogInMetaMaskReq, responseHandler: (AuthLoginMetaMaskSubmitted) -> T): T {
        // FIXME 138 - verify signature
        val auth = authRepository.find(req.address)
        if (auth == null) {
            val submitted = createAccount(object : AccountCreateMetaMaskReq {
                override val id: AccountId = generateDomainId(::AccountId)
                override val address: Web3Address = req.address
            }) { it }

            return AuthLoginMetaMaskSubmitted(
                id = generateDomainId(::ReqId),
                status = ReqStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = submitted.accountId,
                groupIds = listOf(submitted.groupId),
                defaultFlowIds = listOf(io.hamal.lib.domain.vo.GroupDefaultFlowId(submitted.groupId, submitted.flowId)),
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(reqCmdRepository::queue).let(responseHandler)

        } else {
            val groupIds = groupList(auth.accountId) { groups -> groups.map(Group::id) }
            val flows = flowList(FlowQuery(groupIds = groupIds, limit = Limit.all)) { it }
            val defaultFlowIds = flows.filter { it.name == FlowName.default }
                .map {
                    io.hamal.lib.domain.vo.GroupDefaultFlowId(it.groupId, it.id)
                }

            return AuthLoginMetaMaskSubmitted(
                id = generateDomainId(::ReqId),
                status = ReqStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = auth.accountId,
                groupIds = groupIds,
                defaultFlowIds = defaultFlowIds,
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(reqCmdRepository::queue).let(responseHandler)
        }
    }

    override operator fun <T : Any> invoke(
        req: AuthLogInEmailReq,
        responseHandler: (AuthLoginEmailSubmitted) -> T
    ): T {
        val auth = authRepository.find(req.email) ?: throw NoSuchElementException("Account not found")
        val account = accountQueryRepository.find(auth.accountId) ?: throw NoSuchElementException("Account not found")

        val encodedPassword = encodePassword(req.password, account.salt)
        val match = authRepository.list(account.id).filterIsInstance<EmailAuth>().any { it.hash == encodedPassword }
        if (!match) {
            throw NoSuchElementException("Account not found")
        }

        val groupIds = groupList(account.id) { groups -> groups.map(Group::id) }

        val flows = flowList(FlowQuery(groupIds = groupIds, limit = Limit.all)) { it }
        val defaultFlowIds = flows.filter { it.name == FlowName.default }
            .map { io.hamal.lib.domain.vo.GroupDefaultFlowId(it.groupId, it.id) }

        return AuthLoginEmailSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
            defaultFlowIds = defaultFlowIds,
            hash = encodedPassword,
            token = generateToken()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}