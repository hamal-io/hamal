package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.vo.LogTopicId.Companion.LogTopicId
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.CreateTopicCmd
import io.hamal.repository.api.log.LogConsumerId.Companion.LogConsumerId
import io.hamal.repository.fixture.AbstractIntegrationTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.TestFactory
import java.util.concurrent.CompletableFuture.runAsync

class LogBrokerRepositoryTest : AbstractIntegrationTest() {

    @TestFactory
    fun `Concurrent safe - 10 threads add to the same topic`() =
        runWith(LogBrokerRepository::class) { testInstance ->
            val topic = testInstance.create(CreateTopicCmd(CmdId(1), LogTopicId(123)))

            val futures = IntRange(1, 10).map { thread ->
                runAsync {
                    IntRange(1, 1_000).forEach {
                        testInstance.append(generateCmdId(), topic.id, "$thread $it".toByteArray())
                    }
                }
            }

            futures.forEach { it.join() }

            val result = testInstance.consume(LogConsumerId(42), topic.id, Limit(100_000))
            assertThat(result, hasSize(10_000))
        }

    @TestFactory
    fun `Concurrent safe - 100 threads add to the different topics`() =
        runWith(LogBrokerRepository::class) { testInstance ->

            val futures = IntRange(1, 100).map { thread ->
                runAsync {
                    val topic = testInstance.create(CreateTopicCmd(CmdId(1), LogTopicId(thread)))

                    IntRange(1, 100).forEach {
                        testInstance.append(generateCmdId(), topic.id, "$thread $it".toByteArray())
                    }
                }
            }
            futures.forEach { it.join() }

            IntRange(1, 100).forEach { thread ->
                val result = testInstance.consume(
                    LogConsumerId(42),
                    testInstance.getTopic(LogTopicId(thread)).id,
                    Limit(1_000_000)
                )
                assertThat(result, hasSize(100))
                assertThat(result.first().bytes, equalTo("$thread 1".toByteArray()))
                assertThat(result.last().bytes, equalTo("$thread 100".toByteArray()))
            }

        }

    private val generateCmdId: GenerateCmdId = object : GenerateCmdId {
        override fun invoke() = CmdId(generator.next())
        private val generator = SnowflakeGenerator(PartitionSourceImpl(1))
    }
}