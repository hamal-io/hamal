package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.Topic
import io.hamal.lib.core.util.Files
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

@DisplayName("BrokerRepository")
class BrokerRepositoryTest {

    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {

        @Test
        fun `Creates a directory if path does not exists yet`() {
            val targetDir = Path(testDir, "some-path", "another-path")
            DefaultBrokerRepository(testBroker(targetDir)).use { }

            assertTrue(Files.exists(targetDir))
            assertTrue(Files.exists(Path(targetDir.pathString, "topics.db")))
            assertTrue(Files.exists(Path(targetDir.pathString, "consumers.db")))
        }

        private fun testBroker(path: Path = Path(testDir)) = Broker(
            id = Broker.Id(2810),
            path = path
        )

        private val testDir = "/tmp/hamal/test/broker"
    }

    @Nested
    @DisplayName("resolveTopic()")
    inner class ResolveTopicTest {
        @Test
        fun `Bug - Able to resolve real topic`() {
            val testPath = java.nio.file.Files.createTempDirectory("testDir")
            val testInstance = DefaultBrokerRepository(Broker(Broker.Id(456), testPath))

            val result = testInstance.resolveTopic(Topic.Name("scheduler::flow_enqueued"))
            assertThat(result.id, equalTo(Topic.Id(1)))
            assertThat(result.brokerId, equalTo(Broker.Id(456)))
            assertThat(result.name, equalTo(Topic.Name("scheduler::flow_enqueued")))
            assertThat(result.path, equalTo(testPath))
        }
    }

}
