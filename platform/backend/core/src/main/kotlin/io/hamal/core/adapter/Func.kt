package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.UpdateFuncReq
import org.springframework.stereotype.Component

interface CreateFuncPort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateFuncReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetFuncPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Namespace) -> T): T
}

interface InvokeFuncPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface ListFuncsPort {
    operator fun <T : Any> invoke(query: FuncQuery, responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T): T
}

interface UpdateFuncPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: UpdateFuncReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface FuncPort : CreateFuncPort, GetFuncPort, InvokeFuncPort, ListFuncsPort, UpdateFuncPort

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
        responseHandler: (SubmittedReqWithGroupId) -> T
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
        responseHandler: (SubmittedReqWithGroupId) -> T
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
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        ensureFuncExists(funcId)
        return responseHandler(submitRequest(funcId, req))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}