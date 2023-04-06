package io.hamal.application.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

@Configuration
open class DatabaseConfig {

    @Bean
    open fun dataSource(): DataSource {
        val result = HikariDataSource()
        result.maximumPoolSize = 12
        result.driverClassName = "org.h2.Driver"
        result.jdbcUrl = "jdbc:h2:file:/tmp/data"
        result.username = "root"
        result.password = "toor"
        result.isAutoCommit = true
        return result
    }

    @Bean
    open fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        val transactionManager = DataSourceTransactionManager()
        transactionManager.dataSource = dataSource
        return transactionManager
    }

    @Bean
    open fun transactionTemplate(txManager: PlatformTransactionManager): TransactionTemplate {
        val result = TransactionTemplate(txManager)
        result.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
        result.isolationLevel = TransactionDefinition.ISOLATION_DEFAULT
        return result
    }

    @Bean
    open fun namedParameterJdbcOperations(dataSource: DataSource): NamedParameterJdbcOperations {
        return NamedParameterJdbcTemplate(dataSource)
    }
}