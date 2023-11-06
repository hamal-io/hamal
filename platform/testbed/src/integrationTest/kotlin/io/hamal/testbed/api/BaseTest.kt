package io.hamal.testbed.api

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.config.BackendBasePath
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.plugin.net.http.HttpPluginFactory
import io.hamal.plugin.std.debug.DebugPluginFactory
import io.hamal.plugin.std.log.LogPluginFactory
import io.hamal.plugin.std.sys.SysPluginFactory
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.runner.config.SandboxFactory
import io.hamal.testbed.api.TestResult.*
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.name
import kotlin.time.Duration.Companion.milliseconds


@TestConfiguration
class TestSandboxConfig {
    @Bean
    fun sandboxFactory(@Value("\${io.hamal.runner.api.host}") apiHost: String): SandboxFactory =
        TestRunnerSandboxFactory(apiHost)
}


class TestRunnerSandboxFactory(
    private val apiHost: String,
) : SandboxFactory {
    override fun create(ctx: SandboxContext): Sandbox {
        NativeLoader.load(NativeLoader.Preference.Jar)

        val sdk = ApiSdkImpl(
            apiHost,
            token = AuthToken("root-token")
        )

        return Sandbox(ctx)
            .register(
                LogPluginFactory(sdk.execLog),
                DebugPluginFactory(),
                SysPluginFactory(sdk),
                HttpPluginFactory()
            )
    }
}


@TestConfiguration
class TestRetryConfig {
    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/testbed/${UUID.randomUUID()}")

    @Bean
    fun delayRetry(): DelayRetry = DelayRetryFixedTime(1.milliseconds)
}


@RestController
class ClearController {

    @PostMapping("/v1/clear")
    fun clear() {
        eventBrokerRepository.clear()
        accountRepository.clear()
        authRepository.clear()
        codeRepository.clear()
        extensionRepository.clear()
        reqRepository.clear()
        execRepository.clear()
        funcRepository.clear()
        groupRepository.clear()
        hookRepository.clear()
        namespaceRepository.clear()
        blueprintRepository.clear()
        triggerRepository.clear()

        testAccount = accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = AccountId.root,
                accountType = AccountType.Root,
                name = AccountName("root"),
                email = AccountEmail("root@hamal.io"),
                salt = PasswordSalt("root-salt")
            )
        )

        testAccountAuthToken = (authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("root-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        ) as TokenAuth).token

        testGroup = groupRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = GroupId.root,
                name = GroupName("root-group"),
                creatorId = testAccount.id
            )
        )

        testNamespace = namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(5),
                namespaceId = NamespaceId.root,
                groupId = testGroup.id,
                name = NamespaceName("root-namespace"),
                inputs = NamespaceInputs()
            )
        )
    }

    @Autowired
    lateinit var blueprintRepository: BlueprintRepository

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var execRepository: ExecRepository

    @Autowired
    lateinit var extensionRepository: ExtensionRepository

    @Autowired
    lateinit var funcRepository: FuncRepository

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var hookRepository: HookRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var reqRepository: ReqRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testNamespace: Namespace
}


sealed interface TestResult {
    object Success : TestResult
    data class Failed(val message: String) : TestResult
    object Timeout : TestResult
}


@TestConfiguration
class TestConfig {

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testNamespace: Namespace

    @PostConstruct
    fun setup() {

        try {
            testAccount = accountRepository.create(
                AccountCmdRepository.CreateCmd(
                    id = CmdId(2),
                    accountId = AccountId.root,
                    accountType = AccountType.Root,
                    name = AccountName("root"),
                    email = AccountEmail("root@hamal.io"),
                    salt = PasswordSalt("root-salt")
                )
            )

            testAccountAuthToken = (authRepository.create(
                AuthCmdRepository.CreateTokenAuthCmd(
                    id = CmdId(3),
                    authId = AuthId(1),
                    accountId = testAccount.id,
                    token = AuthToken("root-token"),
                    expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
                )
            ) as TokenAuth).token

            testGroup = groupRepository.create(
                GroupCmdRepository.CreateCmd(
                    id = CmdId(4),
                    groupId = GroupId.root,
                    name = GroupName("root-group"),
                    creatorId = testAccount.id
                )
            )

            testNamespace = namespaceRepository.create(
                NamespaceCmdRepository.CreateCmd(
                    id = CmdId(5),
                    namespaceId = NamespaceId.root,
                    groupId = testGroup.id,
                    name = NamespaceName("root-namespace"),
                    inputs = NamespaceInputs()
                )
            )
        } catch (t: Throwable) {
        }
    }

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository
}


abstract class BaseApiTest {

    abstract val apiHttpTemplate: HttpTemplate

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                DynamicTest.dynamicTest(testName) {

                    apiHttpTemplate.post("/v1/clear").execute()

                    var counter = 0
                    while (true) {
                        when (val result = runTest(testPath)) {
                            is Success -> break
                            is Failed -> {
                                if (counter++ >= 3) {
                                    fail { result.message }
                                }
                                apiHttpTemplate.post("/v1/clear").execute()
                            }

                            is Timeout -> fail("Timeout")
                        }
                    }
                }
            }.toList()
    }


    private fun runTest(testPath: Path): TestResult {

        val files = Files.walk(testPath)
            .filter { f: Path -> f.name.endsWith(".lua") }
            .sorted()

        for (file in files) {
            println(">>>>>>>>>>>>>> ${file.fileName}")

            val sdk = ApiSdkImpl(apiHttpTemplate)

            val execReq = sdk.adhoc.invoke(
                NamespaceId(1),
                ApiAdhocInvokeReq(InvocationInputs(), CodeValue(String(Files.readAllBytes(file))))
            )

            sdk.await(execReq)

            var wait = true
            val startedAt = TimeUtils.now()


            while (wait) {
                with(sdk.exec.get(execReq.execId)) {
                    if (status == io.hamal.lib.domain.vo.ExecStatus.Completed) {
                        wait = false
                    } else if (status == io.hamal.lib.domain.vo.ExecStatus.Failed) {
                        return Failed(message = "Execution failed: ${this.result!!.value["message"]}")
                    } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                        return Timeout
                    }
                }
            }
        }
        return Success
    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .map { it.parent }
        .distinct()
        .sorted()

    private val testPath: Path = Paths.get("src", "integrationTest", "resources", "api")
}