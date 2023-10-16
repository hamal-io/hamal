package io.hamal.core.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.req.req.CreateRootAccountReq
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.submitted_req.*
import io.hamal.request.*
import org.springframework.stereotype.Component

data class InvokeExecReq(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: CodeValue?,
    val codeId: CodeId?,
    val codeVersion: CodeVersion?,
    val events: List<Event>
)

@Component
class SubmitRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val generateSalt: GenerateSalt,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val execQueryRepository: ExecQueryRepository,
    private val eventBrokerRepository: BrokerRepository
) {

    operator fun invoke(req: CreateRootAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = AccountId.root,
            type = Root,
            groupId = GroupId.root,
            namespaceId = generateDomainId(::NamespaceId),
            name = req.name,
            email = req.email,
            authenticationId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password, salt = salt
            ),
            salt = salt,
            token = generateToken()
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
            code = ExecCode(id = func.codeId, version = func.codeVersion),
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

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

    operator fun invoke(req: CreateAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::AccountId),
            type = AccountType.Enjoyer,
            groupId = generateDomainId(::GroupId),
            namespaceId = generateDomainId(::NamespaceId),
            name = req.name,
            email = req.email,
            authenticationId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password ?: throw NoSuchElementException("Account not found"), salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(groupId: GroupId, req: InvokeAdhocReq) = SubmittedInvokeExecReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = generateDomainId(::ExecId),
        groupId = groupId,
        inputs = req.inputs,
        code = ExecCode(value = req.code),
        funcId = null,
        correlationId = null,
        events = listOf()
    ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, req: InvokeFuncReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = ExecCode(id = func.codeId, func.codeVersion),
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: CompleteExecReq) = SubmittedCompleteExecReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = execQueryRepository.get(execId).groupId,
        id = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = SubmittedFailExecReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = execQueryRepository.get(execId).groupId,
        id = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateFuncReq) = SubmittedCreateFuncReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = generateDomainId(::FuncId),
        groupId = groupId,
        namespaceId = req.namespaceId ?: namespaceQueryRepository.find(NamespaceName("hamal"))!!.id,
        name = req.name,
        inputs = req.inputs,
        code = req.code,
        codeId = generateDomainId(::CodeId)
    ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, req: UpdateFuncReq) = SubmittedUpdateFuncReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = funcQueryRepository.get(funcId).groupId,
        id = funcId,
        namespaceId = req.namespaceId,
        name = req.name,
        inputs = req.inputs,
        code = req.code,

        ).also(reqCmdRepository::queue)


    operator fun invoke(groupId: GroupId, req: CreateHookReq) = SubmittedCreateHookReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = generateDomainId(::HookId),
        groupId = groupId,
        namespaceId = req.namespaceId ?: namespaceQueryRepository.find(NamespaceName("hamal"))!!.id,
        name = req.name
    ).also(reqCmdRepository::queue)

    operator fun invoke(hookId: HookId, req: UpdateHookReq) = SubmittedUpdateHookReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = hookQueryRepository.get(hookId).groupId,
        id = hookId,
        namespaceId = req.namespaceId,
        name = req.name,
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateNamespaceReq) = SubmittedCreateNamespaceReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = generateDomainId(::NamespaceId),
        groupId = groupId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: UpdateNamespaceReq) = SubmittedUpdateNamespaceReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = namespaceQueryRepository.get(namespaceId).groupId,
        id = namespaceId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(req: CreateTriggerReq): SubmittedCreateTriggerReq {
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
            topicId = req.topicId,
            hookId = req.hookId
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(groupId: GroupId, req: CreateTopicReq) = SubmittedCreateTopicReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        id = generateDomainId(::TopicId),
        name = req.name
    ).also(reqCmdRepository::queue)

    operator fun invoke(req: AppendEntryReq): SubmittedAppendToTopicReq {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        return SubmittedAppendToTopicReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            groupId = topic.groupId,
            id = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: SetStateReq): SubmittedSetStateReq {
        val func = funcQueryRepository.get(req.correlation.funcId)
        return SubmittedSetStateReq(
            reqId = generateDomainId(::ReqId), status = Submitted, groupId = func.groupId, state = CorrelatedState(
                correlation = req.correlation, value = req.value
            )
        ).also(reqCmdRepository::queue)
    }
}

