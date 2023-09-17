package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.UpdateFuncReq
import org.springframework.stereotype.Component

interface CreateFuncPort {
    operator fun <T : Any> invoke(groupId: GroupId, req: CreateFuncReq, responseHandler: (SubmittedReq) -> T): T
}

interface GetFuncPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Namespace) -> T): T
}

interface InvokeFuncPort {
    operator fun <T : Any> invoke(funcId: FuncId, req: InvokeFuncReq, responseHandler: (SubmittedReq) -> T): T
}

interface ListFuncPort {
    operator fun <T : Any> invoke(query: FuncQuery, responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T): T
}

interface UpdateFuncPort {
    operator fun <T : Any> invoke(funcId: FuncId, req: UpdateFuncReq, responseHandler: (SubmittedReq) -> T): T
}

interface FuncPort : CreateFuncPort, GetFuncPort, InvokeFuncPort, ListFuncPort, UpdateFuncPort

@Component
class FuncAdapter(
    private val submitRequest: SubmitRequest,
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : FuncPort {
    override fun <T : Any> invoke(groupId: GroupId, req: CreateFuncReq, responseHandler: (SubmittedReq) -> T): T {
        ensureNamespaceIdExists(req.namespaceId)
        return responseHandler(submitRequest(groupId, req))
    }

    override fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Namespace) -> T): T {
        val func = funcQueryRepository.get(funcId)
        val namespaces = namespaceQueryRepository.get(func.namespaceId)
        return responseHandler(func, namespaces)
    }

    override fun <T : Any> invoke(funcId: FuncId, req: InvokeFuncReq, responseHandler: (SubmittedReq) -> T): T {
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

    override fun <T : Any> invoke(funcId: FuncId, req: UpdateFuncReq, responseHandler: (SubmittedReq) -> T): T {
        ensureFuncExists(funcId)
        ensureNamespaceIdExists(req.namespaceId)
        return responseHandler(submitRequest(funcId, req))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }

    private fun ensureNamespaceIdExists(namespaceId: NamespaceId?) {
        namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}