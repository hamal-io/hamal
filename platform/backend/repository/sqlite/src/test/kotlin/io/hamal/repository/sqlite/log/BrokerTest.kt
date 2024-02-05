//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.domain.CmdId
//import io.hamal.lib.common.util.FileUtils
//import io.hamal.lib.domain.vo.FlowId
//import io.hamal.lib.domain.vo.GroupId
//import io.hamal.lib.domain.vo.TopicId
//import io.hamal.lib.domain.vo.TopicName
//import io.hamal.repository.api.log.CreateTopic.TopicToCreate
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import java.nio.file.Files
//import java.nio.file.Path
//import kotlin.io.path.Path
//import kotlin.io.path.pathString
//
//
//internal class LogBrokerSqliteRepositoryTest {
//
////    @Nested
////    inner class NextEntryIdTest {
////        @Test
////        fun `Returns entry id 0 if no entry exists for consumer id and topic id`() {
////            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(42))
////            assertThat(result, equalTo(LogEntryId(0)))
////        }
////
////        @Test
////        fun `Next entry id - is last committed entry id plus 1`() {
////            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEntryId(127))
////
////            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(1))
////            assertThat(result, equalTo(LogEntryId(128)))
////        }
////
////        @Test
////        fun `Does not return next entry id of different topic`() {
////            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEntryId(127))
////
////            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(2))
////            assertThat(result, equalTo(LogEntryId(0)))
////        }
////
////        @Test
////        fun `Does not return next chunk id of different consumer`() {
////            testInstance.commit(LogConsumerId(42), LogTopicId(1), LogEntryId(127))
////
////            val result = testInstance.nextEntryId(LogConsumerId(1337), LogTopicId(1))
////            assertThat(result, equalTo(LogEntryId(0)))
////        }
////
////        private val testInstance = LogBrokerSqliteRepository()
////    }
//
//}
//
//class BrokerSqliteRepositoryTest {
//    @Nested
//    inner class ConstructorTest {
//
//        @Test
//        fun `Creates a directory if path does not exists yet`() {
//            val targetDir = Path(testDir, "some-path", "another-path")
//            BrokerSqliteRepository(testBroker(targetDir)).use { }
//
//            assertTrue(FileUtils.exists(targetDir))
//            assertTrue(FileUtils.exists(Path(targetDir.pathString, "topics.db")))
//            assertTrue(FileUtils.exists(Path(targetDir.pathString, "consumers.db")))
//        }
//
//        private fun testBroker(path: Path = Path(testDir)) = BrokerSqlite(path)
//
//        private val testDir = "/tmp/hamal/test/broker"
//    }
//
//    @Nested
//    inner class CreateTopicTest {
//        @Test
//        fun `Bug - Able to resolve real topic`() {
//            val testPath = Files.createTempDirectory("testDir")
//            val testInstance = BrokerSqliteRepository(BrokerSqlite(testPath))
//
//            val result = testInstance.create(
//                CmdId(123),
//                TopicToCreate(TopicId(234), TopicName("scheduler::flow_enqueued"), FlowId(1), GroupId(1))
//            )
//
//            assertThat(result.id, equalTo(TopicId(234)))
//            assertThat(result.name, equalTo(TopicName("scheduler::flow_enqueued")))
//        }
//    }
//
//}
