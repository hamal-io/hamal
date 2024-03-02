package io.hamal.extension.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.type.KuaString

object ExtensionHttpFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = KuaString("net.http")
        )
    }
}