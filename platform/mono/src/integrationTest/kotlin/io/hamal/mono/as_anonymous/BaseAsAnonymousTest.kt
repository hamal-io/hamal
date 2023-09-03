package io.hamal.mono.as_anonymous

import io.hamal.mono.BaseTest
import java.nio.file.Paths

abstract class BaseAsAnonymousTest : BaseTest() {
    override val testPath = Paths.get("src", "integrationTest", "resources", "as_anonymous")

    override fun setup() {
    }

}
