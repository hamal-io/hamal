package io.hamal.testbed.api

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.runner.RunnerConfig
import org.springframework.boot.Banner.Mode.OFF
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.*

internal object MemoryApiTest : BaseApiTest() {

    init {
        val properties = Properties()
        properties["HTTP_POLL_EVERY_MS"] = 1
        properties["API_HOST"] = "http://localhost:8040"
        properties["BRIDGE_HOST"] = "http://localhost:7040"

        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestConfig::class.java, TestRetryConfig::class.java)
            .profiles("test", "api", "integration-test", "memory")
            .properties(properties)
            .bannerMode(OFF)
            .web(NONE)

        applicationBuilder.run().let {
            applicationBuilder
                .parent(it)
                .child(ApiConfig::class.java)
                .sources(ClearController::class.java)
                .web(SERVLET)
                .properties("server.port=8040")
                .bannerMode(OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(BridgeConfig::class.java)
                .web(SERVLET)
                .properties("server.port=7040")
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

    override val sdk = ApiSdkImpl(
        apiHost = "http://localhost:8040",
        token = AuthToken("root-token")
    )
}