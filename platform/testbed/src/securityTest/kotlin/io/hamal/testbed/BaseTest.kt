package io.hamal.testbed

import io.hamal.lib.common.Logger
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.name

abstract class BaseTest {

    sealed interface TestResult {
        object Success : TestResult
        data class Failed(val message: String) : TestResult
        object Timeout : TestResult
    }

    protected abstract fun setupTest()

    @TestFactory
    fun run(): List<DynamicTest> {
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    setupTest()

                    var counter = 0
                    while (true) {
                        when (val result = runTest(testPath)) {
                            is TestResult.Success -> break
                            is TestResult.Failed -> {
                                if (counter++ >= 3) {
                                    fail { result.message }
                                }

                                repeat(10) {
                                    setupTest()
                                    Thread.sleep(10)
                                }
                            }

                            is TestResult.Timeout -> fail("Timeout")
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
            val execReq = sdk.adhoc.invoke(
                testNamespace.id,
                ApiAdhocInvokeReq(InvocationInputs(), CodeValue(String(Files.readAllBytes(file))))
            )

            sdk.await(execReq)

            var wait = true
            val startedAt = TimeUtils.now()
            while (wait) {
                with(execRepository.get(execReq.execId)) {
                    if (status == ExecStatus.Completed) {
                        wait = false
                    } else if (status == ExecStatus.Failed) {
                        check(this is FailedExec)
                        return TestResult.Failed(message = "Execution failed: ${this.result.value["message"]}")
                    } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                        return TestResult.Timeout
                    }
                }
            }
        }
        return TestResult.Success
    }


    protected fun withApiSdk(serverPort: Number) = ApiSdkImpl("http://localhost:$serverPort")

    protected fun clearRepository() {
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

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var blueprintRepository: BlueprintRepository

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

    abstract val sdk: ApiSdk
    abstract val testPath: Path
    abstract val log: Logger

    lateinit var testAccount: Account
    lateinit var testAccountAuthToken: AuthToken
    lateinit var testGroup: Group
    lateinit var testNamespace: Namespace

}