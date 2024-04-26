package io.hamal.core.config.repo

import io.hamal.core.config.BackendBasePath
import io.hamal.repository.api.*
import io.hamal.repository.sqlite.AuthSqliteRepository
import io.hamal.repository.sqlite.ExecLogSqliteRepository
import io.hamal.repository.sqlite.RequestSqliteRepository
import io.hamal.repository.sqlite.StateSqliteRepository
import io.hamal.repository.sqlite.log.LogBrokerSqliteRepository
import io.hamal.repository.sqlite.record.account.AccountSqliteRepository
import io.hamal.repository.sqlite.record.code.CodeSqliteRepository
import io.hamal.repository.sqlite.record.exec.ExecSqliteRepository
import io.hamal.repository.sqlite.record.extension.ExtensionSqliteRepository
import io.hamal.repository.sqlite.record.feedback.FeedbackSqliteRepository
import io.hamal.repository.sqlite.record.func.FuncSqliteRepository
import io.hamal.repository.sqlite.record.namespace.NamespaceSqliteRepository
import io.hamal.repository.sqlite.record.namespace_tree.NamespaceTreeSqliteRepository
import io.hamal.repository.sqlite.record.recipe.RecipeSqliteRepository
import io.hamal.repository.sqlite.record.topic.TopicSqliteRepository
import io.hamal.repository.sqlite.record.trigger.TriggerSqliteRepository
import io.hamal.repository.sqlite.record.workspace.WorkspaceSqliteRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Configuration
@Profile("sqlite")
open class SqliteRepositoryConfig(backendBasePath: BackendBasePath) {

    @Bean
    open fun accountRepository() = AccountSqliteRepository(path)

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = AuthSqliteRepository(path)

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun codeRepository() = CodeSqliteRepository(path)

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun extensionRepository() = ExtensionSqliteRepository(path)

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun feedbackRepository() = FeedbackSqliteRepository(path)

    @Bean
    open fun feedbackCmdRepository() = feedbackRepository()

    @Bean
    open fun feedbackQueryRepository() = feedbackRepository()

    @Bean
    open fun funcRepository() = FuncSqliteRepository(path)

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun workspaceRepository() = WorkspaceSqliteRepository(path)

    @Bean
    open fun workspaceQueryRepository() = workspaceRepository()

    @Bean
    open fun workspaceCmdRepository() = workspaceRepository()

    @Bean
    open fun namespaceRepository() = NamespaceSqliteRepository(path)

    @Bean
    open fun namespaceCmdRepository(): NamespaceCmdRepository = namespaceRepository()

    @Bean
    open fun namespaceQueryRepository(): NamespaceQueryRepository = namespaceRepository()

    @Bean
    open fun namespaceTreeRepository() = NamespaceTreeSqliteRepository(path)

    @Bean
    open fun namespaceTreeCmdRepository() = namespaceTreeRepository()

    @Bean
    open fun namespaceTreeQueryRepository() = namespaceTreeRepository()

    @Bean
    open fun execRepository() = ExecSqliteRepository(path)

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = execRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = execRepository()

    @Bean
    open fun execLogRepository(): ExecLogRepository = ExecLogSqliteRepository(path)

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = execLogRepository()

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = execLogRepository()

    @Bean
    open fun logBrokerRepository() = LogBrokerSqliteRepository(path)

    @Bean
    open fun recipeRepository() = RecipeSqliteRepository(path)

    @Bean
    open fun recipeCmdRepository(): RecipeCmdRepository = recipeRepository()

    @Bean
    open fun recipeQueryRepository(): RecipeQueryRepository = recipeRepository()

    @Bean
    open fun requestRepository(): RequestRepository = RequestSqliteRepository(path)

    @Bean
    open fun requestCmdRepository(): RequestCmdRepository = requestRepository()

    @Bean
    open fun requestQueryRepository(): RequestQueryRepository = requestRepository()

    @Bean
    open fun stateRepository() = StateSqliteRepository(path)

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun topicRepository(): TopicRepository = TopicSqliteRepository(
        path = path,
        logBrokerRepository = logBrokerRepository(),
    )

    @Bean
    open fun topicCmdRepository(): TopicCmdRepository = topicRepository()

    @Bean
    open fun topicQueryRepository(): TopicQueryRepository = topicRepository()

    @Bean
    open fun triggerRepository() = TriggerSqliteRepository(path)

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

    private val path = Path(backendBasePath.value)
}