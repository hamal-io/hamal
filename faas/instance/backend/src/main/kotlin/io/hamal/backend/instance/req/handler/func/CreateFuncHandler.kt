package io.hamal.backend.instance.req.handler.func

import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.event.FuncCreatedEvent
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedCreateFuncReq
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceName
import org.springframework.stereotype.Component

@Component
class CreateFuncHandler(
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: InstanceEventEmitter,
    val namespaceQueryRepository: NamespaceQueryRepository
) : ReqHandler<SubmittedCreateFuncReq>(SubmittedCreateFuncReq::class) {
    override fun invoke(req: SubmittedCreateFuncReq) {
        createFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateFuncHandler.createFunc(req: SubmittedCreateFuncReq): Func {
    return funcCmdRepository.create(
        FuncCmdRepository.CreateCmd(
            id = req.cmdId(),
            funcId = req.id,
            namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        )
    )
}

private fun CreateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncCreatedEvent(func))
}
