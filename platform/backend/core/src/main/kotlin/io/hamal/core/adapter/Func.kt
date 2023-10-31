package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import io.hamal.repository.api.submitted_req.FuncCreateSubmitted
import io.hamal.repository.api.submitted_req.FuncDeploySubmitted
import io.hamal.repository.api.submitted_req.FuncUpdateSubmitted
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.UpdateFuncReq
import org.springframework.stereotype.Component

interface FuncCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateFuncReq,
        responseHandler: (FuncCreateSubmitted) -> T
    ): T
}

interface FuncGetPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Namespace) -> T): T
}

interface FuncInvokePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T
}

interface FuncListPort {
    operator fun <T : Any> invoke(query: FuncQuery, responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T): T
}

interface FuncDeployPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        versionToDeploy: CodeVersion,
        responseHandler: (FuncDeploySubmitted) -> T
    ): T
}

interface FuncUpdatePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: UpdateFuncReq,
        responseHandler: (FuncUpdateSubmitted) -> T
    ): T

}

interface FuncPort : FuncCreatePort, FuncDeployPort, FuncGetPort, FuncInvokePort, FuncListPort, FuncUpdatePort

@Component
class FuncAdapter(
    private val submitRequest: SubmitRequest,
    private val funcQueryRepository: FuncQueryRepository,
    private val codeQueryRepository: CodeQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : FuncPort {
    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateFuncReq,
        responseHandler: (FuncCreateSubmitted) -> T
    ): T {
        return responseHandler(submitRequest(namespaceId, req))
    }

    override fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Namespace) -> T): T {
        val func = funcQueryRepository.get(funcId)
        val code = codeQueryRepository.get(func.code.id)
        val namespaces = namespaceQueryRepository.get(func.namespaceId)
        return responseHandler(func, code, namespaces)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T {
        return responseHandler(submitRequest(funcId, req))
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
        req: UpdateFuncReq,
        responseHandler: (FuncUpdateSubmitted) -> T
    ): T {
        ensureFuncExists(funcId)
        return responseHandler(submitRequest(funcId, req))
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        versionToDeploy: CodeVersion,
        responseHandler: (FuncDeploySubmitted) -> T
    ): T {
        return responseHandler(submitRequest(funcId, versionToDeploy))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}