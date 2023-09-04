package io.hamal.mono.as_anonymous

import io.hamal.mono.BaseTest
import java.nio.file.Path
import java.nio.file.Paths

abstract class BaseAsAnonymousTest : BaseTest() {
    override val testPath: Path = Paths.get("src", "integrationTest", "resources", "as_anonymous")
}
