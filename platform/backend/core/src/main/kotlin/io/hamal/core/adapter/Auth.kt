package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.submitted_req.AuthLoginMetaMaskSubmitted
import io.hamal.repository.api.submitted_req.AuthLoginPasswordSubmitted
import io.hamal.request.ChallengeMetaMaskReq
import io.hamal.request.CreateMetaMaskAccountReq
import io.hamal.request.LogInMetaMaskReq
import io.hamal.request.LogInPasswordReq
import org.springframework.stereotype.Component


interface AuthLoginPasswordPort {
    operator fun <T : Any> invoke(
        req: LogInPasswordReq,
        responseHandler: (AuthLoginPasswordSubmitted) -> T
    ): T
}

interface AuthLoginMetaMaskPort {
    operator fun <T : Any> invoke(
        req: LogInMetaMaskReq,
        responseHandler: (AuthLoginMetaMaskSubmitted) -> T
    ): T
}

interface AuthChallengeMetaMaskPort {
    operator fun invoke(req: ChallengeMetaMaskReq): Web3Challenge
}

interface AuthPort : AuthChallengeMetaMaskPort, AuthLoginMetaMaskPort, AuthLoginPasswordPort

@Component
class AuthAdapter(
    private val createAccount: AccountCreateMetaMaskPort,
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateDomainId,
    private val generateToken: GenerateToken,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort,
    private val flowList: FlowListPort,
) : AuthPort {

    override fun invoke(req: ChallengeMetaMaskReq): Web3Challenge {
        return Web3Challenge("challenge123")
    }

    override fun <T : Any> invoke(req: LogInMetaMaskReq, responseHandler: (AuthLoginMetaMaskSubmitted) -> T): T {
        val accountName = AccountName(req.address.value)
        val account = accountQueryRepository.find(accountName)

        if (account == null) {
            val submitted = createAccount(object : CreateMetaMaskAccountReq {
                override val id: AccountId = generateDomainId(::AccountId)
                override val name: AccountName = accountName
                override val address: Web3Address = req.address
            }) { it }

            val groupIds = groupList(submitted.accountId) { groups -> groups.map(Group::id) }

            val flows = flowList(FlowQuery(groupIds = groupIds, limit = Limit.all)) { it }
            val defaultFlowIds = flows.filter { it.name == FlowName.default }.associate { it.groupId to it.id }

            return AuthLoginMetaMaskSubmitted(
                id = generateDomainId(::ReqId),
                status = ReqStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = submitted.accountId,
                groupIds = groupIds,
                defaultFlowIds = defaultFlowIds,
                token = generateToken(),
                name = accountName,
                address = req.address,
                signature = req.signature
            ).also(reqCmdRepository::queue).let(responseHandler)

        } else {
            val groupIds = groupList(account.id) { groups -> groups.map(Group::id) }

            val flows = flowList(FlowQuery(groupIds = groupIds, limit = Limit.all)) { it }
            val defaultFlowIds = flows.filter { it.name == FlowName.default }.associate { it.groupId to it.id }

            return AuthLoginMetaMaskSubmitted(
                id = generateDomainId(::ReqId),
                status = ReqStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = account.id,
                groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
                defaultFlowIds = defaultFlowIds,
                token = generateToken(),
                name = accountName,
                address = req.address,
                signature = req.signature
            ).also(reqCmdRepository::queue).let(responseHandler)
        }
    }

    override operator fun <T : Any> invoke(
        req: LogInPasswordReq,
        responseHandler: (AuthLoginPasswordSubmitted) -> T
    ): T {
        val account = accountQueryRepository.find(req.name) ?: throw NoSuchElementException("Account not found")
        val encodedPassword = encodePassword(req.password, account.salt)
        val match = authRepository.list(account.id).filterIsInstance<PasswordAuth>().any { it.hash == encodedPassword }
        if (!match) {
            throw NoSuchElementException("Account not found")
        }

        val groupIds = groupList(account.id) { groups -> groups.map(Group::id) }

        val flows = flowList(FlowQuery(groupIds = groupIds, limit = Limit.all)) { it }
        val defaultFlowIds = flows.filter { it.name == FlowName.default }.associate { it.groupId to it.id }

        return AuthLoginPasswordSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
            defaultFlowIds = defaultFlowIds,
            hash = encodedPassword,
            token = generateToken(),
            name = account.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}