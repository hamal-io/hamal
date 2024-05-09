package io.hamal.extension.std.sys

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory

object ExtensionStdSysFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(name = ValueString("std.sys"))
    }
}
