package io.hamal.backend.cmd

import io.hamal.backend.event.FuncCreatedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuncCmdService
@Autowired constructor(
    val funcRequestRepository: FuncRequestRepository,
    val eventEmitter: EventEmitter
) {

    fun create(funcToCreate: FuncToCreate): Func = createFunc(funcToCreate).also(this::emitEvent)


    data class FuncToCreate(
        val reqId: ReqId,
        val shard: Shard,
        val name: FuncName,
        val code: Code
    )
}

internal fun FuncCmdService.createFunc(funcToCreate: FuncCmdService.FuncToCreate): Func {
    return funcRequestRepository.request(funcToCreate.reqId) {
        createFunc {
            name = funcToCreate.name
            code = funcToCreate.code
        }
    }.first()
}

internal fun FuncCmdService.emitEvent(func: Func) {
    eventEmitter.emit(
        FuncCreatedEvent(
            shard = func.shard,
            id = func.id
        )
    )
}
