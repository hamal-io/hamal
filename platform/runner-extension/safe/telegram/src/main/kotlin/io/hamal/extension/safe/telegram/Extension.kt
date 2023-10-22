package io.hamal.extension.safe.telegram

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.safe.RunnerSafeExtension
import io.hamal.lib.kua.extension.safe.RunnerSafeExtensionFactory


object TelegramSafeFactory : RunnerSafeExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerSafeExtension {
        return RunnerSafeExtension(name = "telegram")
    }
}

