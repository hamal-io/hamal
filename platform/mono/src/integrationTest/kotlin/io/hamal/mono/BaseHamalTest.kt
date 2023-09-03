package io.hamal.mono

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.HubSdk
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.temporal.ChronoUnit.DAYS
import kotlin.io.path.name

abstract class BaseHamalTest {

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var accountCmdRepository: AccountCmdRepository

    @Autowired
    lateinit var authCmdRepository: AuthCmdRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var groupCmdRepository: GroupCmdRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    lateinit var rootAccount: Account
    lateinit var rootAccountAuthToken: AuthToken

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}") {
                setupTestEnv()

                val execReq = rootHubSdk.adhocService.submit(
                    InvokeAdhocReq(
                        InvocationInputs(),
                        CodeType(String(Files.readAllBytes(testFile)))
                    )
                )
                rootHubSdk.awaitService.await(execReq)

                var wait = true
                val startedAt = TimeUtils.now()
                while (wait) {
                    sleep(1)
                    with(execQueryRepository.get(execReq.id(::ExecId))) {
                        if (status == ExecStatus.Completed) {
                            wait = false
                        }
                        if (status == ExecStatus.Failed) {
                            fail { "Execution failed" }
                        }

                        if (startedAt.plusSeconds(1).isBefore(TimeUtils.now())) {
                            fail("Timeout")
                        }
                    }
                }
            }
        }.toList()
    }

    private fun setupTestEnv() {
        eventBrokerRepository.clear()

        accountCmdRepository.clear()
        authCmdRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        groupCmdRepository.clear()
        namespaceCmdRepository.clear()
        triggerCmdRepository.clear()


        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = generateDomainId(::NamespaceId),
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )

        rootAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                name = AccountName("test-root"),
                email = AccountEmail("test@hamal.io"),
                salt = PasswordSalt("test-salt")
            )
        )

        rootAccountAuthToken = (authCmdRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = rootAccount.id,
                token = AuthToken("test-root-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, DAYS))
            )
        ) as TokenAuth).token

    }

    abstract val rootHttpTemplate: HttpTemplate
    abstract val rootHubSdk: HubSdk
}


private val testPath = Paths.get("src", "integrationTest", "resources", "as_root")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }