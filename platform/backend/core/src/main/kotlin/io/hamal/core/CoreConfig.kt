package io.hamal.core

import io.hamal.core.adapter.AccountCreateRootPort
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.request.AccountCreateRootRequest
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.TopicCmdRepository.TopicInternalCreateCmd
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.event.internalEventClasses
import io.hamal.repository.api.event.topicName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(
    exclude = [
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class,
        JdbcTemplateAutoConfiguration::class,
        SpringApplicationAdminJmxAutoConfiguration::class,
        JmxAutoConfiguration::class
    ]
)
open class CoreConfig {

    @Bean
    @Profile("!test")
    open fun commandLineRunner() = CommandLineRunner {
        internalEventClasses.forEach { internalEventClass ->
            val topicName = internalEventClass.topicName()
            topicRepository.findGroupTopic(GroupId.root, topicName) ?: topicRepository.create(
                generateDomainId(::TopicId).let { topicId ->
                    TopicInternalCreateCmd(
                        id = CmdId(topicId),
                        topicId = topicId,
                        name = topicName,
                        logTopicId = generateDomainId(::LogTopicId)
                    )
                }
            )
        }

        createRoot(
            AccountCreateRootRequest(
                email = Email("root@hamal.io"),
                password = Password("toor")
            )
        )
    }

    @Autowired
    private lateinit var createRoot: AccountCreateRootPort

    @Autowired
    private lateinit var topicRepository: TopicRepository

    @Autowired
    private lateinit var generateDomainId: GenerateId
}


