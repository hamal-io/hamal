package io.hamal.testbed.api.unauthenticated

import io.hamal.api.ApiConfig
import io.hamal.core.CoreConfig
import io.hamal.testbed.api.TestApiConfig
import io.hamal.testbed.api.TestSetupConfig
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.Banner.Mode.OFF
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.builder.SpringApplicationBuilder

@TestInstance(PER_CLASS)
internal object ApiMiscSqliteTest : BaseApiMiscTest(
    "http://localhost:9050"
) {
    init {
        val applicationBuilder = SpringApplicationBuilder()
            .parent(CoreConfig::class.java, TestApiConfig::class.java)
            .profiles("test", "api", "misc", "sqlite")
            .bannerMode(OFF)
            .web(NONE)

        applicationBuilder.run().let {
            applicationBuilder
                .parent(it)
                .child(ApiConfig::class.java, TestSetupConfig::class.java)
                .web(SERVLET)
                .properties("server.port=9050")
                .bannerMode(OFF)
                .run()
        }
    }
}