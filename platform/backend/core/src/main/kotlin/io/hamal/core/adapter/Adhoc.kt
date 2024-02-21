package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.AdhocInvokeRequest
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component


interface AdhocInvokePort {
    operator fun invoke(
        namespaceId: NamespaceId,
        req: AdhocInvokeRequest
    ): ExecInvokeRequested
}

interface AdhocPort : AdhocInvokePort

@Component
class AdhocAdapter(
    private val generateDomainId: GenerateDomainId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : AdhocPort {
    override operator fun invoke(
        namespaceId: NamespaceId,
        req: AdhocInvokeRequest
    ): ExecInvokeRequested {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return ExecInvokeRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = namespace.id,
            workspaceId = namespace.workspaceId,
            inputs = req.inputs,
            code = ExecCode(value = req.code),
            funcId = null,
            correlationId = null,
            invocation = Invocation.Adhoc
        ).also(requestCmdRepository::queue)
    }
}
