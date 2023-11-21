package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.sqlite.SqliteAuthRepository
import io.hamal.repository.sqlite.SqliteStateRepository
import io.hamal.repository.sqlite.log.*
import io.hamal.repository.sqlite.record.account.SqliteAccountRepository
import io.hamal.repository.sqlite.record.blueprint.SqliteBlueprintRepository
import io.hamal.repository.sqlite.record.code.SqliteCodeRepository
import io.hamal.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.repository.sqlite.record.extension.SqliteExtensionRepository
import io.hamal.repository.sqlite.record.flow.SqliteFlowRepository
import io.hamal.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.repository.sqlite.record.group.SqliteGroupRepository
import io.hamal.repository.sqlite.record.hook.SqliteHookRepository
import io.hamal.repository.sqlite.record.trigger.SqliteTriggerRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> SqliteAccountRepository(
            SqliteAccountRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_account_test"))
        ) as REPO

        AuthRepository::class -> SqliteAuthRepository(
            SqliteAuthRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_auth_test"))
        ) as REPO

        BlueprintRepository::class -> SqliteBlueprintRepository(
            SqliteBlueprintRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_blueprint_test"))
        ) as REPO

        BrokerConsumersRepository::class -> SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(createTempDirectory("/tmp/hamal_test/sqlite_broker_consumers_test"))
        ) as REPO

        BrokerRepository::class -> SqliteBrokerRepository(
            SqliteBroker(createTempDirectory("/tmp/hamal_test/sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("/tmp/hamal_test/sqlite_broker_topics_test"))
        ) as REPO

        CodeRepository::class -> SqliteCodeRepository(
            SqliteCodeRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_code_test"))
        ) as REPO

        ExecRepository::class -> SqliteExecRepository(
            SqliteExecRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_exec_test"))
        ) as REPO

        ExtensionRepository::class -> SqliteExtensionRepository(
            SqliteExtensionRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_extension_test"))
        ) as REPO

        FuncRepository::class -> SqliteFuncRepository(
            SqliteFuncRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_func_test"))
        ) as REPO

        GroupRepository::class -> SqliteGroupRepository(
            SqliteGroupRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_group_test"))
        ) as REPO

        HookRepository::class -> SqliteHookRepository(
            SqliteHookRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_hook_test"))
        ) as REPO

        FlowRepository::class -> SqliteFlowRepository(
            SqliteFlowRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_flow_test"))
        ) as REPO

        SegmentRepository::class -> SqliteSegmentRepository(
            SqliteSegment(
                Segment.Id(2810),
                TopicId(1506),
                createTempDirectory("/tmp/hamal_test/sqlite_topic_test")
            )
        ) as REPO

        StateRepository::class -> SqliteStateRepository(
            SqliteStateRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_state_test"))
        ) as REPO

        TopicRepository::class -> SqliteTopicRepository(
            Topic(TopicId(23), FlowId(23), GroupId(1), TopicName("test-topic")),
            createTempDirectory("/tmp/hamal_test/sqlite_topic_test")
        ) as REPO

        TriggerRepository::class -> SqliteTriggerRepository(
            SqliteTriggerRepository.Config(createTempDirectory("/tmp/hamal_test/sqlite_trigger_test"))
        ) as REPO

        else -> TODO()
    }
}