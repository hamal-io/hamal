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
    val code: ExecCode,
    val events: List<Event>
)

@Component
class SubmitRequest(
    private val encodePassword: EncodePassword,
    private val eventBrokerRepository: BrokerRepository,
    private val execQueryRepository: ExecQueryRepository,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: ReqCmdRepository,
    private val snippetQueryRepository: SnippetQueryRepository
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
            namespaceId = func.namespaceId,
            groupId = func.groupId,
            funcId = req.funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = func.code.toExecCode(),
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

    operator fun invoke(namespaceId: NamespaceId, req: InvokeAdhocReq): SubmittedInvokeExecReq {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            namespaceId = namespace.id,
            groupId = namespace.groupId,
            inputs = req.inputs,
            code = ExecCode(value = req.code),
            funcId = null,
            correlationId = null,
            events = listOf()
        ).also(reqCmdRepository::queue)
    }


    operator fun invoke(funcId: FuncId, req: InvokeFuncReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = func.code.toExecCode(),
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: CompleteExecReq) = SubmittedCompleteExecReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = SubmittedFailExecReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateFuncReq): SubmittedCreateFuncReq {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return SubmittedCreateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            groupId = namespace.groupId,
            id = generateDomainId(::FuncId),
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(funcId: FuncId, req: UpdateFuncReq) = SubmittedUpdateFuncReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = funcQueryRepository.get(funcId).groupId,
        id = funcId,
        name = req.name,
        inputs = req.inputs,
        code = req.code,
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateExtensionReq) = SubmittedCreateExtensionReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        id = generateDomainId(::ExtensionId),
        name = req.name,
        codeId = generateDomainId(::CodeId),
        code = req.code

    ).also(reqCmdRepository::queue)

    operator fun invoke(extId: ExtensionId, req: UpdateExtensionReq) = SubmittedUpdateExtensionReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = extensionQueryRepository.get(extId).groupId,
        id = extId,
        name = req.name,
        code = req.code
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, accountId: AccountId, req: CreateSnippetReq) = SubmittedCreateSnippetReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        id = generateDomainId(::SnippetId),
        name = req.name,
        inputs = req.inputs,
        value = req.value,
        creatorId = accountId
    ).also(reqCmdRepository::queue)

    operator fun invoke(snippetId: SnippetId, req: UpdateSnippetReq) = SubmittedUpdateSnippetReq(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = snippetQueryRepository.get(snippetId).groupId,
        id = snippetId,
        name = req.name,
        inputs = req.inputs,
        value = req.value,
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateHookReq): SubmittedCreateHookReq {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return SubmittedCreateHookReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::HookId),
            groupId = namespace.groupId,
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

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

    operator fun invoke(namespaceId: NamespaceId, req: CreateTriggerReq): SubmittedCreateTriggerReq {
        val namespace = namespaceQueryRepository.get(namespaceId)
        val func = funcQueryRepository.get(req.funcId)
        return SubmittedCreateTriggerReq(
            type = req.type,
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::TriggerId),
            groupId = namespace.groupId,
            name = req.name,
            funcId = func.id,
            namespaceId = namespaceId,
            inputs = req.inputs,
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId,
            hookId = req.hookId
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(namespaceId: NamespaceId, req: CreateTopicReq): SubmittedCreateTopicReq {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return SubmittedCreateTopicReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            groupId = namespace.groupId,
            id = generateDomainId(::TopicId),
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

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

