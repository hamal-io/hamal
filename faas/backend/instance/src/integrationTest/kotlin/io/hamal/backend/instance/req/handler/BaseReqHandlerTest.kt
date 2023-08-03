package io.hamal.backend.instance.req.handler

import io.hamal.backend.instance.BaseTest
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.memory.log.MemoryLogTopic
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import java.util.concurrent.atomic.AtomicInteger

internal object NextCommandId {
    operator fun invoke(): CmdId {
        return CmdId(counter.incrementAndGet())
    }

    private val counter = AtomicInteger(0)
}

internal abstract class BaseReqHandlerTest : BaseTest() {

    fun createFunc(
        id: FuncId = generateDomainId(::FuncId),
        name: FuncName = FuncName("SomeFuncName"),
        inputs: FuncInputs = FuncInputs(),
        code: CodeValue = CodeValue("")
    ): Func {
        return funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = id,
                name = name,
                inputs = inputs,
                code = code
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