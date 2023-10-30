package io.hamal.testbed

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.*

@TestInstance(PER_CLASS)
internal object MemoryTest : BaseTest() {

    init {
        val properties = Properties()
        properties["HTTP_POLL_EVERY_MS"] = 1
        properties["API_HOST"] = "http://localhost:8040"
        properties["BRIDGE_HOST"] = "http://localhost:7040"

        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestConfig::class.java, TestRetryConfig::class.java)
            .profiles("test", "admin", "memory")
            .properties(properties)
            .bannerMode(Banner.Mode.OFF)
            .web(WebApplicationType.NONE)

        applicationBuilder.run().let {
            applicationBuilder
                .parent(it)
                .child(ApiConfig::class.java)
                .sources(ClearController::class.java)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=8040")
                .bannerMode(Banner.Mode.OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(BridgeConfig::class.java)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=7040")
                .bannerMode(Banner.Mode.OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(TestSandboxConfig::class.java, RunnerConfig::class.java)
                .properties(properties)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run()
        }
    }

    override val apiHttpTemplate = HttpTemplateImpl(
        baseUrl = "http://localhost:8040",
        headerFactory = {
            this["authorization"] = "Bearer root-token"
        }
    )
}