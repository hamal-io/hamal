package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox


fun main() {
    ResourceLoader.load()
    val template = HttpTemplate("http://localhost:8084")
    val sandbox = Sandbox()

    sandbox.register(SysExtensionFactory { template }.create())

    sandbox.runCode(
        """
        local result = sys.adhoc({})
        print(result)
    """.trimIndent()
    )
}