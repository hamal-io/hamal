package io.hamal.extension.web3.arbitrum

import com.google.gson.Gson
import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.lib.kua.NativeLoader
import io.hamal.plugin.web3.evm.evm.PluginWeb3EvmFactory
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import javax.xml.transform.Source
import kotlin.io.path.name

@SpringBootApplication
internal open class TestProxy

@TestConfiguration
internal open class TestWebConfig : WebMvcConfigurer {

    @Bean
    open fun gsonJson(): Gson = Serde.json()
        .register(SerdeModuleValueJson)
        .register(SerdeModuleValueVariable)
        .gson

    @Bean
    open fun httpMessageJsonConverter(gson: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gson
        result.defaultCharset = StandardCharsets.UTF_8
        result.supportedMediaTypes = listOf(
            MediaType("application", "json", StandardCharsets.UTF_8)
        )
        return result
    }


    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(SourceHttpMessageConverter<Source>())
        converters.add(httpMessageJsonConverter(gsonJson()))
    }
}

@RestController
internal class TestEvmController {
    @PostMapping("/arbitrum")
    fun handle(
        @RequestBody requests: JsonArray
    ): ResponseEntity<JsonArray> {
        return ResponseEntity.ok(TestHandler.handle(requests))
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [TestProxy::class, TestEvmController::class, TestWebConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
internal class ExtensionWeb3ArbitrumTest {

    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(NativeLoader.Preference.Resources)
        return testPaths()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) { runTest(testPath) }
            }.toList()
    }

    private fun runTest(testPath: Path) {

        val files = Files.walk(testPath)
            .filter { f: Path -> f.name.endsWith(".lua") }
            .sorted()

        for (file in files) {

            createTestRunner(
                pluginFactories = listOf(PluginWeb3EvmFactory()),
                extensionFactories = listOf(ExtensionStdTableFactory, ExtensionWeb3ArbitrumFactory),
                env = RunnerEnv(
                    ValueObject.builder()
                        .set("test_url", "http://localhost:$localPort/arbitrum")
                        .build()
                )
            ).also { runner ->
                runner.run(unitOfWork(String(Files.readAllBytes(file))))
            }
        }
    }

    @LocalServerPort
    lateinit var localPort: Number
}

private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
    .dropWhile { it != "resources" }
    .drop(2)
    .joinToString("/")

private fun testPaths(): Stream<Path> = Files.walk(testPath)
    .filter { f: Path -> f.name.endsWith(".lua") }
    .distinct()
    .sorted()


private val testPath = Paths.get("src", "integrationTest", "resources", "integration_test_suite")