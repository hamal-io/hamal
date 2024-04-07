package com.nyanbot.config

import com.nyanbot.repository.impl.account.AccountSqliteRepository
import com.nyanbot.repository.impl.auth.AuthSqliteRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
open class RepositoryConfig(basePath: NyanBotBasePath) {

    @Bean
    open fun accountRepository() = AccountSqliteRepository(path)

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = AuthSqliteRepository(path)

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    private val path = Path(basePath.value)
}