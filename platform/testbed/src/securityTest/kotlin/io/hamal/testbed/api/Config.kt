package io.hamal.testbed.api

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.component.SetupInternalTopics
import io.hamal.core.config.BackendBasePath
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@TestConfiguration
class TestApiConfig {

    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/testbed/${UUID.randomUUID()}")

    @Bean
    fun delayRetry(): DelayRetry = DelayRetryFixedTime(1.milliseconds)
}

@TestConfiguration
class TestSetupConfig {

    @PostConstruct
    fun setup() {
        try {
            setupInternalTopics()

            setupUser(1) // test user & resources which other users try to access
            setupAnonymous(2)
            setupUser(3)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun setupAnonymous(id: Int) {
        accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(id + 2),
                accountId = AccountId(id),
                accountType = AccountType.Anonymous,
                salt = PasswordSalt("$id-salt")
            )
        )

        authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(id + 3),
                authId = AuthId(id),
                accountId = AccountId(id),
                token = AuthToken("$id-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        )
    }


    private fun setupUser(id: Int) {
        accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(id + 2),
                accountId = AccountId(id),
                accountType = AccountType.User,
                salt = PasswordSalt("$id-salt")
            )
        )

        authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(id + 3),
                authId = AuthId(id),
                accountId = AccountId(id),
                token = AuthToken("$id-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        )

        workspaceRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = CmdId(id + 4),
                workspaceId = WorkspaceId(id),
                name = WorkspaceName("$id-workspace"),
                creatorId = AccountId(id)
            )
        )

        namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(id + 5),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                name = NamespaceName("$id-namespace")
            )
        )

        codeRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(id + 6),
                codeId = CodeId(id),
                value = CodeValue(""),
                workspaceId = WorkspaceId(id)
            )
        )

        funcRepository.create(
            FuncCmdRepository.CreateCmd(
                id = CmdId(id + 7),
                funcId = FuncId(id),
                name = FuncName("$id-func"),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = FuncInputs(),
                codeId = CodeId(id),
                codeVersion = CodeVersion(1)
            )
        )
    }

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var funcRepository: FuncRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics
}
