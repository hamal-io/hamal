package io.hamal.admin.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.admin.*
import io.hamal.repository.api.Account
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.*
import org.springframework.stereotype.Component

@Component
internal class SubmitAdminRequest(
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
            status = ReqStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            hash = encodedPassword,
            token = generateToken()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(req: AdminCreateAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::AccountId),
            type = AccountType.Enjoyer,
            groupId = generateDomainId(::GroupId),
            namespaceId = generateDomainId(::NamespaceId),
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

    operator fun invoke(groupId: GroupId, req: AdminInvokeAdhocReq) =
        SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::ExecId),
            groupId = groupId,
            inputs = req.inputs,
            code = req.code,
            funcId = null,
            correlationId = null,
            events = listOf()
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, req: AdminInvokeFuncReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::ExecId),
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = func.code,
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: AdminCompleteExecReq) =
        SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: AdminFailExecReq) =
        SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            cause = req.cause
        ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: AdminCreateFuncReq) =
        SubmittedCreateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::FuncId),
            groupId = groupId,
            namespaceId = req.namespaceId ?: namespaceQueryRepository.find(NamespaceName("hamal"))!!.id,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, req: AdminUpdateFuncReq) =
        SubmittedUpdateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = funcId,
            namespaceId = req.namespaceId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(groupId: GroupId, req: AdminCreateNamespaceReq) =
        SubmittedCreateNamespaceReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::NamespaceId),
            groupId = groupId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue)

    operator fun invoke(namespaceId: NamespaceId, req: AdminUpdateNamespaceReq) =
        SubmittedUpdateNamespaceReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = namespaceId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: AdminCreateTriggerReq): SubmittedCreateTriggerReq {
        val func = funcQueryRepository.get(req.funcId)
        return SubmittedCreateTriggerReq(
            type = req.type,
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
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

    operator fun invoke(req: AdminCreateTopicReq) =
        SubmittedCreateTopicReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::TopicId),
            name = req.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: AdminAppendEntryReq) =
        SubmittedAppendToTopicReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue)

    operator fun invoke(req: AdminSetStateReq) =
        SubmittedSetStateReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            state = CorrelatedState(
                correlation = req.correlation,
                value = req.value
            )
        ).also(reqCmdRepository::queue)

}
