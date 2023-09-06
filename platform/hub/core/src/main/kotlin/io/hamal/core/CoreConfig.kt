package io.hamal.core

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.req.CreateRootAccountReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.hub.HubCreateNamespaceReq
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

    @Autowired
    private lateinit var submitRequest: SubmitRequest

    @Bean
    open fun commandLineRunner() = CommandLineRunner {
        val acctReq = submitRequest(
            CreateRootAccountReq(
                AccountName("root"),
                AccountEmail("root@hamal.io"),
                Password("toor")
            )
        )
        submitRequest(acctReq.groupId, HubCreateNamespaceReq(NamespaceName("hamal"), NamespaceInputs()))
    }
}


