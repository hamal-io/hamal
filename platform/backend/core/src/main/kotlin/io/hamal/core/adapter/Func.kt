package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.springframework.stereotype.Component

interface FuncCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: FuncCreateRequest): FuncCreateRequested
}

fun interface FuncGetPort {
    operator fun invoke(funcId: FuncId): Func
}

@Component
class FuncGetAdapter(private val funcQueryRepository: FuncQueryRepository) : FuncGetPort {
    override fun invoke(funcId: FuncId): Func = funcQueryRepository.get(funcId)
}

interface FuncInvokePort {
    operator fun invoke(funcId: FuncId, req: FuncInvokeRequest, invocation: Invocation): ExecInvokeRequested
}

interface FuncListPort {
    operator fun invoke(query: FuncQuery): List<Func>
}

fun interface FuncDeploymentListPort {
    fun funcDeploymentList(funcId: FuncId): List<FuncDeployment>
}

interface FuncDeployPort {
    operator fun invoke(funcId: FuncId, req: FuncDeployRequest): FuncDeployRequested
}

interface FuncUpdatePort {
    operator fun invoke(funcId: FuncId, req: FuncUpdateRequest): FuncUpdateRequested

}

interface FuncPort : FuncCreatePort, FuncDeployPort, FuncInvokePort, FuncListPort, FuncUpdatePort,
    FuncDeploymentListPort

@Component
class FuncAdapter(
    private val codeQueryRepository: CodeQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val namespaceGet: NamespaceGetPort,
    private val requestCmdRepository: RequestCmdRepository
) : FuncPort {

    override fun invoke(
        namespaceId: NamespaceId,
        req: FuncCreateRequest,
    ): FuncCreateRequested {
        val namespace = namespaceGet(namespaceId)

        return FuncCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = namespace.workspaceId,
            funcId = generateDomainId(::FuncId),
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code
        ).also(requestCmdRepository::queue)
    }

//    override fun invoke(funcId: FuncId) = funcQueryRepository.get(funcId)

    override fun invoke(
        funcId: FuncId,
        req: FuncInvokeRequest,
        invocation: Invocation
    ): ExecInvokeRequested {
        val func = funcQueryRepository.get(funcId)

        val version = req.version?.also {
            codeQueryRepository.get(func.code.id, it)
        } ?: func.code.version

        return ExecInvokeRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            workspaceId = func.workspaceId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = ExecCode(
                id = func.code.id,
                version = version,
                value = null
            ),
            invocation = invocation
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(query: FuncQuery): List<Func> = funcQueryRepository.list(query)

    override fun funcDeploymentList(funcId: FuncId): List<FuncDeployment> {
        ensureFuncExists(funcId)
        return funcQueryRepository.listDeployments(funcId)
    }

    override fun invoke(funcId: FuncId, req: FuncUpdateRequest): FuncUpdateRequested {
        ensureFuncExists(funcId)
        val func = funcQueryRepository.get(funcId)
        return FuncUpdateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = func.workspaceId,
            funcId = funcId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(funcId: FuncId, req: FuncDeployRequest): FuncDeployRequested {
        val func = funcQueryRepository.get(funcId)
        req.version?.let {
            ensureCodeExists(func.code.id, req.version!!)
        }
        return FuncDeployRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = func.workspaceId,
            funcId = funcId,
            version = req.version,
            message = req.message
        ).also(requestCmdRepository::queue)
    }

    private fun ensureFuncExists(funcId: FuncId): Func {
        return funcQueryRepository.get(funcId)
    }

    private fun ensureCodeExists(codeId: CodeId, codeVersion: CodeVersion) {
        codeQueryRepository.get(codeId, codeVersion)
    }
}