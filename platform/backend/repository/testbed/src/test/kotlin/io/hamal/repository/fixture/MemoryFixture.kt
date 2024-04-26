package io.hamal.repository.fixture

import io.hamal.lib.common.domain.CreatedAt
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.memory.AuthMemoryRepository
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.RequestMemoryRepository
import io.hamal.repository.memory.StateMemoryRepository
import io.hamal.repository.memory.log.LogBrokerMemoryRepository
import io.hamal.repository.memory.log.LogSegmentMemory
import io.hamal.repository.memory.log.LogSegmentMemoryRepository
import io.hamal.repository.memory.log.LogTopicMemoryRepository
import io.hamal.repository.memory.record.account.AccountMemoryRepository
import io.hamal.repository.memory.record.code.CodeMemoryRepository
import io.hamal.repository.memory.record.exec.ExecMemoryRepository
import io.hamal.repository.memory.record.extension.ExtensionMemoryRepository
import io.hamal.repository.memory.record.feedback.FeedbackMemoryRepository
import io.hamal.repository.memory.record.func.FuncMemoryRepository
import io.hamal.repository.memory.record.namespace.NamespaceMemoryRepository
import io.hamal.repository.memory.record.namespace_tree.NamespaceTreeMemoryRepository
import io.hamal.repository.memory.record.recipe.RecipeMemoryRepository
import io.hamal.repository.memory.record.topic.TopicMemoryRepository
import io.hamal.repository.memory.record.trigger.TriggerMemoryRepository
import io.hamal.repository.memory.record.workspace.MemoryWorkspaceRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> AccountMemoryRepository() as REPO
        AuthRepository::class -> AuthMemoryRepository() as REPO
        RecipeRepository::class -> RecipeMemoryRepository() as REPO
        CodeRepository::class -> CodeMemoryRepository() as REPO
        ExecLogRepository::class -> ExecLogMemoryRepository() as REPO
        ExecRepository::class -> ExecMemoryRepository() as REPO
        ExtensionRepository::class -> ExtensionMemoryRepository() as REPO
        FeedbackRepository::class -> FeedbackMemoryRepository() as REPO
        FuncRepository::class -> FuncMemoryRepository() as REPO
        WorkspaceRepository::class -> MemoryWorkspaceRepository() as REPO
        NamespaceRepository::class -> NamespaceMemoryRepository() as REPO
        NamespaceTreeRepository::class -> NamespaceTreeMemoryRepository() as REPO
        RequestRepository::class -> RequestMemoryRepository() as REPO

        LogBrokerRepository::class -> LogBrokerMemoryRepository() as REPO
        LogSegmentRepository::class -> LogSegmentMemoryRepository(
            LogSegmentMemory(
                LogSegmentId(2810),
                LogTopicId(1506)
            )
        ) as REPO

        LogTopicRepository::class -> LogTopicMemoryRepository(
            LogTopic(LogTopicId(23), CreatedAt.now(), UpdatedAt.now())
        ) as REPO

        StateRepository::class -> StateMemoryRepository() as REPO
        TopicRepository::class -> TopicMemoryRepository(LogBrokerMemoryRepository()) as REPO
        TriggerRepository::class -> TriggerMemoryRepository() as REPO
        else -> TODO()
    }
}