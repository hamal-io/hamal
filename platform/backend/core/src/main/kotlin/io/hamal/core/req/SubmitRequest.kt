package io.hamal.core.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.req.req.CreateRootAccountReq
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Anonymous
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
    private val blueprintQueryRepository: BlueprintQueryRepository,
    private val encodePassword: EncodePassword,
    private val eventBrokerRepository: BrokerRepository,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) {

    operator fun invoke(req: CreateRootAccountReq): AccountCreateSubmitted {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = AccountId.root,
            type = Root,
            groupId = GroupId.root,
            namespaceId = NamespaceId.root,
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

    operator fun invoke(req: InvokeExecReq): ExecInvokeSubmitted {
        val func = funcQueryRepository.get(req.funcId)
        return ExecInvokeSubmitted(
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

    operator fun invoke(account: Account, password: Password): AuthLoginSubmitted {
        val encodedPassword = encodePassword(password, account.salt)
        return AuthLoginSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            hash = encodedPassword,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: CreateAccountReq): AccountCreateSubmitted {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::AccountId),
            type = AccountType.User,
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

    operator fun invoke(req: CreateAnonymousAccountReq): AccountCreateSubmitted {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = req.id,
            type = Anonymous,
            groupId = generateDomainId(::GroupId),
            namespaceId = generateDomainId(::NamespaceId),
            name = req.name,
            email = null,
            authenticationId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = Password(">>You-shall-not-know<<"),
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }


    operator fun invoke(namespaceId: NamespaceId, req: InvokeAdhocReq): ExecInvokeSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return ExecInvokeSubmitted(
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


    operator fun invoke(funcId: FuncId, req: InvokeFuncReq): ExecInvokeSubmitted {
        val func = funcQueryRepository.get(funcId)
        return ExecInvokeSubmitted(
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

    operator fun invoke(execId: ExecId, req: CompleteExecReq) = ExecCompleteSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = ExecFailSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateFuncReq): FuncCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return FuncCreateSubmitted(
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

    operator fun invoke(funcId: FuncId, req: UpdateFuncReq) = FuncUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = funcQueryRepository.get(funcId).groupId,
        id = funcId,
        name = req.name,
        inputs = req.inputs,
        code = req.code,
        deployedVersion = null
    ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, deployedVersion: CodeVersion) = FuncUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = funcQueryRepository.get(funcId).groupId,
        id = funcId,
        name = null,
        inputs = null,
        code = null,
        deployedVersion = deployedVersion
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateExtensionReq) = ExtensionCreateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        id = generateDomainId(::ExtensionId),
        name = req.name,
        codeId = generateDomainId(::CodeId),
        code = req.code

    ).also(reqCmdRepository::queue)

    operator fun invoke(extId: ExtensionId, req: UpdateExtensionReq) = ExtensionUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = extensionQueryRepository.get(extId).groupId,
        id = extId,
        name = req.name,
        code = req.code
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, accountId: AccountId, req: CreateBlueprintReq) = BlueprintCreateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        id = generateDomainId(::BlueprintId),
        name = req.name,
        inputs = req.inputs,
        value = req.value,
        creatorId = accountId
    ).also(reqCmdRepository::queue)

    operator fun invoke(blueprintId: BlueprintId, req: UpdateBlueprintReq) = BlueprintUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = blueprintQueryRepository.get(blueprintId).groupId,
        id = blueprintId,
        name = req.name,
        inputs = req.inputs,
        value = req.value,
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateHookReq): HookCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return HookCreateSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::HookId),
            groupId = namespace.groupId,
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(hookId: HookId, req: UpdateHookReq) = HookUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = hookQueryRepository.get(hookId).groupId,
        id = hookId,
        name = req.name,
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateNamespaceReq) = NamespaceCreateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        id = generateDomainId(::NamespaceId),
        groupId = groupId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: UpdateNamespaceReq) = NamespaceUpdateSubmitted(
        reqId = generateDomainId(::ReqId),
        status = Submitted,
        groupId = namespaceQueryRepository.get(namespaceId).groupId,
        id = namespaceId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateTriggerReq): TriggerCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        val func = funcQueryRepository.get(req.funcId)
        return TriggerCreateSubmitted(
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

    operator fun invoke(namespaceId: NamespaceId, req: CreateTopicReq): TopicCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return TopicCreateSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            groupId = namespace.groupId,
            id = generateDomainId(::TopicId),
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: AppendEntryReq): TopicAppendToSubmitted {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        return TopicAppendToSubmitted(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            groupId = topic.groupId,
            id = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: SetStateReq): StateSetSubmitted {
        val func = funcQueryRepository.get(req.correlation.funcId)
        return StateSetSubmitted(
            reqId = generateDomainId(::ReqId), status = Submitted, groupId = func.groupId, state = CorrelatedState(
                correlation = req.correlation, value = req.value
            )
        ).also(reqCmdRepository::queue)
    }
}

