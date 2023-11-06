package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.ExecCode
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import io.hamal.request.InvokeAdhocReq
import org.springframework.stereotype.Component


interface AdhocInvokePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T
}

interface AdhocPort : AdhocInvokePort

@Component
class AdhocAdapter(
    private val generateDomainId: GenerateDomainId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : AdhocPort {
    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T {
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
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}
