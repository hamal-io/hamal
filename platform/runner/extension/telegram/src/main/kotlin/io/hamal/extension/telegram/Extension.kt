package io.hamal.extension.telegram

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.type.KuaString


object ExtensionTelegramFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(name = KuaString("telegram"))
    }
}

