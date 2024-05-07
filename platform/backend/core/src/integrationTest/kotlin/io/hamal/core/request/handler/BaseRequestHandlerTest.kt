package io.hamal.core.request.handler

import io.hamal.core.BaseTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.domain._enum.TopicTypes.Namespace
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository
import java.util.concurrent.atomic.AtomicInteger

internal object NextCommandId {
    operator fun invoke(): CmdId {
        return CmdId(counter.incrementAndGet())
    }

    private val counter = AtomicInteger(0)
}

internal abstract class BaseRequestHandlerTest : BaseTest() {

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
                workspaceId = testWorkspace.id,
                name = name,
                inputs = inputs,
                codeId = codeId,
                codeVersion = codeVersion
            )
        )
    }

    fun createTopic(id: TopicId, name: TopicName): Topic {
        return topicCmdRepository.create(
            TopicCmdRepository.TopicCreateCmd(
                id = NextCommandId(),
                topicId = id,
                name = name,
                workspaceId = testWorkspace.id,
                namespaceId = testNamespace.id,
                type = TopicType(Namespace),
                logTopicId = generateDomainId(::LogTopicId)
            )
        )
    }

}