package io.hamal.testbed

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryExponentialTime
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.plugin.std.debug.DebugPluginFactory
import io.hamal.plugin.std.log.LogPluginFactory
import io.hamal.plugin.std.sys.SysPluginFactory
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.runner.RunnerConfig
import io.hamal.runner.config.SandboxFactory
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.env.Environment
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.name
import kotlin.time.Duration.Companion.milliseconds


//@SpringBootTest(
//    webEnvironment = DEFINED_PORT,
//    properties = [
//        "server.port=8042",
//        "io.hamal.runner.api.host=http://localhost:8042",
//        "io.hamal.runner.bridge.host=http://localhost:8042",
//        "io.hamal.runner.http.poll-every-ms=1"
//    ], classes = [
//        ApiTestConfig::class,
//        CoreConfig::class,
//        ApiConfig::class,
////        BridgeConfig::class,
////        RunnerConfig::class
//    ]
//)
//@DirtiesContext
//@DisplayName("admin - memory")
//@ActiveProfiles(value = ["test", "admin", "memory"])
//@ContextConfiguration
//@ExtendWith(SpringExtension::class)
//@ComponentScan


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

        val sdk = ApiSdkImpl(apiHost, token = AuthToken("root-token"))

        return Sandbox(ctx)
            .register(
                LogPluginFactory(sdk.execLog),
                DebugPluginFactory(),
                SysPluginFactory(sdk),
            )
    }
}


@TestConfiguration
class TestRetryConfig {
    @Bean
    fun delayRetry(): DelayRetry = DelayRetryExponentialTime(
        base = 1.milliseconds,
        maxBackOffTime = 1.milliseconds
    )
}


@RestController
class ClearController {
    @PostMapping("/v1/clear")
    fun clear() {
//        repeat(10) {
//        platformEventBrokerRepository.clear()
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
//        }


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
    lateinit var platformEventBrokerRepository: BrokerRepository

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


@TestConfiguration
class TestConfig {

    private lateinit var testAccount: Account
    private lateinit var testAccountAuthToken: AuthToken
    private lateinit var testGroup: Group
    private lateinit var testNamespace: Namespace

    @PostConstruct
    fun setup() {
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

@ExtendWith(SpringExtension::class)
@ComponentScan
internal class MemoryAdminTest {

//    @Autowired
//    lateinit var execRepository: ExecRepository

    init {
        val properties = Properties()
        properties["HTTP_POLL_EVERY_MS"] = 1


        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestConfig::class.java, TestRetryConfig::class.java)
            .properties(properties)
            .profiles("test", "admin", "memory")
            .banner { _: Environment, _: Class<*>, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
 _    _                       _   _       
 | |  | |                     | | (_)      
 | |__| | __ _ _ __ ___   __ _| |  _  ___  
 |  __  |/ _` | '_ ` _ \ / _` | | | |/ _ \ 
 | |  | | (_| | | | | | | (_| | |_| | (_) |
 |_|  |_|\__,_|_| |_| |_|\__,_|_(_)_|\___/ 
            """.trimIndent()
                )
                out.println("")
                out.println("")
            }
            .web(WebApplicationType.NONE)

        val parent: ConfigurableApplicationContext = applicationBuilder.run()
//
        applicationBuilder
            .parent(parent)
            .child(ApiConfig::class.java)
            .sources(ClearController::class.java)
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8008")
            .bannerMode(Banner.Mode.OFF)
            .run()

        applicationBuilder
            .parent(parent)
            .child(BridgeConfig::class.java)
            .web(WebApplicationType.SERVLET)
            .properties("server.port=7007")
            .bannerMode(Banner.Mode.OFF)
            .run()

        applicationBuilder
            .parent(parent)
            .child(TestSandboxConfig::class.java, RunnerConfig::class.java)
            .properties(properties)
            .web(WebApplicationType.NONE)
            .bannerMode(Banner.Mode.OFF)
            .run()
    }

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                DynamicTest.dynamicTest(testName) {
//                    setupTest()

                    HttpTemplateImpl(headerFactory = {
                        this["authorization"] = "Bearer root-token"
                    }).post("http://localhost:8008/v1/clear").execute()
//                    Thread.sleep(100)

                    var counter = 0
                    while (true) {
                        when (val result = runTest(testPath)) {
                            is BaseTest.TestResult.Success -> break
                            is BaseTest.TestResult.Failed -> {
                                if (counter++ >= 3) {
                                    fail { result.message }
                                }

//                                repeat(10) {
                                HttpTemplateImpl(
                                    headerFactory = {
                                        this["authorization"] = "Bearer root-token"
                                    }
                                ).post("http://localhost:8008/v1/clear").execute()
//                                    setupTest()
//                                    Thread.sleep(10)
//                                }
                            }

                            is BaseTest.TestResult.Timeout -> fail("Timeout")
                        }
                    }
                }
            }.toList()
//            .plus(DynamicTest.dynamicTest("hold") {
//                while (true) {
//
//                }
//            })
    }


    private fun runTest(testPath: Path): BaseTest.TestResult {

        val files = Files.walk(testPath)
            .filter { f: Path -> f.name.endsWith(".lua") }
            .sorted()

        for (file in files) {
            println(">>>>>>>>>>>>>> ${file.fileName}")

            val sdk = ApiSdkImpl(
                apiHost = "http://localhost:8008",
                token = AuthToken("root-token")
            )

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
//                        check(this is FailedExec)
                        return BaseTest.TestResult.Failed(message = "Execution failed: ${this.result!!.value["message"]}")
                    } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                        return BaseTest.TestResult.Timeout
                    }
                }
//                with(execRepository.get(execReq.execId)) {
//                    if (status == io.hamal.lib.domain.vo.ExecStatus.Completed) {
//                        wait = false
//                    } else if (status == io.hamal.lib.domain.vo.ExecStatus.Failed) {
//                        check(this is FailedExec)
//                        return io.hamal.testbed.BaseTest.TestResult.Failed(message = "Execution failed: ${this.result.value["message"]}")
//                    } else if (startedAt.plusSeconds(5).isBefore(io.hamal.lib.common.util.TimeUtils.now())) {
//                        return io.hamal.testbed.BaseTest.TestResult.Timeout
//                    }
//                }
            }
        }
        return BaseTest.TestResult.Success
    }

//
//    @Test
//    fun test() {
//
//        val sdk = ApiSdkImpl(
//            apiHost = "http://localhost:8008",
//            token = AuthToken("root-token")
//        )
//
//        sdk.func.create(
//            NamespaceId(1), ApiFuncCreateReq(
//                name = FuncName("test"),
//                inputs = FuncInputs(),
//                code = CodeValue("abc")
//            )
//        )
//        println(sdk.func.list(ApiFuncService.FuncQuery()))
//
//        val execReq = sdk.adhoc.invoke(
//            NamespaceId(1),
//            ApiAdhocInvokeReq(InvocationInputs(), CodeValue(String(Files.readAllBytes(file))))
//        )
//
//    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .map { it.parent }
        .distinct()
        .sorted()

    val testPath: Path = Paths.get("src", "integrationTest", "resources")


//    override val log: Logger = logger(this::class)
//    override val sdkProvider = withApiSdk(8042)
//    override val testPath: Path = Paths.get("src", "integrationTest", "resources")
}