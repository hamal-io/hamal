package io.hamal.mono.as_group_admin

import io.hamal.mono.BaseTest
import java.nio.file.Path
import java.nio.file.Paths

abstract class BaseAsGroupAdminTest : BaseTest() {
    override val testPath: Path = Paths.get("src", "integrationTest", "resources", "as_group_admin")
}
