package io.hamal.core.req.handler

import io.hamal.core.BaseTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.Topic
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
        codeId: CodeId = CodeId(2222),
        codeVersion: CodeVersion = CodeVersion(2233),
    ): Func {
        return funcCmdRepository.create(
            CreateCmd(
                id = NextCommandId(),
                funcId = id,
                namespaceId = testNamespace.id,
                groupId = testGroup.id,
                name = name,
                inputs = inputs,
                codeId = codeId,
                codeVersion = codeVersion
            )
        )
    }

    fun createTopic(id: TopicId, name: TopicName): Topic {
        return eventBrokerRepository.create(
            NextCommandId(), TopicToCreate(
                id = id,
                name = name,
                namespaceId = testNamespace.id,
                groupId = testGroup.id
            )
        )
    }

    fun createHook(
        id: HookId,
        name: HookName = HookName("SomeName"),
        namespaceId: NamespaceId = generateDomainId(::NamespaceId),
    ): Hook {
        return hookRepository.create(
            HookCmdRepository.CreateCmd(
                id = NextCommandId(),
                groupId = testGroup.id,
                hookId = id,
                name = name,
                namespaceId = namespaceId,
            )
        )
    }


}