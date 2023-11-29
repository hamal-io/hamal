package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.sqlite.AuthSqliteRepository
import io.hamal.repository.sqlite.StateSqliteRepository
import io.hamal.repository.sqlite.log.*
import io.hamal.repository.sqlite.record.account.AccountSqliteRepository
import io.hamal.repository.sqlite.record.blueprint.BlueprintSqliteRepository
import io.hamal.repository.sqlite.record.code.CodeSqliteRepository
import io.hamal.repository.sqlite.record.endpoint.EndpointSqliteRepository
import io.hamal.repository.sqlite.record.exec.ExecSqliteRepository
import io.hamal.repository.sqlite.record.extension.ExtensionSqliteRepository
import io.hamal.repository.sqlite.record.flow.FlowSqliteRepository
import io.hamal.repository.sqlite.record.func.FuncSqliteRepository
import io.hamal.repository.sqlite.record.group.GroupSqliteRepository
import io.hamal.repository.sqlite.record.hook.HookSqliteRepository
import io.hamal.repository.sqlite.record.trigger.TriggerSqliteRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> AccountSqliteRepository(
            AccountSqliteRepository.Config(createTempDirectory("sqlite_account_test"))
        ) as REPO

        AuthRepository::class -> AuthSqliteRepository(
            AuthSqliteRepository.Config(createTempDirectory("sqlite_auth_test"))
        ) as REPO

        BlueprintRepository::class -> BlueprintSqliteRepository(
            BlueprintSqliteRepository.Config(createTempDirectory("sqlite_blueprint_test"))
        ) as REPO

        BrokerConsumersRepository::class -> BrokerConsumersSqliteRepository(
            BrokerConsumersSqlite(createTempDirectory("sqlite_broker_consumers_test"))
        ) as REPO

        BrokerRepository::class -> BrokerSqliteRepository(
            BrokerSqlite(createTempDirectory("sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> BrokerTopicsSqliteRepository(
            BrokerTopicsSqlite(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        CodeRepository::class -> CodeSqliteRepository(
            CodeSqliteRepository.Config(createTempDirectory("sqlite_code_test"))
        ) as REPO

        EndpointRepository::class -> EndpointSqliteRepository(
            EndpointSqliteRepository.Config(createTempDirectory("sqlite_endpoint_test"))
        ) as REPO

        ExecRepository::class -> ExecSqliteRepository(
            ExecSqliteRepository.Config(createTempDirectory("sqlite_exec_test"))
        ) as REPO

        ExtensionRepository::class -> ExtensionSqliteRepository(
            ExtensionSqliteRepository.Config(createTempDirectory("sqlite_extension_test"))
        ) as REPO

        FuncRepository::class -> FuncSqliteRepository(
            FuncSqliteRepository.Config(createTempDirectory("sqlite_func_test"))
        ) as REPO

        GroupRepository::class -> GroupSqliteRepository(
            GroupSqliteRepository.Config(createTempDirectory("sqlite_group_test"))
        ) as REPO

        HookRepository::class -> HookSqliteRepository(
            HookSqliteRepository.Config(createTempDirectory("sqlite_hook_test"))
        ) as REPO

        FlowRepository::class -> FlowSqliteRepository(
            FlowSqliteRepository.Config(createTempDirectory("sqlite_flow_test"))
        ) as REPO

        SegmentRepository::class -> SegmentSqliteRepository(
            SegmentSqlite(
                Segment.Id(2810),
                TopicId(1506),
                createTempDirectory("sqlite_topic_test")
            )
        ) as REPO

        StateRepository::class -> StateSqliteRepository(
            StateSqliteRepository.Config(createTempDirectory("sqlite_state_test"))
        ) as REPO

        TopicRepository::class -> TopicSqliteRepository(
            Topic(TopicId(23), FlowId(23), GroupId(1), TopicName("test-topic")),
            createTempDirectory("sqlite_topic_test")
        ) as REPO

        TriggerRepository::class -> TriggerSqliteRepository(
            TriggerSqliteRepository.Config(createTempDirectory("sqlite_trigger_test"))
        ) as REPO

        else -> TODO()
    }
}