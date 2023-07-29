package io.hamal.extension.std.sys

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ExtensionFactory
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.sdk.HttpTemplateSupplier

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ExtensionFactory {

    override fun create(): Extension {
        return Extension(
            name = "sys",
            functions = listOf(
                NamedFunctionValue(
                    name = "adhoc",
                    function = InvokeAdhocFunction(templateSupplier)
                ),
                NamedFunctionValue(
                    name = "list_execs",
                    function = ListExecsFunction(templateSupplier)
                ),
                NamedFunctionValue(
                    name = "get_exec",
                    function = GetExecFunction(templateSupplier)
                ),
                NamedFunctionValue(
                    name = "create_func",
                    function = CreateFuncFunction(templateSupplier)
                )
            )
        )
    }
}