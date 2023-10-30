package io.hamal.core.req

import io.hamal.core.adapter.GroupListPort
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
    private val eventBrokerRepository: BrokerRepository,
    private val encodePassword: EncodePassword,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort
) {

    operator fun invoke(req: CreateRootAccountReq): AccountCreateSubmitted {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = AccountId.root,
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
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            groupId = func.groupId,
            funcId = req.funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = func.code.toExecCode(),
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(account: Account, passwordHash: PasswordHash): AuthLoginSubmitted {
        return AuthLoginSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            groupIds = groupList(account.id) { groups -> groups.map(Group::id) },
            hash = passwordHash,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: CreateAccountReq): AccountCreateSubmitted {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = generateDomainId(::AccountId),
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
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = req.id,
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
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
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
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
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
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = ExecFailSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateFuncReq): FuncCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return FuncCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = namespace.groupId,
            funcId = generateDomainId(::FuncId),
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(funcId: FuncId, req: UpdateFuncReq) = FuncUpdateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = funcQueryRepository.get(funcId).groupId,
        funcId = funcId,
        name = req.name,
        inputs = req.inputs,
        code = req.code
    ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, versionToDeploy: CodeVersion): FuncDeploySubmitted {
        return FuncDeploySubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = funcQueryRepository.get(funcId).groupId,
            funcId = funcId,
            versionToDeploy = versionToDeploy
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(groupId: GroupId, req: CreateExtensionReq) = ExtensionCreateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        extensionId = generateDomainId(::ExtensionId),
        name = req.name,
        codeId = generateDomainId(::CodeId),
        code = req.code

    ).also(reqCmdRepository::queue)

    operator fun invoke(extId: ExtensionId, req: UpdateExtensionReq) = ExtensionUpdateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = extensionQueryRepository.get(extId).groupId,
        extensionId = extId,
        name = req.name,
        code = req.code
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, accountId: AccountId, req: CreateBlueprintReq) = BlueprintCreateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = groupId,
        blueprintId = generateDomainId(::BlueprintId),
        name = req.name,
        inputs = req.inputs,
        value = req.value,
        creatorId = accountId
    ).also(reqCmdRepository::queue)

    operator fun invoke(bpId: BlueprintId, req: UpdateBlueprintReq) = BlueprintUpdateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = blueprintQueryRepository.get(bpId).groupId,
        blueprintId = bpId,
        name = req.name,
        inputs = req.inputs,
        value = req.value,
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateHookReq): HookCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return HookCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            hookId = generateDomainId(::HookId),
            groupId = namespace.groupId,
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(hookId: HookId, req: UpdateHookReq) = HookUpdateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = hookQueryRepository.get(hookId).groupId,
        hookId = hookId,
        name = req.name,
    ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: CreateNamespaceReq) = NamespaceCreateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        namespaceId = generateDomainId(::NamespaceId),
        groupId = groupId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: UpdateNamespaceReq) = NamespaceUpdateSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        groupId = namespaceQueryRepository.get(namespaceId).groupId,
        namespaceId = namespaceId,
        name = req.name,
        inputs = req.inputs
    ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: CreateTriggerReq): TriggerCreateSubmitted {
        val namespace = namespaceQueryRepository.get(namespaceId)
        val func = funcQueryRepository.get(req.funcId)
        return TriggerCreateSubmitted(
            type = req.type,
            id = generateDomainId(::ReqId),
            status = Submitted,
            triggerId = generateDomainId(::TriggerId),
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
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = namespace.groupId,
            topicId = generateDomainId(::TopicId),
            namespaceId = namespace.id,
            name = req.name
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: AppendEntryReq): TopicAppendToSubmitted {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        return TopicAppendToSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = topic.groupId,
            topicId = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: SetStateReq): StateSetSubmitted {
        val func = funcQueryRepository.get(req.correlation.funcId)
        return StateSetSubmitted(
            id = generateDomainId(::ReqId), status = Submitted, groupId = func.groupId, state = CorrelatedState(
                correlation = req.correlation, value = req.value
            )
        ).also(reqCmdRepository::queue)
    }
}
