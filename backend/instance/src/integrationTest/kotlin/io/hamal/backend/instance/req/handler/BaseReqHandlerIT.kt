package io.hamal.backend.instance.req.handler

import io.hamal.backend.instance.BaseIT
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.memory.log.MemoryLogTopic
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*
import java.util.concurrent.atomic.AtomicInteger

internal object NextCommandId {
    operator fun invoke(): CmdId {
        return CmdId(counter.incrementAndGet())
    }

    private val counter = AtomicInteger(0)
}

internal abstract class BaseReqHandlerIT : BaseIT() {

    fun createFunc(id: FuncId, name: FuncName): Func {
        return funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = id,
                name = name,
                inputs = FuncInputs(),
                secrets = FuncSecrets(),
                code = Code("")
            )
        )
    }

    fun createTopic(id: TopicId, name: TopicName): MemoryLogTopic {
        return eventBrokerRepository.create(
            NextCommandId(), CreateTopic.TopicToCreate(
                id = id,
                name = name
            )
        )
    }
}