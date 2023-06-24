package io.hamal.backend.instance.service.cmd

import io.hamal.backend.instance.component.SystemEventEmitter
import io.hamal.backend.instance.event.FuncCreatedEvent
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuncCmdService
@Autowired constructor(
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: SystemEventEmitter<*>
) {
    fun create(cmdId: CmdId, toCreate: ToCreate): Func =
        createFunc(cmdId, toCreate).also { emitEvent(cmdId, it) }

    data class ToCreate(
        val funcId: FuncId,
        val name: FuncName,
        val inputs: FuncInputs,
        val secrets: FuncSecrets,
        val code: Code
    )
}

private fun FuncCmdService.createFunc(cmdId: CmdId, toCreate: FuncCmdService.ToCreate): Func {
    return funcCmdRepository.create(
        FuncCmdRepository.CreateCmd(
            id = cmdId,
            funcId = toCreate.funcId,
            name = toCreate.name,
            inputs = toCreate.inputs,
            secrets = toCreate.secrets,
            code = toCreate.code
        )
    )
}

private fun FuncCmdService.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(
        cmdId, FuncCreatedEvent(
            funcId = func.id
        )
    )
}
