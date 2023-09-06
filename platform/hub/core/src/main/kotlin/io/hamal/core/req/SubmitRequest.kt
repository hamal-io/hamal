package io.hamal.core.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.req.req.CreateRootAccountReq
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.kua.type.CodeType
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedCreateAccountWithPasswordReq
import io.hamal.repository.api.submitted_req.SubmittedInvokeExecReq
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
internal class SubmitCoreRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateSalt: GenerateSalt,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken
) {

    operator fun invoke(req: CreateRootAccountReq): SubmittedCreateAccountWithPasswordReq {
        val salt = generateSalt()
        return SubmittedCreateAccountWithPasswordReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::AccountId),
            type = Root,
            groupId = generateDomainId(::GroupId),
            namespaceId = generateDomainId(::NamespaceId),
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
}

