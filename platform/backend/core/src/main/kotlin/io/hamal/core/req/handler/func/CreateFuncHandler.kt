package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.FuncCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedCreateFuncReq
import org.springframework.stereotype.Component

@Component
class CreateFuncHandler(
    val codeCmdRepository: CodeCmdRepository,
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: PlatformEventEmitter,
    val namespaceQueryRepository: NamespaceQueryRepository
) : ReqHandler<SubmittedCreateFuncReq>(SubmittedCreateFuncReq::class) {
    override fun invoke(req: SubmittedCreateFuncReq) {
        createFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateFuncHandler.createFunc(req: SubmittedCreateFuncReq): Func {
    val code = codeCmdRepository.create(
        CodeCmdRepository.CreateCmd(
            id = req.cmdId(),
            codeId = req.codeId,
            groupId = req.groupId,
            value = req.code

        )
    )
    return funcCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            funcId = req.id,
            groupId = req.groupId,
            namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
            name = req.name,
            inputs = req.inputs,
            codeId = code.id,
            codeVersion = code.version
        )
    )
}

private fun CreateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncCreatedEvent(func))
}
