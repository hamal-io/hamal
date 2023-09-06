package io.hamal.core.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Enjoyer
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.*
import io.hamal.repository.api.Account
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.*
import org.springframework.stereotype.Component

data class InvokeExecReq(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: CodeType,
    val events: List<Event>
)

@Component
class SubmitRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val generateSalt: GenerateSalt,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken
) {

    operator fun invoke(account: Account, password: Password): SubmittedSignInWithPasswordReq {
        val encodedPassword = encodePassword(password, account.salt)
        return SubmittedSignInWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            hash = encodedPassword,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: CreateRootAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::AccountId),
            type = Root,
            groupId = generateDomainId(::GroupId),
            name = req.name,
            email = req.email,
            authenticationId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password,
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: HubCreateAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::AccountId),
            type = Enjoyer,
            groupId = generateDomainId(::GroupId),
            name = req.name,
            email = req.email,
            authenticationId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password ?: throw NoSuchElementException("Account not found"),
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(groupId: GroupId, req: HubInvokeAdhocReq) =
        SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            groupId = groupId,
            inputs = req.inputs,
            code = req.code,
            funcId = null,
            correlationId = null,
            events = listOf()
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, req: HubInvokeFuncReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = func.code,
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: InvokeExecReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(req.funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            groupId = func.groupId,
            funcId = req.funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = req.code,
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: HubCompleteExecReq) =
        SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = execId,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: HubFailExecReq) =
        SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = execId,
            cause = req.cause
        ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: HubCreateFuncReq) =
        SubmittedCreateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::FuncId),
            groupId = groupId,
            namespaceId = req.namespaceId ?: namespaceQueryRepository.find(NamespaceName("hamal"))!!.id,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, req: HubUpdateFuncReq) =
        SubmittedUpdateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = funcId,
            namespaceId = req.namespaceId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: HubCreateNamespaceReq) =
        SubmittedCreateNamespaceReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::NamespaceId),
            groupId = groupId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: HubUpdateNamespaceReq) =
        SubmittedUpdateNamespaceReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = namespaceId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: HubCreateTriggerReq): SubmittedCreateTriggerReq {
        val func = funcQueryRepository.get(req.funcId)
        return SubmittedCreateTriggerReq(
            type = req.type,
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::TriggerId),
            groupId = func.groupId,
            name = req.name,
            funcId = func.id,
            namespaceId = req.namespaceId,
            inputs = req.inputs,
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: HubCreateTopicReq) =
        SubmittedCreateTopicReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::TopicId),
            name = req.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: HubAppendEntryReq) =
        SubmittedAppendToTopicReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: HubSetStateReq) =
        SubmittedSetStateReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            state = CorrelatedState(
                correlation = req.correlation,
                value = req.value
            )
        ).also(reqCmdRepository::queue)
}

