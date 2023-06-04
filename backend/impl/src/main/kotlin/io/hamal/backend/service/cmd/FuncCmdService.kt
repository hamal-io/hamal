package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.FuncCreatedEvent
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuncCmdService
@Autowired constructor(
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: EventEmitter
) {
    fun create(cmdId: CmdId, funcToCreate: FuncToCreate): Func =
        createFunc(cmdId, funcToCreate).also { emitEvent(cmdId, it) }

    data class FuncToCreate(
        val shard: Shard,
        val name: FuncName,
        val code: Code
    )
}

private fun FuncCmdService.createFunc(cmdId: CmdId, funcToCreate: FuncCmdService.FuncToCreate): Func {
    return funcCmdRepository.request(cmdId) {
        createFunc {
            name = funcToCreate.name
            code = funcToCreate.code
        }
    }.first()
}

private fun FuncCmdService.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(
        cmdId, FuncCreatedEvent(
//            cmdId = CommandId(123), // FIXME
            shard = func.shard,
            funcId = func.id
        )
    )
}
