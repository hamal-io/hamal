package io.hamal.extension.std.sys

import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.HttpTemplateSupplier

class DepSysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : NativeExtensionFactory {

    override fun create(): NativeExtension {
        return NativeExtension(
            name = "sys",
            values = mapOf(
                "adhoc" to InvokeAdhocFunction(templateSupplier),
                "list_execs" to ListExecsFunction(templateSupplier),
                "get_exec" to GetExecFunction(templateSupplier),
                "create_func" to CreateFuncFunction(templateSupplier)
            )
        )
    }
}

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "sys",
            internals = mapOf(),
            init = ""
        )
    }
}