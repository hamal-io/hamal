package io.hamal.core.req.handler

import io.hamal.core.BaseTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.FuncCode
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
        namespaceId: NamespaceId = generateDomainId(::NamespaceId),
        inputs: FuncInputs = FuncInputs(),
        codeId: CodeId = CodeId(2222),
        codeVersion: CodeVersion = CodeVersion(2233),
    ): Func {
        return funcCmdRepository.create(
            CreateCmd(
                id = NextCommandId(),
                funcId = id,
                groupId = testGroup.id,
                namespaceId = namespaceId,
                name = name,
                inputs = inputs,
                code = FuncCode(
                    id = codeId,
                    version = codeVersion
                )
            )
        )
    }

    fun createTopic(id: TopicId, name: TopicName): Topic {
        return eventBrokerRepository.create(
            NextCommandId(), TopicToCreate(
                id = id,
                name = name,
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