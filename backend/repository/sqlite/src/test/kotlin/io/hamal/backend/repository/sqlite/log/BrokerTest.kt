package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString


class LogBrokerRepositoryTest {
    @Nested
    inner class ConstructorTest {

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            SqliteLogBrokerRepository(testBroker(targetDir)).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topics.db")))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "consumers.db")))
        }

        private fun testBroker(path: Path = Path(testDir)) = SqliteLogBroker(
            id = LogBroker.Id(2810),
            path = path
        )

        private val testDir = "/tmp/hamal/test/broker"
    }

    @Nested
    inner class ResolveTopicTest {
        @Test
        fun `Bug - Able to resolve real topic`() {
            val testPath = java.nio.file.Files.createTempDirectory("testDir")
            val testInstance = SqliteLogBrokerRepository(SqliteLogBroker(LogBroker.Id(456), testPath))

            val result = testInstance.resolveTopic(TopicName("scheduler::flow_enqueued"))
            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(456)))
            assertThat(result.name, equalTo(TopicName("scheduler::flow_enqueued")))
            assertThat(result.path, equalTo(testPath))
        }
    }

}
