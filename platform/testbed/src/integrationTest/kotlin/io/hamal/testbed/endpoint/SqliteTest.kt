package io.hamal.testbed.endpoint

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.Banner.Mode.OFF
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.*


@TestInstance(PER_CLASS)
internal object SqliteEndpointTest : BaseEndpointTest() {

    init {
        val properties = Properties()
        properties["HTTP_POLL_EVERY_MS"] = 1
        properties["API_HOST"] = "http://localhost:8046"
        properties["BRIDGE_HOST"] = "http://localhost:7046"

        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestConfig::class.java, TestRetryConfig::class.java)
            .profiles("test", "endpoint", "sqlite")
            .properties(properties)
            .bannerMode(OFF)
            .web(NONE)

        applicationBuilder.run().let {
            applicationBuilder
                .parent(it)
                .child(ApiConfig::class.java)
                .sources(ClearController::class.java)
                .web(SERVLET)
                .properties("server.port=8046")
                .bannerMode(OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(BridgeConfig::class.java)
                .web(SERVLET)
                .properties("server.port=7046")
                .bannerMode(OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(TestSandboxConfig::class.java, RunnerConfig::class.java)
                .properties(properties)
                .web(NONE)
                .bannerMode(OFF)
                .run()
        }
    }

    override val apiHttpTemplate = HttpTemplateImpl(
        baseUrl = "http://localhost:8046",
        headerFactory = {
            this["authorization"] = "Bearer root-token"
        }
    )
}