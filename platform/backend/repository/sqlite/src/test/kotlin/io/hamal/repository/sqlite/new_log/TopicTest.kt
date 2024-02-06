package io.hamal.repository.sqlite.log

import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.sqlite.new_log.LogTopicSqlite
import io.hamal.repository.sqlite.new_log.LogTopicSqliteRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.pathString


internal class LogTopicSqliteRepositoryTest {

    @Nested
    inner class ConstructorTest {
        @BeforeEach
        fun setup() {
            FileUtils.delete(Path(testDir))
            FileUtils.createDirectories(Path(testDir))
        }

        @Test
        fun `Creates a directory if path does not exists yet and populates with a partition`() {
            val targetDir = Path(testDir, "another-path", "more-nesting")

            LogTopicSqliteRepository(
                LogTopicSqlite(
                    id = LogTopicId(23),
                    path = targetDir
                ),
            ).use { }

            assertTrue(FileUtils.exists(targetDir))
            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topics/00000023")))
        }
    }

    private val testDir = "/tmp/hamal/test/partitions"
}