package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.FuncCreatedEvent
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
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
    fun create(reqId: ReqId, funcToCreate: FuncToCreate): Func =
        createFunc(reqId, funcToCreate).also { emitEvent(reqId, it) }

    data class FuncToCreate(
        val shard: Shard,
        val name: FuncName,
        val code: Code
    )
}

private fun FuncCmdService.createFunc(reqId: ReqId, funcToCreate: FuncCmdService.FuncToCreate): Func {
    return funcCmdRepository.request(reqId) {
        createFunc {
            name = funcToCreate.name
            code = funcToCreate.code
        }
    }.first()
}

private fun FuncCmdService.emitEvent(reqId: ReqId, func: Func) {
    eventEmitter.emit(
        reqId, FuncCreatedEvent(
//            reqId = ReqId(123), // FIXME
            shard = func.shard,
            funcId = func.id
        )
    )
}
