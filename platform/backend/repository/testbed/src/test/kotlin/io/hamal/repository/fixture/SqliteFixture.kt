package io.hamal.repository.fixture

import io.hamal.lib.common.domain.CreatedAt
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.domain.vo.LogTopicId.Companion.LogTopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogSegmentId.Companion.LogSegmentId
import io.hamal.repository.api.log.LogSegmentRepository
import io.hamal.repository.api.log.LogTopic
import io.hamal.repository.api.log.LogTopicRepository
import io.hamal.repository.sqlite.AuthSqliteRepository
import io.hamal.repository.sqlite.ExecLogSqliteRepository
import io.hamal.repository.sqlite.RequestSqliteRepository
import io.hamal.repository.sqlite.StateSqliteRepository
import io.hamal.repository.sqlite.log.LogBrokerSqliteRepository
import io.hamal.repository.sqlite.log.LogSegmentSqlite
import io.hamal.repository.sqlite.log.LogSegmentSqliteRepository
import io.hamal.repository.sqlite.log.LogTopicSqliteRepository
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
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> AccountSqliteRepository(createTempDirectory("sqlite_account_test")) as REPO
        AuthRepository::class -> AuthSqliteRepository(createTempDirectory("sqlite_auth_test")) as REPO
        RecipeRepository::class -> RecipeSqliteRepository(createTempDirectory("sqlite_recipe_test")) as REPO
        CodeRepository::class -> CodeSqliteRepository(createTempDirectory("sqlite_code_test")) as REPO
        ExecRepository::class -> ExecSqliteRepository(createTempDirectory("sqlite_exec_test")) as REPO
        ExecLogRepository::class -> ExecLogSqliteRepository(createTempDirectory("sqlite_exec_log_test")) as REPO
        ExtensionRepository::class -> ExtensionSqliteRepository(createTempDirectory("sqlite_extension_test")) as REPO
        FeedbackRepository::class -> FeedbackSqliteRepository(createTempDirectory("sqlite_feedback_test")) as REPO
        FuncRepository::class -> FuncSqliteRepository(createTempDirectory("sqlite_func_test")) as REPO
        WorkspaceRepository::class -> WorkspaceSqliteRepository(createTempDirectory("sqlite_workspace_test")) as REPO
        NamespaceRepository::class -> NamespaceSqliteRepository(createTempDirectory("sqlite_namespace_test")) as REPO
        NamespaceTreeRepository::class -> NamespaceTreeSqliteRepository(createTempDirectory("sqlite_namespace_tree_test")) as REPO
        StateRepository::class -> StateSqliteRepository(createTempDirectory("sqlite_state_test")) as REPO
        LogBrokerRepository::class -> LogBrokerSqliteRepository(createTempDirectory("sqlite_log_broker_test")) as REPO
        LogSegmentRepository::class -> LogSegmentSqliteRepository(
            LogSegmentSqlite(
                LogSegmentId(2810),
                LogTopicId(1506),
                createTempDirectory("sqlite_log_segment_test")
            )
        ) as REPO

        LogTopicRepository::class -> LogTopicSqliteRepository(
            LogTopic(LogTopicId(23), CreatedAt.now(), UpdatedAt.now()),
            createTempDirectory("sqlite_log_topic_test")
        ) as REPO

        RequestRepository::class -> RequestSqliteRepository(createTempDirectory("sqlite_req_test")) as REPO
        TopicRepository::class -> TopicSqliteRepository(
            createTempDirectory("sqlite_topic_test"),
            LogBrokerSqliteRepository(createTempDirectory("sqlite_log_broker_test"))
        ) as REPO

        TriggerRepository::class -> TriggerSqliteRepository(createTempDirectory("sqlite_trigger_test")) as REPO

        else -> TODO()
    }
}