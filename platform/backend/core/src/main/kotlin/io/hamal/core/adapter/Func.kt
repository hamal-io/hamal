package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.submitted_req.*
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.InvokeFuncVersionReq
import io.hamal.request.UpdateFuncReq
import org.springframework.stereotype.Component

interface FuncCreatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: CreateFuncReq,
        responseHandler: (FuncCreateSubmitted) -> T
    ): T
}

interface FuncGetPort {
    operator fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Code, Flow) -> T): T
}

interface FuncInvokePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T

    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncVersionReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T
}

interface FuncListPort {
    operator fun <T : Any> invoke(query: FuncQuery, responseHandler: (List<Func>, Map<FlowId, Flow>) -> T): T
}

interface FuncDeployPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        versionToDeploy: CodeVersion,
        responseHandler: (FuncDeploySubmitted) -> T
    ): T

    operator fun <T : Any> invoke(
        funcId: FuncId,
        responseHandler: (FuncDeployLatestSubmitted) -> T
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
    private val codeQueryRepository: CodeQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : FuncPort {

    override fun <T : Any> invoke(
        flowId: FlowId,
        req: CreateFuncReq,
        responseHandler: (FuncCreateSubmitted) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return FuncCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = flow.groupId,
            funcId = generateDomainId(::FuncId),
            flowId = flowId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(funcId: FuncId, responseHandler: (Func, Code, Code, Flow) -> T): T {
        val func = funcQueryRepository.get(funcId)
        val current = codeQueryRepository.get(func.code.id, func.code.version)
        val deployed = codeQueryRepository.get(func.code.id, func.code.deployedVersion)
        val flows = flowQueryRepository.get(func.flowId)
        return responseHandler(func, current, deployed, flows)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)

        return ExecInvokeSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            flowId = func.flowId,
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = func.code.toExecCode(),
            events = listOf()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncVersionReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)

        val version = req.version?.also {
            codeQueryRepository.get(func.code.id, it)
        } ?: func.code.version

        return ExecInvokeSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            flowId = func.flowId,
            groupId = func.groupId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = ExecCode(
                id = func.code.id,
                version = version,
                value = null
            ),
            events = listOf()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        query: FuncQuery,
        responseHandler: (List<Func>, Map<FlowId, Flow>) -> T
    ): T {
        val funcs = funcQueryRepository.list(query)
        val flows = flowQueryRepository.list(funcs.map(Func::flowId))
            .associateBy(Flow::id)
        return responseHandler(funcs, flows)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        req: UpdateFuncReq,
        responseHandler: (FuncUpdateSubmitted) -> T
    ): T {
        ensureFuncExists(funcId)
        val func = funcQueryRepository.get(funcId)
        return FuncUpdateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = func.groupId,
            funcId = funcId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        funcId: FuncId,
        versionToDeploy: CodeVersion,
        responseHandler: (FuncDeploySubmitted) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)
        val code = codeQueryRepository.get(func.code.id, versionToDeploy)
        return FuncDeploySubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = func.groupId,
            funcId = funcId,
            versionToDeploy = code.version
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(funcId: FuncId, responseHandler: (FuncDeployLatestSubmitted) -> T): T {
        val func = funcQueryRepository.get(funcId)
        return FuncDeployLatestSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = func.groupId,
            funcId = funcId,
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}