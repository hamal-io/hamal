package io.hamal.backend.instance.req.handler

import io.hamal.backend.instance.BaseTest
import io.hamal.backend.repository.api.Func
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
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
        namespaceId: NamespaceId = generateDomainId(::NamespaceId),
        inputs: FuncInputs = FuncInputs(),
        code: CodeType = CodeType(""),
    ): Func {
        return funcCmdRepository.create(
            FuncCmdRepository.CreateCmd(
                id = NextCommandId(),
                funcId = id,
                namespaceId = namespaceId,
                name = name,
                inputs = inputs,
                code = code
            )
        )
    }

    fun createTopic(id: TopicId, name: TopicName): LogTopic {
        return eventBrokerRepository.create(
            NextCommandId(), CreateTopic.TopicToCreate(
                id = id,
                name = name
            )
        )
    }
}