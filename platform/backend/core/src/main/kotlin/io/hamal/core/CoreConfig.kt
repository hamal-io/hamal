package io.hamal.core

import io.hamal.core.adapter.account.AccountCreatePort
import io.hamal.core.adapter.account.AccountCreateRootPort
import io.hamal.core.component.SetupInternalTopics
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.request.AccountCreateRequest
import io.hamal.lib.domain.request.AccountCreateRootRequest
import io.hamal.lib.domain.vo.Email.Companion.Email
import io.hamal.lib.domain.vo.Password.Companion.Password
import io.hamal.repository.api.Auth
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
    open fun setupInternalTopicsRunner() = CommandLineRunner {
        setupInternalTopics()
    }

    @Bean
    @Profile("!test")
    open fun commandLineRunner() = CommandLineRunner {
        SecurityContext.with(Auth.System) {
            createRoot(
                AccountCreateRootRequest(
                    email = Email("root@hamal.io"),
                    password = Password("toor")
                )
            )

            createAccount(
                object : AccountCreateRequest {
                    override val email = Email("admin@hamal.io")
                    override val password = Password("lamah")
                }
            )
        }
    }

    @Autowired
    private lateinit var createRoot: AccountCreateRootPort

    @Autowired
    private lateinit var createAccount: AccountCreatePort

    @Autowired
    private lateinit var setupInternalTopics: SetupInternalTopics
}


