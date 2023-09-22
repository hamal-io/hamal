package io.hamal.mono.api

import io.hamal.lib.common.Logger
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.hub.HubInvokeAdhocReq
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Files
import java.nio.file.Path
import java.time.temporal.ChronoUnit.DAYS
import kotlin.io.path.name

abstract class BaseApiTest {

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
    lateinit var metricRepository: MetricRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    private lateinit var testAdminAccount: Account
    private lateinit var testAdminAuthToken: AuthToken
    private lateinit var testAdminGroup: Group

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->

            val testFileWithPath = "${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}"
            dynamicTest(testFileWithPath) {
                log.info("Start test $testFileWithPath")
                setupTestEnv()

                val execReq = rootHubSdk.adhoc.invoke(
                    testAdminGroup.id,
                    HubInvokeAdhocReq(
                        InvocationInputs(),
                        CodeType(String(Files.readAllBytes(testFile)))
                    )
                )
                rootHubSdk.await(execReq)

                var wait = true
                val startedAt = TimeUtils.now()
                while (wait) {
                    Thread.sleep(1)
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
        metricRepository.clear()
        namespaceCmdRepository.clear()
        triggerCmdRepository.clear()

        testAdminAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                accountType = Root,
                name = AccountName("test-account"),
                email = AccountEmail("test-account@hamal.io"),
                salt = PasswordSalt("test-salt")
            )
        )

        testAdminAuthToken = (authCmdRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAdminAccount.id,
                token = AuthToken("test-root-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, DAYS))
            )
        ) as TokenAuth).token

        testAdminGroup = groupCmdRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = generateDomainId(::GroupId),
                name = GroupName("test-group"),
                creatorId = testAdminAccount.id
            )
        )

        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = generateDomainId(::NamespaceId),
                groupId = testAdminGroup.id,
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )
    }

    abstract val rootHubSdk: HubSdk
    abstract val testPath: Path
    abstract val log: Logger

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }

    fun rootHubSdk(serverPort: Number) = DefaultHubSdk(
        HttpTemplate(
            baseUrl = "http://localhost:$serverPort",
            headerFactory = {
                set("x-hamal-token", "test-root-token")
            }
        )
    )
}
