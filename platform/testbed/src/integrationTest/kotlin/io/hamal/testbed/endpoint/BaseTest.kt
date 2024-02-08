package io.hamal.testbed.endpoint

import AbstractRunnerTest
import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.component.SetupInternalTopics
import io.hamal.core.config.BackendBasePath
import io.hamal.core.service.InternalEventService
import io.hamal.extension.net.http.ExtensionHttpFactory
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.plugin.std.sys.PluginSysFactory
import io.hamal.repository.api.*
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
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

    @Bean
    fun envFactory(@Value("\${io.hamal.runner.api.host}") apiHost: String): EnvFactory =
        object : EnvFactory {
            override fun create() = RunnerEnv(
                HotObject.builder()
                    .set("api_host", apiHost)
                    .build()
            )
        }
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
            .registerPlugins(
                PluginLogFactory(sdk.execLog),
                PluginDebugFactory(),
                PluginSysFactory(sdk),
                PluginHttpFactory()
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
        accountRepository.clear()
        authRepository.clear()
        codeRepository.clear()
        endpointRepository.clear()
        extensionRepository.clear()
        reqRepository.clear()
        execRepository.clear()
        funcRepository.clear()
        groupRepository.clear()
        hookRepository.clear()

        flowRepository.clear()
        blueprintRepository.clear()
        triggerRepository.clear()

        testAccount = accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = AccountId.root,
                accountType = AccountType.Root,
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

        testFlow = flowRepository.create(
            FlowCmdRepository.CreateCmd(
                id = CmdId(5),
                flowId = FlowId.root,
                groupId = testGroup.id,
                name = FlowName("root-namespace"),
                inputs = FlowInputs()
            )
        )
    }

    @Autowired
    lateinit var blueprintRepository: BlueprintRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var endpointRepository: EndpointRepository

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
    lateinit var flowRepository: FlowRepository

    @Autowired
    lateinit var reqRepository: RequestRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateId

    @Autowired
    lateinit var logBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var topicRepository: TopicRepository

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics

    @Autowired
    lateinit var internalEvenService: InternalEventService

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testFlow: Flow
}


@TestConfiguration
class TestConfig {

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testFlow: Flow

    @PostConstruct
    fun setup() {
        topicRepository.clear()
        logBrokerRepository.clear()
        setupInternalTopics()
//        internalEvenService.reload()

        try {
            testAccount = accountRepository.create(
                AccountCmdRepository.CreateCmd(
                    id = CmdId(2),
                    accountId = AccountId.root,
                    accountType = AccountType.Root,
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

            testFlow = flowRepository.create(
                FlowCmdRepository.CreateCmd(
                    id = CmdId(5),
                    flowId = FlowId.root,
                    groupId = testGroup.id,
                    name = FlowName("root-namespace"),
                    inputs = FlowInputs()
                )
            )
        } catch (ignore: Throwable) {
        }
    }

    @Autowired
    lateinit var topicRepository: TopicRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var generateDomainId: GenerateId

    @Autowired
    lateinit var internalEvenService: InternalEventService

    @Autowired
    lateinit var logBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var flowRepository: FlowRepository
}


abstract class BaseEndpointTest : AbstractRunnerTest() {

    abstract val sdk: ApiSdkImpl

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    sdk.template.post("/v1/clear").execute()

                    createTestRunner(
                        pluginFactories = listOf(
                            PluginSysFactory(sdk),
                            PluginHttpFactory()
                        ),
                        extensionFactories = listOf(
                            ExtensionHttpFactory
                        ),
                        env = RunnerEnv(
                            HotObject.builder()
                                .set("api_host", sdk.template.baseUrl)
                                .build()
                        )
                    ).run(
                        unitOfWork(
                            code = String(Files.readAllBytes(testPath)),
                        )
                    )

                }
            }.toList()
    }


    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .sorted()

    private val testPath: Path = Paths.get("src", "integrationTest", "resources", "endpoint")
}