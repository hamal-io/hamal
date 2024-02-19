package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.springframework.stereotype.Component

interface FuncCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: FuncCreateRequest,
        responseHandler: (FuncCreateRequested) -> T
    ): T
}

interface FuncGetPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Code, Namespace) -> T): T
}

interface FuncInvokePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncInvokeRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T

    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncInvokeVersionRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T
}

interface FuncListPort {
    operator fun <T : Any> invoke(query: FuncQuery, responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T): T
}

interface FuncDeploymentListPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (List<FuncDeployment>) -> T): T
}

interface FuncDeployPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncDeployRequest,
        responseHandler: (FuncDeployRequested) -> T
    ): T
}

interface FuncUpdatePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncUpdateRequest,
        responseHandler: (FuncUpdateRequested) -> T
    ): T

}

interface FuncPort : FuncCreatePort, FuncDeployPort, FuncGetPort, FuncInvokePort, FuncListPort, FuncUpdatePort,
    FuncDeploymentListPort

@Component
class FuncAdapter(
    private val codeQueryRepository: CodeQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : FuncPort {

    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: FuncCreateRequest,
        responseHandler: (FuncCreateRequested) -> T
    ): T {
        val namespace = namespaceQueryRepository.get(namespaceId)
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Code, Namespace) -> T): T {
        val func = funcQueryRepository.get(funcId)
        val current = codeQueryRepository.get(func.code.id, func.code.version)
        val deployed = codeQueryRepository.get(func.code.id, func.deployment.version)
        val namespaces = namespaceQueryRepository.get(func.namespaceId)
        return responseHandler(func, current, deployed, namespaces)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncInvokeRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)

        return ExecInvokeRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            workspaceId = func.workspaceId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = func.deployment.toExecCode(),
            invocation = req.invocation
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncInvokeVersionRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T {
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
            invocation = Invocation.DeprecatedEmptyInvocation
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        query: FuncQuery,
        responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T
    ): T {
        val funcs = funcQueryRepository.list(query)
        val namespaces = namespaceQueryRepository.list(funcs.map(Func::namespaceId))
            .associateBy(Namespace::id)
        return responseHandler(funcs, namespaces)
    }


    override fun <T : Any> invoke(
        funcId: FuncId,
        responseHandler: (List<FuncDeployment>) -> T
    ): T {
        ensureFuncExists(funcId)
        return responseHandler(funcQueryRepository.listDeployments(funcId))
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncUpdateRequest,
        responseHandler: (FuncUpdateRequested) -> T
    ): T {
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: FuncDeployRequest,
        responseHandler: (FuncDeployRequested) -> T
    ): T {
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFuncExists(funcId: FuncId): Func {
        return funcQueryRepository.get(funcId)
    }

    private fun ensureCodeExists(codeId: CodeId, codeVersion: CodeVersion) {
        codeQueryRepository.get(codeId, codeVersion)
    }
}