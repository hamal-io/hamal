package io.hamal.mono.as_root

import io.hamal.mono.BaseTest
import java.nio.file.Paths

abstract class BaseAsRootTest : BaseTest() {
    override val testPath = Paths.get("src", "integrationTest", "resources", "as_root")
}
