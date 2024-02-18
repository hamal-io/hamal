//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.domain.CmdId
//import io.hamal.lib.domain.vo.NamespaceId
//import io.hamal.lib.domain.vo.WorkspaceId
//import io.hamal.lib.domain.vo.TopicId
//import io.hamal.lib.domain.vo.TopicName
//import io.hamal.repository.api.log.AppenderImpl
//import io.hamal.repository.api.log.ConsumerId
//import io.hamal.repository.api.log.CreateTopic.TopicToCreate
//import io.hamal.repository.api.log.LogConsumerImpl
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Test
//import java.nio.file.Files
//
//class ConsumerTest {
//    @Test
//    fun `Best effort to consume chunk once`() {
//        val path = Files.createTempDirectory("broker_it")
//
//        BrokerSqliteRepository(BrokerSqlite(path))
//            .use { brokerRepository ->
//                val topic = brokerRepository.append(
//                    CmdId(1),
//                    TopicToCreate(TopicId(123), TopicName("topic"), NamespaceId(1), WorkspaceId.root)
//                )
//                val appender = AppenderImpl<String>(brokerRepository)
//                IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
//            }
//
//        BrokerSqliteRepository(BrokerSqlite(path))
//            .use { brokerRepository ->
//                val topic = brokerRepository.findTopic(NamespaceId(1), TopicName("topic"))!!
//                val testInstance =
//                    LogConsumerImpl(ConsumerId("consumer-01"), topic, brokerRepository, String::class)
//                testInstance.consumeIndexed(10) { index, _, value ->
//                    assertThat("${index + 1}", equalTo(value))
//                }
//            }
//    }
//}