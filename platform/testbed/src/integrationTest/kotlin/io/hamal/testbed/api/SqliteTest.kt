package io.hamal.testbed.api

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.runner.RunnerConfig
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.*

internal object SqliteApiTest : BaseApiTest() {

    init {
        val properties = Properties()
        properties["HTTP_POLL_EVERY_MS"] = 1
        properties["API_HOST"] = "http://localhost:8041"
        properties["BRIDGE_HOST"] = "http://localhost:7041"

        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestConfig::class.java, TestRetryConfig::class.java)
            .profiles("test", "api", "sqlite")
            .properties(properties)
            .bannerMode(Banner.Mode.OFF)
            .web(WebApplicationType.NONE)

        applicationBuilder.run().let {
            applicationBuilder
                .parent(it)
                .child(ApiConfig::class.java)
                .sources(ClearController::class.java)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=8041")
                .bannerMode(Banner.Mode.OFF)
                .run()

            applicationBuilder
                .parent(it)
                .child(BridgeConfig::class.java)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=7041")
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

    override val sdk = ApiSdkImpl(
        apiHost = "http://localhost:8041",
        token = AuthToken("root-token")
    )
}